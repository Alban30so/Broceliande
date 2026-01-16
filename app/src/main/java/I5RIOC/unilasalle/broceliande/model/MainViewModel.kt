package I5RIOC.unilasalle.broceliande.model

import I5RIOC.unilasalle.broceliande.data.BroceliandeDatabase
import I5RIOC.unilasalle.broceliande.network.RetrofitInstance
import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
	// liste produits
	var productList = mutableStateOf<List<Product>>(emptyList())
		private set

	// liste catégories
	val categories = mutableStateOf<List<String>>(emptyList())

	// DAO panier
	private val cartDao = BroceliandeDatabase.getDatabase(application).cartDao()

	// DAO historique commande
	private val orderDao = BroceliandeDatabase.getDatabase(application).orderDao()

	// historique de commandes
	val orders = orderDao.getAllOrders()

	// refresh UI automatique
	private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
	val cartItems = _cartItems.asStateFlow()

	init {
		fetchProducts()
		fetchCategories()
		viewModelScope.launch {
			cartDao.getCartItems().collect { items ->
				_cartItems.value = items
			}
		}
	}

	// récupérer les produits avec l'API
	private fun fetchProducts() {
		viewModelScope.launch {
			try {
				productList.value = RetrofitInstance.api.getAllProducts()
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
	}

	// récupérer les catégories avec l'API
	private fun fetchCategories() {
		viewModelScope.launch {
			try {
				categories.value = RetrofitInstance.api.getAllCategories()
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
	}

	// ajout d'un produit au panier
	fun addToCart(product: Product) {
		viewModelScope.launch {
			val existingItem = cartDao.getCartItemById(product.id)
			if (existingItem != null) {
				val updatedItem = existingItem.copy(quantity = existingItem.quantity + 1)
				cartDao.insertOrUpdate(updatedItem)
			} else {
				val newItem = CartItem(
					id = product.id,
					title = product.title,
					price = product.price,
					image = product.image,
					quantity = 1
				)
				cartDao.insertOrUpdate(newItem)
			}
		}
	}

	// suppression d'un produit au panier
	fun removeFromCart(product: Product) {
		viewModelScope.launch {
			val existingItem = cartDao.getCartItemById(product.id)
			if (existingItem != null) {
				if (existingItem.quantity > 1) {
					val updatedItem = existingItem.copy(quantity = existingItem.quantity - 1)
					cartDao.insertOrUpdate(updatedItem)
				} else {
					cartDao.deleteItem(product.id)
				}
			}
		}
	}

	suspend fun validateOrder() {
		val currentCart = cartDao.getCartItemsSync()

		if (currentCart.isNotEmpty()) {
			val total = currentCart.sumOf { it.price * it.quantity }
			val timestamp = System.currentTimeMillis()

			val order = Order(date = timestamp, totalAmount = total)
			val orderId = orderDao.insertOrder(order)

			val orderItems = currentCart.map { item ->
				OrderItem(
					orderId = orderId,
					productTitle = item.title,
					productPrice = item.price,
					quantity = item.quantity,
					image = item.image
				)
			}
			orderDao.insertOrderItems(orderItems)

			cartDao.clearCart()
		}
	}
}
