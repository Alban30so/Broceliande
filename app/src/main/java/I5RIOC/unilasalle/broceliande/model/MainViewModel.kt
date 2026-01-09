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
	private val dao = BroceliandeDatabase.getDatabase(application).cartDao()

	// refresh UI automatique
	private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
	val cartItems = _cartItems.asStateFlow()

	init {
		fetchProducts()
		fetchCategories()
		viewModelScope.launch {
			dao.getCartItems().collect { items ->
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
			val existingItem = dao.getCartItemById(product.id)
			if (existingItem != null) {
				val updatedItem = existingItem.copy(quantity = existingItem.quantity + 1)
				dao.insertOrUpdate(updatedItem)
			} else {
				val newItem = CartItem(
					id = product.id,
					title = product.title,
					price = product.price,
					image = product.image,
					quantity = 1
				)
				dao.insertOrUpdate(newItem)
			}
		}
	}

	// suppression d'un produit au panier
	fun removeFromCart(product: Product) {
		viewModelScope.launch {
			dao.deleteItem(product.id)
		}
	}
}