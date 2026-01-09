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
                // États pour la recherche et le filtre
                var searchQuery by remember { mutableStateOf("") }
                var selectedCategory by remember { mutableStateOf<String?>(null) } // null = "Toutes les catégories"
                var showMenu by remember { mutableStateOf(false) } // Pour ouvrir/fermer le menu

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                    ) {
                        // --- 1. BARRE DE RECHERCHE ---
                        SearchBar(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it }
                        )

                        // --- 2. FILTRE CATÉGORIE (Bouton + Menu) ---
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            Button(
                                onClick = { showMenu = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            ) {
                                Text(text = selectedCategory?.replaceFirstChar { it.uppercase() } ?: "Toutes les catégories")
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }

                            // Menu déroulant
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                // Option pour tout afficher
                                DropdownMenuItem(
                                    text = { Text("Toutes les catégories") },
                                    onClick = {
                                        selectedCategory = null
                                        showMenu = false
                                    }
                                )
                                // Liste des catégories depuis l'API
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

                        // --- 3. LOGIQUE DE FILTRAGE COMBINÉE ---
                        val filteredProducts = viewModel.productList.value.filter { product ->
                            // Vérifie si le titre contient la recherche
                            val matchesSearch = product.title.contains(searchQuery, ignoreCase = true)
                            // Vérifie si la catégorie correspond (ou si aucune n'est sélectionnée)
                            val matchesCategory = selectedCategory == null || product.category == selectedCategory

                            matchesSearch && matchesCategory
                        }

                        // --- 4. AFFICHAGE ---
                        if (filteredProducts.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Aucun produit trouvé", style = MaterialTheme.typography.bodyLarge)
                            }
                        } else {
                            ProductListScreen(
                                products = filteredProducts,
                                onProductClick = { product ->
                                    val intent = Intent(this@MainActivity, ProductDetailsActivity::class.java).apply {
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
fun ProductListScreen(products: List<Product>, onProductClick: (Product) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(bottom = 16.dp, start = 8.dp, end = 8.dp),
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
            .height(260.dp)
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

            Text(
                text = "${product.price} €",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}