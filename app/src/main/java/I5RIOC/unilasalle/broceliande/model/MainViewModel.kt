package I5RIOC.unilasalle.broceliande.model

import I5RIOC.unilasalle.broceliande.network.RetrofitInstance
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
	var productList = mutableStateOf<List<Product>>(emptyList())
		private set

	init {
		fetchProducts()
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
}