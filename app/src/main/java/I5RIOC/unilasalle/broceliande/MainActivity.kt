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
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
				var searchQuery by remember { mutableStateOf("") }
				var selectedCategory by remember { mutableStateOf<String?>(null) }
				var showMenu by remember { mutableStateOf(false) }

				Scaffold(
					floatingActionButton = {
						FloatingActionButton(
							onClick = {
								startActivity(Intent(this, CartActivity::class.java))
							},
							containerColor = MaterialTheme.colorScheme.primary
						) {
							Icon(
								imageVector = Icons.Default.ShoppingCart,
								contentDescription = "Voir le panier"
							)
						}
					}
				) { innerPadding ->
					Column(
						modifier = Modifier
							.fillMaxSize()
							.padding(innerPadding)
					) {
						Box(
							modifier = Modifier
								.fillMaxWidth()
								.padding(top = 16.dp, bottom = 8.dp),
							contentAlignment = Alignment.Center
						) {
							Image(
								painter = painterResource(id = R.drawable.logo_home),
								contentDescription = "Logo Broceliande",
								modifier = Modifier.height(60.dp),
								contentScale = ContentScale.Fit
							)
						}

						SearchBar(
							query = searchQuery,
							onQueryChange = { searchQuery = it }
						)

						Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
							Button(
								onClick = { showMenu = true },
								colors = ButtonDefaults.buttonColors(
									containerColor = MaterialTheme.colorScheme.secondaryContainer,
									contentColor = MaterialTheme.colorScheme.onSecondaryContainer
								)
							) {
								Text(text = selectedCategory?.replaceFirstChar { it.uppercase() }
									?: "Toutes les catégories")
								Icon(Icons.Default.ArrowDropDown, contentDescription = null)
							}

							DropdownMenu(
								expanded = showMenu,
								onDismissRequest = { showMenu = false }
							) {
								DropdownMenuItem(
									text = { Text("Toutes les catégories") },
									onClick = {
										selectedCategory = null
										showMenu = false
									}
								)
								viewModel.categories.value.forEach { category ->
									DropdownMenuItem(
										text = { Text(category.replaceFirstChar { it.uppercase() }) },
										onClick = {
											selectedCategory = category
											showMenu = false
										}
									)
								}
							}
						}

						val filteredProducts = viewModel.productList.value.filter { product ->
							val matchesSearch =
								product.title.contains(searchQuery, ignoreCase = true)
							val matchesCategory =
								selectedCategory == null || product.category == selectedCategory
							matchesSearch && matchesCategory
						}

						if (filteredProducts.isEmpty()) {
							Box(
								modifier = Modifier.fillMaxSize(),
								contentAlignment = Alignment.Center
							) {
								Text(
									"Aucun produit trouvé",
									style = MaterialTheme.typography.bodyLarge
								)
							}
						} else {
							ProductListScreen(
								products = filteredProducts,
								onProductClick = { product ->
									val intent = Intent(
										this@MainActivity,
										ProductDetailsActivity::class.java
									).apply {
										putExtra("PRODUCT_EXTRA", product)
									}
									startActivity(intent)
								},
								onAddToCart = { product ->
									viewModel.addToCart(product)
								}
							)
						}
					}
				}
			}
		}
	}
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
	OutlinedTextField(
		value = query,
		onValueChange = onQueryChange,
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 8.dp),
		placeholder = { Text("Rechercher...") },
		leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
		trailingIcon = {
			if (query.isNotEmpty()) {
				IconButton(onClick = { onQueryChange("") }) {
					Icon(Icons.Default.Clear, contentDescription = "Effacer")
				}
			}
		},
		singleLine = true,
		shape = MaterialTheme.shapes.medium
	)
}

@Composable
fun ProductListScreen(
	products: List<Product>,
	onProductClick: (Product) -> Unit,
	onAddToCart: (Product) -> Unit
) {
	LazyVerticalGrid(
		columns = GridCells.Adaptive(minSize = 160.dp),
		contentPadding = PaddingValues(
			bottom = 80.dp,
			start = 8.dp,
			end = 8.dp,
			top = 8.dp
		),
		verticalArrangement = Arrangement.spacedBy(8.dp),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		items(products) { product ->
			ProductItem(
				product = product,
				onClick = { onProductClick(product) },
				onAddToCart = { onAddToCart(product) }
			)
		}
	}
}

@Composable
fun ProductItem(product: Product, onClick: () -> Unit, onAddToCart: () -> Unit) {
	Card(
		elevation = CardDefaults.cardElevation(4.dp),
		modifier = Modifier
			.fillMaxWidth()
			.height(300.dp)
			.clickable { onClick() },
	) {
		Column(
			modifier = Modifier.padding(8.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			AsyncImage(
				model = product.image,
				contentDescription = product.title,
				modifier = Modifier
					.height(120.dp)
					.fillMaxWidth(),
				contentScale = ContentScale.Fit
			)

			Spacer(modifier = Modifier.height(8.dp))

			Text(
				text = product.title,
				style = MaterialTheme.typography.bodyMedium,
				maxLines = 2,
				overflow = TextOverflow.Ellipsis,
				modifier = Modifier.weight(1f)
			)

			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween
			) {
				Text(
					text = "${product.price} €",
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.Bold,
					color = MaterialTheme.colorScheme.primary
				)

				Button(
					onClick = { onAddToCart() },
					contentPadding = PaddingValues(horizontal = 8.dp)
				) {
					Text("(+)")
				}
			}
		}
	}
}