package gt.uvg.pmproject.data

import gt.uvg.pmproject.model.FavoriteQuotation
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun observeFavorites(): Flow<List<FavoriteQuotation>>

    fun observeIsFavorite(quotationId: String): Flow<Boolean>

    suspend fun addFavorite(favorite: FavoriteQuotation)
    suspend fun updateFavorite(favorite: FavoriteQuotation)

    suspend fun removeFavorite(quotationId: String)

}
