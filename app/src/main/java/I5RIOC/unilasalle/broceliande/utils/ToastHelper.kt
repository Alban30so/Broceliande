package I5RIOC.unilasalle.broceliande.utils

import android.content.Context
import android.widget.Toast

object ToastHelper {
	private var currentToast: Toast? = null

	fun showShortToast(context: Context, message: String) {
		currentToast?.cancel()

		currentToast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
		currentToast?.show()
	}

	fun showLongToast(context: Context, message: String) {
		currentToast?.cancel()

		currentToast = Toast.makeText(context, message, Toast.LENGTH_LONG)
		currentToast?.show()
	}
}