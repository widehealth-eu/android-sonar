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
    promptInterface: PromptInterface,
    hint: String = "",
    errorMessage: String? = null,
    val acceptanceInterface: AcceptanceInterface?
) {
    var dialog: AlertDialog

    interface AcceptanceInterface {
        /**Returns if the current input is accepted and and optional error message*/
        fun onInputChanged(text: String): Pair<Boolean, String?>
    }

    interface PromptInterface {
        fun onInputSubmitted(text: String)
    }

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
            promptInterface.onInputSubmitted(input.text.toString())
        }

        dialog.show()
        input.addTextChangedListener { text ->
            val positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            acceptanceInterface?.onInputChanged(text.toString())
            if (acceptanceInterface != null) {
                val result = acceptanceInterface.onInputChanged(text.toString())
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
