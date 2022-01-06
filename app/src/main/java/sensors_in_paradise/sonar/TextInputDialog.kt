package sensors_in_paradise.sonar

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View

import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener

class TextInputDialog(
    context: Context,
    title: String,
    promptInterface: (text: String) -> Unit,
    hint: String = "",
    errorMessage: String? = null,
    val acceptanceInterface: (text: String) -> Pair<Boolean, String?>
) {
    var dialog: AlertDialog

    init {

        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(title)

        val root = LayoutInflater.from(context).inflate(R.layout.prompt_dialog, null)
        val input = root.findViewById<EditText>(R.id.editText_promptDialog)
        val errorTV = root.findViewById<TextView>(R.id.tv_error_promptDialog)

        if (errorMessage != null) {
            errorTV.text = errorMessage
        }
        errorTV.visibility = if (errorMessage == null) View.GONE else View.VISIBLE
        input.hint = hint
        builder.setView(root)

        builder.setNegativeButton(
            "Cancel"
        ) { dialog, _ -> dialog.cancel() }

        dialog = builder.create()

        dialog.setButton(Dialog.BUTTON_POSITIVE, "OK") { _, _ ->
            promptInterface(input.text.toString())
        }

        dialog.show()
        input.addTextChangedListener { text ->
            val positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            if (acceptanceInterface != null) {
                val result = acceptanceInterface(text.toString())
                positiveBtn.isEnabled = result.first
                if (result.second != null) {
                    errorTV.text = result.second
                }
                errorTV.visibility =
                    if (result.second != null && !result.first) View.VISIBLE else View.INVISIBLE
            }
        }

        // Trigger change listener once
        input.setText("")
    }

    fun setCancelListener(listener: DialogInterface.OnCancelListener) {
        dialog.setOnCancelListener(listener)
    }
}