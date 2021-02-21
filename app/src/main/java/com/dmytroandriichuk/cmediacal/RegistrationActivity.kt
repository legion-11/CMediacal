package com.dmytroandriichuk.cmediacal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// provide screen for registration
class RegistrationActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText
    private lateinit var passwordET2: EditText

    private lateinit var emailLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var passwordLayout2: TextInputLayout
    private lateinit var nameLayout: TextInputLayout
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        emailET = findViewById(R.id.emailRegistrationET)
        passwordET = findViewById(R.id.passwordlRegistrationET)
        passwordET2 = findViewById(R.id.passwordlRegistrationET2)

        emailLayout = findViewById(R.id.emailRegistrationLayout)
        passwordLayout = findViewById(R.id.passwordRegistrationLayout)
        passwordLayout2 = findViewById(R.id.passwordRegistrationLayout2)

        progressBar = findViewById(R.id.registrationProgressBar)

        val registerButton = findViewById<Button>(R.id.confirmRegistrationButton)
        registerButton.setOnClickListener { registerUser() }
    }

    //check input and sen email verification letter
    private fun registerUser() {

        val email = emailET.text.toString().trim()
        val password = passwordET.text.toString().trim()
        val password2 = passwordET2.text.toString().trim()

        var errors = false
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.error = "invalid email"
            errors = true
        } else {
            emailLayout.error = ""
        }

        if (email.isEmpty()) {
            emailLayout.error = "email is required"
            errors = true
        } else {
            emailLayout.error = ""
        }

        if (password != password2) {
            passwordLayout.error = "passwords are different"
            errors = true
        } else {
            passwordLayout.error = ""
        }

        if (password.length < 6) {
            passwordLayout.error = "password must be at least 6 characters"
            errors = true
        } else {
            if (!errors) {
                passwordLayout.error = ""
            }
        }

        if (!errors) {
            progressBar.visibility = View.VISIBLE
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { taskCreateUser ->
                if (taskCreateUser.isSuccessful) {
                    val user = hashMapOf("email" to email)
                    mAuth.currentUser?.let { currentUser ->
                        database.collection("User").document(currentUser.uid).set(user)
                            .addOnSuccessListener  {
                                currentUser.sendEmailVerification()
                                Toast.makeText(this, "User registered successfully! Verification letter will be send to your email", Toast.LENGTH_LONG).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                            }
//                        database.collection("User").document(currentUser.uid).get().addOnSuccessListener {
//                            Log.d("TAGaaaaaaaa", "DocumentSnapshot data: ${it.data}")
//                        }
                    }
                } else {Toast.makeText(this, taskCreateUser.exception?.message.toString(), Toast.LENGTH_LONG).show()}
            }
            progressBar.visibility = View.GONE
        }
    }
}
