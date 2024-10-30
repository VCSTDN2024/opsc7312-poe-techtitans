package com.VCSDTN.fusion

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class NotificationsPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notifications_page)

        // Back arrow functionality to go back to the previous screen
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            onBackPressed() // Go back when the back arrow is clicked


        }
    }
}