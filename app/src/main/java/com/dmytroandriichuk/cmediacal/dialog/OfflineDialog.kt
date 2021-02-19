package com.dmytroandriichuk.cmediacal.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.fragment.app.DialogFragment
import java.lang.ClassCastException

//message about errors and allowing to check orders from local db
class OfflineDialog(private val message: String): DialogFragment() {

    private lateinit var dialogListener: OfflineDialogListener

    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Failed to sign in!")
                .setMessage(message)
                .setPositiveButton("Ok") {
                        dialog, _ ->  dialog.cancel()
                }

            when (message) {
                "Account is not verified" -> {
                    builder.setNegativeButton("Send Verification Letter") { _, _ ->
                        dialogListener.sendVerificationLetter()
                    }
                }
                "Connection error" -> {
                    builder.setNegativeButton("Go offline") { _, _ ->
                        dialogListener.goOfflineClicked()
                    }
                }
                else -> {
                    builder.setNegativeButton("Sign Up") { _, _ ->
                        dialogListener.goToRegisterActivity()
                    }
                }
            }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            dialogListener = context as OfflineDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context does not implement DialogOfflineListener")
        }
    }

    interface OfflineDialogListener {
        fun goOfflineClicked()
        fun sendVerificationLetter()
        fun goToRegisterActivity()
    }
}