package com.dmytroandriichuk.cmediacal.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.fragment.app.DialogFragment
import com.dmytroandriichuk.cmediacal.MainActivity
import java.lang.ClassCastException

//message about errors and allowing to check orders from local db
class DialogOffline(private val message: String): DialogFragment() {

    private lateinit var listener: DialogOfflineListener

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
                        listener.sendVerificationLetter()
                    }
                }
                "Connection error" -> {
                    builder.setNegativeButton("Go offline") { _, _ ->
                        listener.goOfflineClicked()
                    }
                }
                else -> {
                    builder.setNegativeButton("Sign Up") { _, _ ->
                        listener.goToRegisterActivity()
                    }
                }
            }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as DialogOfflineListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context does not implement DialogOfflineListener")
        }
    }

    interface DialogOfflineListener {
        fun goOfflineClicked()
        fun sendVerificationLetter()
        fun goToRegisterActivity()
    }
}