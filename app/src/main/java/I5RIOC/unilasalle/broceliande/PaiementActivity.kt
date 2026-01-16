package I5RIOC.unilasalle.broceliande

import I5RIOC.unilasalle.broceliande.model.MainViewModel
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import I5RIOC.unilasalle.broceliande.ui.theme.BroceliandeTheme
import androidx.activity.viewModels
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class PaiementActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val viewModel: MainViewModel by viewModels()
		createNotificationChannel(this)
		enableEdgeToEdge()
		setContent {
			BroceliandeTheme {
				val context = LocalContext.current
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colorScheme.background
				) {
					val notificationPermissionLauncher = rememberLauncherForActivityResult(
						contract = ActivityResultContracts.RequestPermission(),
						onResult = { isGranted ->
							// Ici on peut loguer si l'utilisateur a accepté ou refusé
							if (isGranted) {
								println("Permission accordée !")
							} else {
								println("Permission refusée :(")
							}
						}
					)

					// 2. On demande la permission au lancement de l'écran (si Android 13+)
					LaunchedEffect(Unit) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
							val permissionStatus = ContextCompat.checkSelfPermission(
								context,
								Manifest.permission.POST_NOTIFICATIONS
							)
							if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
								notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
							}
						}
					}
					PaymentScreen(
						onPaymentSuccess = {
							//Clearing du panier.
							viewModel.clearCart()
							// Retour à l'accueil en vidant la pile d'activités
							val intent = Intent(this, MainActivity::class.java)
							intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
							startActivity(intent)
							//Envoi notification
							sendNotification(context)
							finish()
						}
					)
				}
			}
		}
	}
}

private fun createNotificationChannel(context: Context) {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
		val name = "Commandes Brocéliande"
		val descriptionText = "Notifications de suivi de commande"
		val importance = NotificationManager.IMPORTANCE_DEFAULT
		val channel = NotificationChannel("ORDER_CHANNEL_ID", name, importance).apply {
			description = descriptionText
		}
		val notificationManager: NotificationManager =
			context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		notificationManager.createNotificationChannel(channel)
	}
}

private fun sendNotification(context: Context) {
	val builder = NotificationCompat.Builder(context, "ORDER_CHANNEL_ID")
		.setSmallIcon(android.R.drawable.stat_notify_sync)
		.setContentTitle("Commande Validée ! \uD83C\uDF89")
		.setContentText("Votre commande a bien été enregistrée et sera expédiée sous peu.")
		.setPriority(NotificationCompat.PRIORITY_DEFAULT)
		.setAutoCancel(true)

	with(NotificationManagerCompat.from(context)) {
		notify(1001, builder.build())
	}
}

@Composable
fun PaymentScreen(onPaymentSuccess: () -> Unit) {
	// États du formulaire
	var cardNumber by remember { mutableStateOf("") }
	var cardHolder by remember { mutableStateOf("") }
	var expiryDate by remember { mutableStateOf("") }
	var cvv by remember { mutableStateOf("") }

	// États de l'interface (Chargement / Succès)
	var isLoading by remember { mutableStateOf(false) }
	var isSuccess by remember { mutableStateOf(false) }

	val scope = rememberCoroutineScope()

	if (isSuccess) {
		SuccessView(onHomeClick = onPaymentSuccess)
	} else {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.statusBarsPadding()
				.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp)
		) {
			Text(
				text = "Paiement sécurisé",
				style = MaterialTheme.typography.headlineMedium,
				color = MaterialTheme.colorScheme.primary
			)

			// Icône de sécurité
			Row(verticalAlignment = Alignment.CenterVertically) {
				Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
				Spacer(modifier = Modifier.width(4.dp))
				Text("Vos données sont chiffrées (Simulation)", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
			}

			Spacer(modifier = Modifier.height(16.dp))

			// 1. Numéro de carte
			OutlinedTextField(
				value = cardNumber,
				onValueChange = { if (it.length <= 16 && it.all { char -> char.isDigit() }) cardNumber = it },
				label = { Text("Numéro de carte") },
				modifier = Modifier.fillMaxWidth(),
				keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
				visualTransformation = CreditCardVisualTransformation(), // Ajoute les espaces
				singleLine = true,
				placeholder = { Text("0000 0000 0000 0000") }
			)

			// 2. Nom du titulaire
			OutlinedTextField(
				value = cardHolder,
				onValueChange = { cardHolder = it },
				label = { Text("Titulaire de la carte") },
				modifier = Modifier.fillMaxWidth(),
				keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
				singleLine = true
			)

			// Ligne Date + CVV
			Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
				// 3. Date d'expiration
				OutlinedTextField(
					value = expiryDate,
					onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) expiryDate = it },
					label = { Text("MM/AA") },
					modifier = Modifier.weight(1f),
					keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
					visualTransformation = ExpiryDateVisualTransformation(), // Ajoute le /
					singleLine = true,
					placeholder = { Text("MM/AA") }
				)

				// 4. CVV
				OutlinedTextField(
					value = cvv,
					onValueChange = { if (it.length <= 3 && it.all { char -> char.isDigit() }) cvv = it },
					label = { Text("CVV") },
					modifier = Modifier.weight(1f),
					keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
					visualTransformation = PasswordVisualTransformation(), // Cache les chiffres
					singleLine = true
				)
			}

			Spacer(modifier = Modifier.weight(1f))

			// Bouton Payer
			Button(
				onClick = {
					isLoading = true
					// Simulation du paiement
					scope.launch {
						delay(2500) // Attendre 2.5 secondes
						isLoading = false
						isSuccess = true
					}
				},
				enabled = !isLoading && cardNumber.length == 16 && expiryDate.length == 4 && cvv.length == 3,
				modifier = Modifier.fillMaxWidth().height(50.dp)
			) {
				if (isLoading) {
					CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
				} else {
					Text("Payer maintenant")
				}
			}
		}
	}
}

