package gt.uvg.pmproject.data

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import gt.uvg.pmproject.model.EventType
import gt.uvg.pmproject.model.Quotation
import gt.uvg.pmproject.model.QuotationStatus
import gt.uvg.pmproject.model.Service
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle


private const val COLLECTION_QUOTATIONS = "quotations"
private const val FIELD_CLIENT = "client"
private const val FIELD_EVENT_TYPE = "eventType"
private const val FIELD_DATE = "date"
private const val FIELD_STATUS = "status"
private const val FIELD_NOTES = "notes"
private const val FIELD_UPDATED_AT = "updatedAt"
private const val FIELD_SERVICES = "services"
private const val SERVICE_ID = "id"
private const val SERVICE_NAME = "name"
private const val SERVICE_PROVIDER = "provider"
private const val SERVICE_QUANTITY = "quantity"
private const val SERVICE_UNIT_PRICE = "unitPrice"
private const val READ_TIMEOUT_MS = 6_000L
private const val WRITE_TIMEOUT_MS = 5_000L

private val legacyDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT)

class FirestoreQuotationRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : QuotationRepository {

    private val collection get() = firestore.collection(COLLECTION_QUOTATIONS)

    override fun observeQuotations(): Flow<QuotationsSnapshot> = callbackFlow {
        val registration = collection
            .orderBy(FIELD_UPDATED_AT, Query.Direction.DESCENDING)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(
                        QuotationsSnapshot(
                            quotations = snapshot.documents.mapNotNull { it.toQuotation() },
                            isFromCache = snapshot.metadata.isFromCache
                        )
                    )
                }
            }
        awaitClose { registration.remove() }
    }

    override suspend fun getQuotation(quotationId: String): Quotation? {
        val reference = collection.document(quotationId)
        val document = withTimeoutOrNull(READ_TIMEOUT_MS) { reference.getOrNull(Source.DEFAULT) }
            ?: reference.getOrNull(Source.CACHE)
            ?: throw DataUnavailableException()
        return document.toQuotation()
    }

    private suspend fun DocumentReference.getOrNull(source: Source): DocumentSnapshot? =
        try {
            get(source).await()
        } catch (exception: FirebaseFirestoreException) {
            null
        }

    override suspend fun saveQuotation(quotation: Quotation): SaveOutcome {
        val document = if (quotation.id.isBlank()) {
            collection.document()
        } else {
            collection.document(quotation.id)
        }
        val task = document.set(quotation.toMap())
        val completed = withTimeoutOrNull(WRITE_TIMEOUT_MS) { task.await() }
        return SaveOutcome(quotationId = document.id, isPending = completed == null)
    }

    override suspend fun updateStatus(
        quotationId: String,
        status: QuotationStatus
    ): SaveOutcome {
        val task = collection.document(quotationId).update(
            mapOf(
                FIELD_STATUS to status.name,
                FIELD_UPDATED_AT to System.currentTimeMillis()
            )
        )
        val completed = withTimeoutOrNull(WRITE_TIMEOUT_MS) { task.await() }
        return SaveOutcome(quotationId = quotationId, isPending = completed == null)
    }

    private fun DocumentSnapshot.toQuotation(): Quotation? {
        if (!exists()) return null
        val rawServices = get(FIELD_SERVICES) as? List<*> ?: emptyList<Any>()
        val services = rawServices.mapNotNull { raw ->
            val map = raw as? Map<*, *> ?: return@mapNotNull null
            Service(
                id = map[SERVICE_ID] as? String ?: "",
                name = map[SERVICE_NAME] as? String ?: "",
                provider = map[SERVICE_PROVIDER] as? String ?: "",
                quantity = (map[SERVICE_QUANTITY] as? Number)?.toInt() ?: 0,
                unitPrice = (map[SERVICE_UNIT_PRICE] as? Number)?.toDouble() ?: 0.0
            )
        }
        val date = parseDate(getString(FIELD_DATE)) ?: return null
        val status = getString(FIELD_STATUS)
            ?.let { name -> QuotationStatus.entries.firstOrNull { it.name == name } }
            ?: QuotationStatus.DRAFT
        return Quotation(
            id = id,
            client = getString(FIELD_CLIENT) ?: "",
            eventType = EventType.fromName(getString(FIELD_EVENT_TYPE)),
            date = date,
            services = services,
            status = status,
            notes = getString(FIELD_NOTES) ?: "",
            updatedAt = getLong(FIELD_UPDATED_AT) ?: 0L
        )
    }

    private fun parseDate(raw: String?): LocalDate? {
        if (raw.isNullOrBlank()) return null
        return runCatching { LocalDate.parse(raw) }.getOrNull()
            ?: runCatching { LocalDate.parse(raw, legacyDateFormatter) }.getOrNull()
    }

    private fun Quotation.toMap(): Map<String, Any> = mapOf(
        FIELD_CLIENT to client,
        FIELD_EVENT_TYPE to eventType.name,
        FIELD_DATE to date.toString(),
        FIELD_STATUS to status.name,
        FIELD_NOTES to notes,
        FIELD_UPDATED_AT to System.currentTimeMillis(),
        FIELD_SERVICES to services.map { service ->
            mapOf(
                SERVICE_ID to service.id,
                SERVICE_NAME to service.name,
                SERVICE_PROVIDER to service.provider,
                SERVICE_QUANTITY to service.quantity,
                SERVICE_UNIT_PRICE to service.unitPrice
            )
        }
    )
}
