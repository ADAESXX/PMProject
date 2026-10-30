package gt.uvg.pmproject.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY client ASC")
    fun observeAll(): Flow<List<FavoriteEntity>>

    @Query("SELECT COUNT(*) > 0 FROM favorites WHERE quotationId = :quotationId")
    fun observeExists(quotationId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE quotationId = :quotationId")
    suspend fun deleteById(quotationId: String)

    @Query(
        "UPDATE favorites SET client = :client, eventType = :eventType, amount = :amount " +
                "WHERE quotationId = :quotationId"
    )
    suspend fun update(quotationId: String, client: String, eventType: String, amount: Double)
}
