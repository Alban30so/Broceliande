package I5RIOC.unilasalle.broceliande

import I5RIOC.unilasalle.broceliande.model.MainViewModel
import I5RIOC.unilasalle.broceliande.model.OrderWithItems
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderHistoryActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val viewModel: MainViewModel by viewModels()

		enableEdgeToEdge()
		setContent {
			BroceliandeTheme {
				Surface {
					val orders by viewModel.orders.collectAsState(initial = emptyList())
					OrderHistoryScreen(orders = orders, onBack = { finish() })
				}
			}
		}
	}
}

@Composable
fun OrderHistoryScreen(orders: List<OrderWithItems>, onBack: () -> Unit) {
	Column(modifier = Modifier
		.fillMaxSize()
		.padding(16.dp)) {
		Text(
			text = "Mes commandes",
			style = MaterialTheme.typography.headlineMedium,
			fontWeight = FontWeight.Bold,
			modifier = Modifier.padding(bottom = 16.dp)
		)

		if (orders.isEmpty()) {
			Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
				Text("Aucune commande passée... pour le moment...")
			}
		} else {
			LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
				items(orders) { orderWithItems ->
					OrderItemCard(orderWithItems)
				}
			}
		}
	}
}

@Composable
fun OrderItemCard(orderWithItems: OrderWithItems) {
	val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
	val dateString = dateFormat.format(Date(orderWithItems.order.date))

	Card(elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier.fillMaxWidth()) {
		Column(modifier = Modifier.padding(16.dp)) {
			Row(verticalAlignment = Alignment.CenterVertically) {
				Icon(
					imageVector = Icons.Default.DateRange,
					contentDescription = null,
					tint = MaterialTheme.colorScheme.primary
				)
				Spacer(modifier = Modifier.width(8.dp))
				Text(
					text = "Commande du $dateString",
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.Bold
				)
			}
			HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

			orderWithItems.items.forEach { item ->
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(vertical = 2.dp),
					horizontalArrangement = Arrangement.SpaceBetween
				) {
					Text(
						text = "${item.quantity}x ${item.productTitle}",
						modifier = Modifier.weight(1f),
						maxLines = 1
					)
					Text(
						text = String.format("%.2f €", item.productPrice * item.quantity),
						fontWeight = FontWeight.Bold
					)
				}
			}
			HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

			Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
				Text(text = "Total : ", style = MaterialTheme.typography.bodyLarge)
				Text(
					text = String.format("%.2f €", orderWithItems.order.totalAmount),
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.ExtraBold,
					color = MaterialTheme.colorScheme.primary
				)
			}
		}
	}
}
