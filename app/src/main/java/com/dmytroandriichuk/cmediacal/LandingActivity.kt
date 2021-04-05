package com.dmytroandriichuk.cmediacal

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dmytroandriichuk.cmediacal.MainActivity.Companion.isOnline
import com.dmytroandriichuk.cmediacal.data.ClinicListItem
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import kotlin.system.exitProcess

class LandingActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    private var startTime: Long = 0
    private var counter: Int = 0
    private lateinit var  sharedPreferences: SharedPreferences
    lateinit var placesClient: PlacesClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)
        mAuth = FirebaseAuth.getInstance()
        Places.initialize(
                applicationContext,
                getString(R.string.google_maps_key), Locale.CANADA
        )
        placesClient = Places.createClient(this)
        sharedPreferences = getSharedPreferences("user default", Context.MODE_PRIVATE)
        counter = sharedPreferences.getInt("counter", 0)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each

        navView.setupWithNavController(navController)
    }

    override fun onDestroy() {
        super.onDestroy()
        with (sharedPreferences.edit()) {
            putInt("counter", counter)
            apply()
        }
    }

    //check for double press of back button and close app
    override fun onBackPressed() {
        if (System.currentTimeMillis() - startTime < 2000){
            finishAffinity()
            exitProcess(0)
        } else {
            Toast.makeText(
                this,
                "Press one more time to close app" + if (isOnline(this))
                "\nYou won't be Logged Out of your account" else "",
                Toast.LENGTH_LONG
            ).show()
            startTime = System.currentTimeMillis()
        }
    }

    fun openDetailsActivity(item: ClinicListItem){

        intent = Intent(this@LandingActivity, DetailsActivity::class.java)
        intent.putExtra("clinicListItem", item)
        counter += 1
        if (counter == 2) {
            counter = 0
            intent.putExtra("openValidationScreen", true)
        }

        startActivity(intent)
    }
}