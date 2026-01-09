package I5RIOC.unilasalle.broceliande.data

import I5RIOC.unilasalle.broceliande.model.CartItem
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
	@Query("SELECT * FROM cart")
	fun getCartItems(): Flow<List<CartItem>>

	@Query("SELECT * FROM cart WHERE id = :productId LIMIT 1")
	suspend fun getCartItemById(productId: Int): CartItem?

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertOrUpdate(item: CartItem)

	@Query("DELETE FROM cart WHERE id = :productId")
	suspend fun deleteItem(productId: Int)

	@Query("DELETE FROM cart")
	suspend fun clearCart()
}