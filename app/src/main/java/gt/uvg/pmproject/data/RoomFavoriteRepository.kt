package gt.uvg.pmproject.data

import gt.uvg.pmproject.data.local.FavoriteDao
import gt.uvg.pmproject.data.local.FavoriteEntity
import gt.uvg.pmproject.model.EventType
import gt.uvg.pmproject.model.FavoriteQuotation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomFavoriteRepository(
    private val favoriteDao: FavoriteDao
) : FavoriteRepository {

    override fun observeFavorites(): Flow<List<FavoriteQuotation>> =
        favoriteDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun observeIsFavorite(quotationId: String): Flow<Boolean> =
        favoriteDao.observeExists(quotationId)

    override suspend fun addFavorite(favorite: FavoriteQuotation) {
        favoriteDao.insert(
            FavoriteEntity(
                quotationId = favorite.quotationId,
                client = favorite.client,
                eventType = favorite.eventType.name,
                amount = favorite.amount
            )
        )
    }

    override suspend fun updateFavorite(favorite: FavoriteQuotation) {
        favoriteDao.update(
            quotationId = favorite.quotationId,
            client = favorite.client,
            eventType = favorite.eventType.name,
            amount = favorite.amount
        )
    }

    override suspend fun removeFavorite(quotationId: String) {
        favoriteDao.deleteById(quotationId)
    }

    private fun FavoriteEntity.toDomain() = FavoriteQuotation(
        quotationId = quotationId,
        client = client,
        eventType = EventType.fromName(eventType),
        amount = amount
    )
}
