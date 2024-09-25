package com.example.fusion

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest

class LandingPage : AppCompatActivity() {

    private lateinit var fusionlogo: ImageView
    private val REQUEST_CODE_NOTIFICATIONS = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.app_landing_page)

        fusionlogo = findViewById(R.id.logo)
        requestNotificationPermission()

        fusionlogo.alpha = 0f
        fusionlogo.animate().setDuration(1500).alpha(1f).withEndAction{
            startActivity(Intent(this, LoginPage::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if the permission is already granted
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request the permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_NOTIFICATIONS
                )
            } else {
                // Permission already granted, proceed with posting notifications
                postNotification()
            }
        } else {
            // For Android versions below 13, proceed without requesting permission
            postNotification()
        }
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_NOTIFICATIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with posting notifications
                postNotification()
            } else {
                // Permission denied, handle accordingly
                // You can disable notification features or inform the user
            }
        }
    }

    private fun postNotification() {
        // Your code to post the notification goes here
    }
}

