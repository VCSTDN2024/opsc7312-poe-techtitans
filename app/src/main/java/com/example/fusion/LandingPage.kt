package com.example.fusion

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class LandingPage : AppCompatActivity() {

    private lateinit var fusionlogo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.app_landing_page)

        fusionlogo = findViewById(R.id.logo)

        fusionlogo.alpha = 0f
        fusionlogo.animate().setDuration(1500).alpha(1f).withEndAction{
            startActivity(Intent(this, LoginPage::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}
