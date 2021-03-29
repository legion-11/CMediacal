package com.dmytroandriichuk.cmediacal

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.dmytroandriichuk.cmediacal.adapter.validate.ValidateViewPagerAdapter
import com.dmytroandriichuk.cmediacal.data.ClinicListItem
import com.dmytroandriichuk.cmediacal.data.DataHolder
import com.dmytroandriichuk.cmediacal.data.ValidationData
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlin.system.exitProcess

class ValidateImageActivity : AppCompatActivity(), ValidateViewPagerAdapter.OnImageClickListener {

    private lateinit var viewPager2: ViewPager2
    private lateinit var descriptionTV: TextView
    private lateinit var descriptionCard: MaterialCardView
    private lateinit var functions: FirebaseFunctions
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Is information right?"
        setContentView(R.layout.activity_validate_image)
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        functions = Firebase.functions
        auth = FirebaseAuth.getInstance()

        descriptionTV = findViewById(R.id.validateTextView)
        descriptionCard = findViewById(R.id.validateCard)

        val data = DataHolder.validationData
        setText(data?.clinicListItem)
        viewPager2 = findViewById(R.id.validateViewPager2)
        val array = data?.images ?: emptyArray()

        viewPager2.adapter = ValidateViewPagerAdapter(array, this)

        findViewById<Button>(R.id.trueButton).setOnClickListener {
            Log.d("ValidateImageActivity", "onCreate: press yes")
            DataHolder.validationData = null
            if (auth.currentUser != null) {
                val resultData = hashMapOf(
                        "result" to true,
                        "userId" to auth.currentUser!!.uid,
                        "imageId" to data?.id
                )
                functions.getHttpsCallable("vote").call(resultData)
            }
            finish()
        }

        findViewById<Button>(R.id.falseButton).setOnClickListener {
            Log.d("ValidateImageActivity", "onCreate: press no")
            DataHolder.validationData = null
            if (auth.currentUser != null) {
                val resultData = hashMapOf(
                        "result" to false,
                        "userId" to auth.currentUser!!.uid,
                        "imageId" to data?.id
                )
                functions.getHttpsCallable("vote").call(resultData)
            }
            finish()
        }
    }

    private fun setText(imageDescription: ClinicListItem?){
        val name = imageDescription?.clinic?.name
        val services = imageDescription?.servicePrices?.joinToString { "${it.serviceName} - ${it.price}$" }
        descriptionTV.text = "Name: $name\n" + services
    }

    override fun onImageClick() {
        descriptionCard.visibility = if (descriptionCard.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    override fun onBackPressed() {}
}