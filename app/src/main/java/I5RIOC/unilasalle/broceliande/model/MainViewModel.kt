package I5RIOC.unilasalle.broceliande.model

import I5RIOC.unilasalle.broceliande.network.RetrofitInstance
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
	var productList = mutableStateOf<List<Product>>(emptyList())
		private set

    val categories = mutableStateOf<List<String>>(emptyList())

	init {
		fetchProducts()
        fetchCategories()
	}

	private fun fetchProducts() {
		viewModelScope.launch {
			try {
				productList.value = RetrofitInstance.api.getAllProducts()
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
	}

    private fun fetchCategories() {
        viewModelScope.launch {
            try {
                // Remplacez 'RetrofitInstance.api' par votre accès réel à l'API
                val fetchedCategories = RetrofitInstance.api.getCategories()
                categories.value = fetchedCategories
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}