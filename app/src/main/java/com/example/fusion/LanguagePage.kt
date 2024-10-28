package com.example.fusion

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LanguagePage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_language_page)


        // Back arrow functionality to go back to the previous screen
        findViewById<ImageView>(R.id.ImgBack2).setOnClickListener {
            onBackPressed() // Go back when the back arrow is clicked

        }
    }
}