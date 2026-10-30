package gt.uvg.pmproject.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val quotationId: String,
    val client: String,
    val eventType: String,
    val amount: Double
)
