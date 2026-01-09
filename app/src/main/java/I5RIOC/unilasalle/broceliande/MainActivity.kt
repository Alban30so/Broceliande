package I5RIOC.unilasalle.broceliande

import I5RIOC.unilasalle.broceliande.model.MainViewModel
import I5RIOC.unilasalle.broceliande.model.Product
import I5RIOC.unilasalle.broceliande.ui.theme.BroceliandeTheme
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: MainViewModel by viewModels()

		enableEdgeToEdge()
		setContent {
			BroceliandeTheme {
				Surface(modifier = Modifier.fillMaxSize()) {
					ProductListScreen(
						products = viewModel.productList.value,
						onProductClick = { product ->
							val intent = Intent(this, ProductDetailsActivity::class.java).apply {
								putExtra("PRODUCT_EXTRA", product)
							}
							startActivity(intent)
						}
					)
				}
			}
		}
	}
}

@Composable
fun ProductListScreen(products: List<Product>, onProductClick: (Product) -> Unit) {
	// GridCells.Adaptive(150.dp) permet d'avoir 2 colonnes sur mobile, plus sur tablette (Responsive)
	LazyVerticalGrid(
		columns = GridCells.Adaptive(minSize = 160.dp),
		contentPadding = PaddingValues(8.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		items(products) { product ->
			ProductItem(product = product, onClick = { onProductClick(product) })
		}
	}
}

@Composable
fun ProductItem(product: Product, onClick: () -> Unit) {
	Card(
		elevation = CardDefaults.cardElevation(4.dp),
		modifier = Modifier
			.fillMaxWidth()
			.height(260.dp) // Hauteur fixe pour uniformiser
			.clickable { onClick() },
	) {
		Column(
			modifier = Modifier.padding(8.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			// Image
			AsyncImage(
				model = product.image,
				contentDescription = product.title,
				modifier = Modifier
					.height(120.dp)
					.fillMaxWidth(),
				contentScale = ContentScale.Fit,
				// Ajoutez ceci pour debugger :
				onState = { state ->
					when (state) {
						is coil3.compose.AsyncImagePainter.State.Error -> {
							// Affiche l'erreur dans les logs
							println("Coil Error: ${state.result.throwable}")
						}

						else -> {}
					}
				}
			)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.title,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "${product.price} €",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}