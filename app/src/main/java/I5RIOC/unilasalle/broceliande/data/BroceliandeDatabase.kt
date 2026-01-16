package I5RIOC.unilasalle.broceliande.data

import I5RIOC.unilasalle.broceliande.model.CartItem
import I5RIOC.unilasalle.broceliande.model.Order
import I5RIOC.unilasalle.broceliande.model.OrderItem
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
	entities = [CartItem::class, Order::class, OrderItem::class],
	version = 2,
	exportSchema = false
)
abstract class BroceliandeDatabase : RoomDatabase() {
	abstract fun cartDao(): CartDao
	abstract fun orderDao(): OrderDao

	companion object {
		@Volatile
		private var INSTANCE: BroceliandeDatabase? = null

		fun getDatabase(context: Context): BroceliandeDatabase {
			return INSTANCE ?: synchronized(this) {
				val instance = Room.databaseBuilder(
					context.applicationContext,
					BroceliandeDatabase::class.java,
					"broceliande_database"
				)
					.fallbackToDestructiveMigration(false)
					.build()
				INSTANCE = instance
				instance
			}
		}
	}
}