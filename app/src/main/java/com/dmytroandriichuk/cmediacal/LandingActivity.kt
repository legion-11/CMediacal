package com.dmytroandriichuk.cmediacal

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dmytroandriichuk.cmediacal.MainActivity.Companion.isOnline
import com.dmytroandriichuk.cmediacal.data.ClinicListItem
import com.dmytroandriichuk.cmediacal.data.DataHolder
import com.google.firebase.auth.FirebaseAuth
import kotlin.system.exitProcess

class LandingActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    private var startTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)
        mAuth = FirebaseAuth.getInstance()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(setOf(
//                R.id.navigation_search, R.id.navigation_leave_review, R.id.navigation_favourites))
//        setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setupWithNavController(navController)
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

    fun itemClicked(item: ClinicListItem){
        DataHolder.data = item
        intent = Intent(this@LandingActivity, DetailsActivity::class.java)
        startActivity(intent)
    }
}