@Composable
fun SuccessView(onHomeClick: () -> Unit) {
	Column(
		modifier = Modifier.fillMaxSize().padding(32.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Icon(
			imageVector = Icons.Default.CheckCircle,
			contentDescription = "Succès",
			tint = Color(0xFF4CAF50), // Vert succès
			modifier = Modifier.size(100.dp)
		)
		Spacer(modifier = Modifier.height(24.dp))
		Text(
			text = "Commande Confirmée !",
			style = MaterialTheme.typography.headlineMedium,
			color = MaterialTheme.colorScheme.primary
		)
		Text(
			text = "Merci pour votre achat.",
			style = MaterialTheme.typography.bodyLarge
		)
		Spacer(modifier = Modifier.height(48.dp))
		Button(onClick = onHomeClick, modifier = Modifier.fillMaxWidth()) {
			Text("Retour à l'accueil")
		}
	}
}

// --- CLASSES UTILITAIRES POUR LE FORMATAGE VISUEL ---

// Transforme "12345678" en "1234 5678" visuellement
class CreditCardVisualTransformation : VisualTransformation {
	override fun filter(text: AnnotatedString): TransformedText {
		val trimmed = if (text.text.length >= 16) text.text.substring(0..15) else text.text
		var out = ""
		for (i in trimmed.indices) {
			out += trimmed[i]
			if (i % 4 == 3 && i != 15) out += " "
		}

		val creditCardOffsetTranslator = object : OffsetMapping {
			override fun originalToTransformed(offset: Int): Int {
				if (offset <= 3) return offset
				if (offset <= 7) return offset + 1
				if (offset <= 11) return offset + 2
				if (offset <= 16) return offset + 3
				return 19
			}

			override fun transformedToOriginal(offset: Int): Int {
				if (offset <= 4) return offset
				if (offset <= 9) return offset - 1
				if (offset <= 14) return offset - 2
				if (offset <= 19) return offset - 3
				return 16
			}
		}
		return TransformedText(AnnotatedString(out), creditCardOffsetTranslator)
	}
}

// Transforme "1225" en "12/25" visuellement
class ExpiryDateVisualTransformation : VisualTransformation {
	override fun filter(text: AnnotatedString): TransformedText {
		val trimmed = if (text.text.length >= 4) text.text.substring(0..3) else text.text
		var out = ""
		for (i in trimmed.indices) {
			out += trimmed[i]
			if (i == 1) out += "/"
		}

		val offsetTranslator = object : OffsetMapping {
			override fun originalToTransformed(offset: Int): Int {
				if (offset <= 1) return offset
				if (offset <= 4) return offset + 1
				return 5
			}

			override fun transformedToOriginal(offset: Int): Int {
				if (offset <= 2) return offset
				if (offset <= 5) return offset - 1
				return 4
			}
		}
		return TransformedText(AnnotatedString(out), offsetTranslator)
	}
}