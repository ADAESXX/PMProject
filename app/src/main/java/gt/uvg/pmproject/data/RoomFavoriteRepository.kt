package gt.uvg.pmproject.data

import com.google.firebase.crashlytics.FirebaseCrashlytics
import gt.uvg.pmproject.data.local.FavoriteDao
import gt.uvg.pmproject.data.local.FavoriteEntity
import gt.uvg.pmproject.model.EventType
import gt.uvg.pmproject.model.FavoriteQuotation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class RoomFavoriteRepository(
    private val favoriteDao: FavoriteDao,
    private val crashlytics: FirebaseCrashlytics = FirebaseCrashlytics.getInstance()
) : FavoriteRepository {

    override fun observeFavorites(): Flow<List<FavoriteQuotation>> =
        favoriteDao.observeAll()
            .map { entities -> entities.map { it.toDomain() } }
            .catch { exception ->
                reportError(exception, "observeFavorites: falló la lectura de favoritos")
                throw exception
            }

    override fun observeIsFavorite(quotationId: String): Flow<Boolean> =
        favoriteDao.observeExists(quotationId)
            .catch { exception ->
                reportError(exception, "observeIsFavorite: falló la lectura de $quotationId")
                throw exception
            }

    override suspend fun addFavorite(favorite: FavoriteQuotation) =
        recordingErrors("addFavorite: falló al guardar ${favorite.quotationId}") {
            favoriteDao.insert(
                FavoriteEntity(
                    quotationId = favorite.quotationId,
                    client = favorite.client,
                    eventType = favorite.eventType.name,
                    amount = favorite.amount
                )
            )
        }

    override suspend fun updateFavorite(favorite: FavoriteQuotation) =
        recordingErrors("updateFavorite: falló al actualizar ${favorite.quotationId}") {
            favoriteDao.update(
                quotationId = favorite.quotationId,
                client = favorite.client,
                eventType = favorite.eventType.name,
                amount = favorite.amount
            )
        }

    override suspend fun removeFavorite(quotationId: String) =
        recordingErrors("removeFavorite: falló al eliminar $quotationId") {
            favoriteDao.deleteById(quotationId)
        }

    /**
     * Ejecuta [block]; si Room lanza una excepción, la registra en Crashlytics como
     * no fatal y la vuelve a lanzar para que el ViewModel decida qué mostrar.
     */
    private suspend fun recordingErrors(context: String, block: suspend () -> Unit) {
        try {
            block()
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (exception: Exception) {
            reportError(exception, context)
            throw exception
        }
    }

    private fun reportError(exception: Throwable, context: String) {
        crashlytics.log(context)
        crashlytics.recordException(exception)
    }

    private fun FavoriteEntity.toDomain() = FavoriteQuotation(
        quotationId = quotationId,
        client = client,
        eventType = EventType.fromName(eventType),
        amount = amount
    )
}
