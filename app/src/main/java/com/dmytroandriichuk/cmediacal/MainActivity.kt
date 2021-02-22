package com.dmytroandriichuk.cmediacal

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.dmytroandriichuk.cmediacal.dialog.OfflineDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

//screen for log in
//TODO make splash screen
class MainActivity : AppCompatActivity(), OfflineDialog.OfflineDialogListener {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var emailLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()

        //provides auth with google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)


        emailET = findViewById(R.id.emailET)
        passwordET = findViewById(R.id.passwordET)
        progressBar = findViewById(R.id.log_in_progress_bar)

        emailLayout = findViewById(R.id.emailLayout)
        passwordLayout = findViewById(R.id.passwordLayout)
        val signInButton = findViewById<Button>(R.id.sign_in_with_email_button)
        signInButton.setOnClickListener {
            signInWithEmail()
        }

        val forgetPasswordButton = findViewById<TextView>(R.id.forget_password_TV)
        forgetPasswordButton.setOnClickListener {
            goToRestorePasswordActivity()
        }

        val registerButton = findViewById<TextView>(R.id.registration_TV)
        registerButton.setOnClickListener {
            goToRegisterActivity()
        }

        val logInWithGoogle = findViewById<SignInButton>(R.id.sign_in_with_google_button)
        logInWithGoogle.setOnClickListener {
            signInWithGoogle()
        }
    }

    //check if user is authenticated and redirect him if one is
    override fun onStart() {
        super.onStart()
        val user = mAuth.currentUser
        if (user != null && user.isEmailVerified) {
            Log.i("auth", "onStart: user registered")
            intent = Intent(this@MainActivity, LandingActivity::class.java)
            startActivity(intent)
        } else {
            Log.i("auth", "onStart: user not registered")
        }
    }

    //check input and create user
    private fun signInWithEmail() {
        val email = emailET.text.toString().trim()
        val password = passwordET.text.toString().trim()

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

        if (password.isEmpty()) {
            passwordLayout.error = "password is required"
            errors = true
        } else {
            passwordLayout.error = ""
        }

        //send email verifiaction letter and create user
        //check visibility not allowing to press button few times
        if (!errors && progressBar.visibility == View.GONE ){
            progressBar.visibility = View.VISIBLE
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                progressBar.visibility = View.GONE
                if(it.isSuccessful){
                    val user = mAuth.currentUser
                    if (user != null) {
                        if (user.isEmailVerified){
                            intent = Intent(this@MainActivity, LandingActivity::class.java)
                            startActivity(intent)
                        } else {
                            buildDialog("Account is not verified")
                        }
                    }
                } else {
                    if(isOnline(this)){
                        buildDialog("User not found")
                    } else {
                        buildDialog("Connection error")
                    }
                }
            }
        }
    }

    //provide dialog for auth with google
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    //provide dialog for auth with google
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("MainActivity", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("MainActivity", "Google sign in failed", e)
                    buildDialog("Connection error")
                }
            } else { Log.w("MainActivity", task.exception) }
        }
    }


    //example for user auth from https://developers.google.com/identity/sign-in/android/start-integrating
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("MainActivity", "signInWithCredential:success")
                        intent = Intent(this@MainActivity, LandingActivity::class.java)
                        startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("MainActivity", "signInWithCredential:failure", task.exception)
                        buildDialog("Connection error")
                    }
                }
    }

    //show error message, depending on message provides option to log in for offline usage
    private fun buildDialog(message: String){
        val dialog = OfflineDialog(message)
        val manager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()
        dialog.show(transaction, "dialog")
    }

    override fun goToRegisterActivity() {
        intent = Intent(this@MainActivity, RegistrationActivity::class.java)
        startActivity(intent)
    }

    private fun goToRestorePasswordActivity() {
        intent = Intent(this@MainActivity, RestorePasswordActivity::class.java)
        startActivity(intent)
    }

    override fun goOfflineClicked() {
        intent = Intent(this@MainActivity, LandingActivity::class.java)
        startActivity(intent)
    }

    override fun sendVerificationLetter() {
        mAuth.currentUser?.sendEmailVerification()
    }

    companion object {
        const val RC_SIGN_IN = 111
        //check internet connection
        fun isOnline(owner: Activity): Boolean {
            val connectivityManager = owner.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                        return true
                    }
                }
            }
            Log.i("Internet", "No network")
            return false
        }
    }
}

