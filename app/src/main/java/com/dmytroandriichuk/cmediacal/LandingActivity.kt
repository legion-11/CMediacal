package com.dmytroandriichuk.cmediacal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dmytroandriichuk.cmediacal.MainActivity.Companion
import com.dmytroandriichuk.cmediacal.MainActivity.Companion.isOnline
import com.google.firebase.auth.FirebaseAuth
import kotlin.system.exitProcess

class LandingActivity : AppCompatActivity() {

    private var startTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(setOf(
//                R.id.navigation_search, R.id.navigation_leave_review, R.id.navigation_favourites))
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

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
}