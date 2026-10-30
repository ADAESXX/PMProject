package gt.uvg.pmproject.data

import android.content.Context
import androidx.room.Room
import gt.uvg.pmproject.data.local.AppDatabase

class AppContainer(context: Context) {
    private val database: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "pmproject.db"
    ).build()

    val quotationRepository: QuotationRepository = FirestoreQuotationRepository()
    val favoriteRepository: FavoriteRepository = RoomFavoriteRepository(database.favoriteDao())
}
