package I5RIOC.unilasalle.broceliande.data

import I5RIOC.unilasalle.broceliande.model.CartItem
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CartItem::class], version = 1, exportSchema = false)
abstract class BroceliandeDatabase : RoomDatabase() {
	abstract fun cartDao(): CartDao

	companion object {
		@Volatile
		private var INSTANCE: BroceliandeDatabase? = null

		fun getDatabase(context: Context): BroceliandeDatabase {
			return INSTANCE ?: synchronized(this) {
				val instance = Room.databaseBuilder(
					context.applicationContext,
					BroceliandeDatabase::class.java,
					"broceliande_database"
				).build()
				INSTANCE = instance
				instance
			}
		}
	}
}