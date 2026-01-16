package I5RIOC.unilasalle.broceliande.data

import I5RIOC.unilasalle.broceliande.model.Order
import I5RIOC.unilasalle.broceliande.model.OrderItem
import I5RIOC.unilasalle.broceliande.model.OrderWithItems
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
	@Insert
	suspend fun insertOrder(order: Order): Long

	@Insert
	suspend fun insertOrderItems(items: List<OrderItem>)

	@Transaction
	@Query("SELECT * FROM orders ORDER BY date DESC")
	fun getAllOrders(): Flow<List<OrderWithItems>>
}