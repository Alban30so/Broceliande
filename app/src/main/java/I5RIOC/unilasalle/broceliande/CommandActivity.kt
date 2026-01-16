package I5RIOC.unilasalle.broceliande

import I5RIOC.unilasalle.broceliande.ui.theme.BroceliandeTheme
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

class CommandActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			BroceliandeTheme {
				val context = LocalContext.current
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colorScheme.background
				) {
					CommandFormScreen(
						onBack = { finish() }, // Termine l'activité pour revenir au panier
						onPayment = {
							val intent = Intent(context, PaiementActivity::class.java)
							context.startActivity(intent)
						}
					)
				}
			}
		}
	}
}

@Composable
fun CommandFormScreen(onBack: () -> Unit, onPayment: () -> Unit) {
	var nom by remember { mutableStateOf("") }
	var prenom by remember { mutableStateOf("") }
	var email by remember { mutableStateOf("") }
	var telephone by remember { mutableStateOf("") }
	var adresse by remember { mutableStateOf("") }

	Column(
		modifier = Modifier
			.fillMaxSize()
			.statusBarsPadding()
			.padding(16.dp)
			.verticalScroll(rememberScrollState()),
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		Text(
			text = "Vos informations de livraison",
			style = MaterialTheme.typography.headlineMedium,
			color = MaterialTheme.colorScheme.primary
		)

		// --- Champs du formulaire ---
		OutlinedTextField(
			value = nom,
			onValueChange = { nom = it },
			label = { Text("Nom") },
			modifier = Modifier.fillMaxWidth(),
			singleLine = true
		)

		OutlinedTextField(
			value = prenom,
			onValueChange = { prenom = it },
			label = { Text("Prénom") },
			modifier = Modifier.fillMaxWidth(),
			singleLine = true
		)

		OutlinedTextField(
			value = email,
			onValueChange = { email = it },
			label = { Text("Email") },
			modifier = Modifier.fillMaxWidth(),
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
			singleLine = true
		)

		OutlinedTextField(
			value = telephone,
			onValueChange = { telephone = it },
			label = { Text("Téléphone") },
			modifier = Modifier.fillMaxWidth(),
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
			singleLine = true
		)

		OutlinedTextField(
			value = adresse,
			onValueChange = { adresse = it },
			label = { Text("Adresse complète") },
			modifier = Modifier.fillMaxWidth(),
			minLines = 3,
			maxLines = 3
		)

		Spacer(modifier = Modifier.weight(1f))

		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(8.dp)
		) {
			OutlinedButton(
				onClick = onBack,
				modifier = Modifier.weight(1f)
			) {
				Text("Retour au panier")
			}
			Button(
				onClick = onPayment,
				modifier = Modifier.weight(1f)
			) {
				Text("Passer au paiement")
			}
		}
	}
}