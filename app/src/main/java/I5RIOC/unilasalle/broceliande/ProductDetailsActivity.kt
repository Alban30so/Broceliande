package I5RIOC.unilasalle.broceliande

import I5RIOC.unilasalle.broceliande.model.Product
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

class ProductDetailsActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val product = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			intent.getParcelableExtra("PRODUCT_EXTRA", Product::class.java)
		} else {
			@Suppress("DEPRECATION")
			intent.getParcelableExtra("PRODUCT_EXTRA")
		}

		setContent {
			MaterialTheme {
				Surface(color = MaterialTheme.colorScheme.background) {
					product?.let { ProductDetailScreen(it) }
				}
			}
		}
	}
}

@Composable
fun ProductDetailScreen(product: Product) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
			.padding(16.dp)
	) {
		// Image du produit
		AsyncImage(
			model = product.image,
			contentDescription = product.title,
			modifier = Modifier
				.fillMaxWidth()
				.height(300.dp),
			contentScale = ContentScale.Fit
		)

		Spacer(modifier = Modifier.height(24.dp))

		// Titre et Catégorie
		Text(
			text = product.category.uppercase(),
			style = MaterialTheme.typography.labelLarge,
			color = MaterialTheme.colorScheme.secondary
		)
		Text(
			text = product.title,
			style = MaterialTheme.typography.headlineMedium,
			fontWeight = FontWeight.Bold
		)

		Spacer(modifier = Modifier.height(16.dp))

		// Prix et Note
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Text(
				text = "${product.price} €",
				style = MaterialTheme.typography.headlineSmall,
				color = MaterialTheme.colorScheme.primary,
				fontWeight = FontWeight.ExtraBold
			)

			Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
				Text(text = "Note: ${product.rating.rate}/5", fontWeight = FontWeight.Medium)
				Text(
					text = "(${product.rating.count} avis)",
					style = MaterialTheme.typography.bodySmall
				)
			}
		}

		HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

		// Description
		Text(
			text = "Description",
			style = MaterialTheme.typography.titleMedium,
			fontWeight = FontWeight.Bold
		)
		Spacer(modifier = Modifier.height(8.dp))
		Text(
			text = product.description,
			style = MaterialTheme.typography.bodyLarge,
			lineHeight = 24.sp
		)
	}
}