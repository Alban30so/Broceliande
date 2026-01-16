package I5RIOC.unilasalle.broceliande.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "orders")
data class Order(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val date: Long,
	val totalAmount: Double
)

@Entity(
	tableName = "order_items",
	foreignKeys = [
		ForeignKey(
			entity = Order::class,
			parentColumns = ["id"],
			childColumns = ["orderId"],
			onDelete = ForeignKey.CASCADE
		)
	]
)
data class OrderItem(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val orderId: Long,
	val productTitle: String,
	val productPrice: Double,
	val quantity: Int,
	val image: String
)

data class OrderWithItems(
	@Embedded val order: Order,
	@Relation(
		parentColumn = "id",
		entityColumn = "orderId"
	)
	val items: List<OrderItem>
)
