package I5RIOC.unilasalle.broceliande

import I5RIOC.unilasalle.broceliande.model.CartItem
import I5RIOC.unilasalle.broceliande.model.MainViewModel
import I5RIOC.unilasalle.broceliande.model.Product
import I5RIOC.unilasalle.broceliande.model.Rating
import I5RIOC.unilasalle.broceliande.ui.theme.BroceliandeTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

class CartActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val viewModel: MainViewModel by viewModels()

		enableEdgeToEdge()
		setContent {
			BroceliandeTheme {
				val cartItems by viewModel.cartItems.collectAsState()
				CartScreen(
					items = cartItems,
					onDelete = { product -> viewModel.removeFromCart(product) }
				)
			}
		}
	}
}

@Composable
fun CartItemRow(item: CartItem, onDelete: () -> Unit) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 4.dp),
		elevation = CardDefaults.cardElevation(2.dp)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(vertical = 4.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			AsyncImage(
				model = item.image,
				contentDescription = null,
				modifier = Modifier.size(60.dp),
				contentScale = ContentScale.Fit
			)

			Spacer(modifier = Modifier.width(16.dp))

			Column(modifier = Modifier.weight(1f)) {
				Text(
					text = item.title,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
					style = MaterialTheme.typography.bodyMedium,
					fontWeight = FontWeight.Bold
				)
				Text(
					text = "${item.price} € / unité",
					style = MaterialTheme.typography.bodySmall
				)
			}

			Column(horizontalAlignment = Alignment.End) {
				Text(
					text = "x${item.quantity}",
					fontWeight = FontWeight.Bold,
					color = MaterialTheme.colorScheme.primary
				)
				Text(
					text = "${item.price * item.quantity} €",
					style = MaterialTheme.typography.bodyMedium
				)
			}

			IconButton(onClick = onDelete) {
				Icon(
					imageVector = Icons.Default.Delete,
					contentDescription = "Supprimer",
					tint = MaterialTheme.colorScheme.error
				)
			}
		}
	}
}

@Composable
fun CartScreen(items: List<CartItem>, onDelete: (Product) -> Unit) {
	val total = items.sumOf { it.price * it.quantity }

	Column(modifier = Modifier
		.fillMaxSize()
		.padding(16.dp)) {
		Text(
			"Mon Panier",
			style = MaterialTheme.typography.headlineMedium,
			modifier = Modifier.padding(bottom = 16.dp)
		)

		if (items.isEmpty()) {
			Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
				Text("Votre panier est vide \uD83D\uDED2")
			}
		} else {
			LazyColumn(modifier = Modifier.weight(1f)) {
				items(items) { item ->
					val productTempo =
						Product(item.id, item.title, item.price, "", "", item.image, Rating(0.0, 0))
					CartItemRow(item = item, onDelete = { onDelete(productTempo) })
				}
			}

			Spacer(modifier = Modifier.height(16.dp))

			Card(
				colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
				modifier = Modifier.fillMaxWidth()
			) {
				Column(modifier = Modifier.padding(16.dp)) {
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.SpaceBetween
					) {
						Text("Total à payer :", style = MaterialTheme.typography.titleMedium)
						Text(
							"$total €",
							style = MaterialTheme.typography.titleLarge,
							fontWeight = FontWeight.Bold
						)
					}
					Spacer(modifier = Modifier.height(8.dp))
					Button(
						onClick = {/* ici on fait rien pour l'instnat */ },
						modifier = Modifier.fillMaxWidth()
					) {
						Text("Valider la commande")
					}
				}
			}
		}
	}
}
