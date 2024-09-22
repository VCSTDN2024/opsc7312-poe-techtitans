package com.example.fusion

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsPage : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_page)

        findViewById<TextView>(R.id.txtEditProfile).setOnClickListener {
            Toast.makeText(this, "Edit profile clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, editProfilePage::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.txtNotifications).setOnClickListener {
            Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, NotificationsPage::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.txtLanguage).setOnClickListener {
            Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LanguagePage::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.txtUOM).setOnClickListener {
            Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ConversionsPage::class.java)
            startActivity(intent)
        }



// Initialize bottom navigation view
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Handle window insets to ensure proper padding
        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = systemBars.bottom)
            insets
        }

        // Setup bottom navigation
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.selectedItemId = R.id.navigation_settings

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomePage::class.java))
                    true
                }
                R.id.navigation_saved -> {
                    startActivity(Intent(this, FavoritesPage::class.java))
                    true
                }
                R.id.navigation_calendar -> {
                    startActivity(Intent(this, MealPlannerPage::class.java))
                    true
                }
                R.id.navigation_cart -> {
                    startActivity(Intent(this, ShoppingListPage::class.java))
                    true
                }
                R.id.navigation_settings -> true
                else -> false
            }
        }
    }
}