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
import android.content.Context
import java.util.Locale

class LandingPage : AppCompatActivity() {

    // Declare variables for the Fusion logo ImageView and request code for notifications
    private lateinit var fusionlogo: ImageView
    private val REQUEST_CODE_NOTIFICATIONS = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enable edge-to-edge display
        setContentView(R.layout.app_landing_page) // Set the content view to the landing page layout

        val selectedLanguage = loadLanguagePreference()
        setLocale(selectedLanguage)

        // Initialize the fusion logo ImageView
        fusionlogo = findViewById(R.id.logo)
        requestNotificationPermission() // Request notification permission if necessary

        // Set the logo's alpha to 0 (invisible) and make it visible
        fusionlogo.alpha = 0f
        fusionlogo.visibility = ImageView.VISIBLE

        // Animate the logo to fade in and after the animation, navigate to the LoginPage
        fusionlogo.animate().setDuration(1100).alpha(1f).withEndAction {
            startActivity(Intent(this, LoginPage::class.java)) // Start the LoginPage activity
            finish()  // Close the LandingPage to prevent the user from returning to it
            overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            ) // Add fade transition
        }
    }

    // Request notification permission if needed
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 and above
            // Check if notification permission is already granted
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request the notification permission if not already granted
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_NOTIFICATIONS
                )
            } else {
                // Permission is already granted, proceed with posting notifications
                postNotification()
            }
        } else {
            // For Android versions below 13, no permission request is needed, proceed with notifications
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
            // Check if the notification permission was granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with posting notifications
                postNotification()
            } else {
                // Permission denied, handle accordingly (e.g., disable notification features)
            }
        }
    }

    // Function to post notifications (to be implemented based on app requirements)
    private fun postNotification() {
        // Your code to post the notification goes here
    }

    // Load language preference from SharedPreferences
    private fun loadLanguagePreference(): String {
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        return prefs.getString("selected_language", "en") ?: "en"
    }

    // Set the app locale
    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = resources
        val config = resources.configuration
        config.setLocale(locale)

        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
