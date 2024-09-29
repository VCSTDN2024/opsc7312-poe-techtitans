package com.example.fusion

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SettingsPage : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var auth: FirebaseAuth
    private val databaseUrl = "https://fusion-14429-default-rtdb.firebaseio.com/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        setContentView(R.layout.activity_settings_page)

        // Set click listeners on the MaterialCardViews

        // Edit Profile Click Listener
        findViewById<MaterialCardView>(R.id.cardEditProfile).setOnClickListener {
            val intent = Intent(this, editProfilePage::class.java)
            startActivity(intent)
        }

        // Notifications Click Listener
        findViewById<MaterialCardView>(R.id.cardNotifications).setOnClickListener {
            val intent = Intent(this, NotificationsPage::class.java)
            startActivity(intent)
        }

        // Language Click Listener
        findViewById<MaterialCardView>(R.id.cardLanguage).setOnClickListener {
            val intent = Intent(this, LanguagePage::class.java)
            startActivity(intent)
        }

        // Convert Measurements Click Listener
        findViewById<MaterialCardView>(R.id.cardUOM).setOnClickListener {
            val intent = Intent(this, ConversionsPage::class.java)
            startActivity(intent)
        }

        // Logout Click Listener
        findViewById<MaterialCardView>(R.id.cardLogout).setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginPage::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Delete Account Click Listener
        findViewById<MaterialCardView>(R.id.cardDeleteAccount).setOnClickListener {
            showDeleteAccountDialog()
        }

        // Initialize bottom navigation view
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Setup bottom navigation
        setupBottomNavigation()
    }

    private fun showDeleteAccountDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Account")
        builder.setMessage("Are you sure you want to delete your account? This action cannot be undone.")

        builder.setPositiveButton("Delete") { dialog, which ->
            // Prompt for password
            showPasswordDialog()
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun showPasswordDialog() {
        val passwordInput = EditText(this)
        passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        val passwordDialog = AlertDialog.Builder(this)
            .setTitle("Confirm Password")
            .setMessage("Please enter your password to confirm account deletion:")
            .setView(passwordInput)
            .setPositiveButton("Confirm") { dialog, which ->
                val password = passwordInput.text.toString()
                if (password.isNotEmpty()) {
                    reauthenticateAndDelete(password)
                } else {
                    Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .create()

        passwordDialog.show()
    }

    private fun reauthenticateAndDelete(password: String) {
        val user = auth.currentUser
        if (user != null && user.email != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, password)

            user.reauthenticate(credential)
                .addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        // Delete user data from Realtime Database
                        val userId = user.uid
                        val database = FirebaseDatabase.getInstance(databaseUrl).reference
                        database.child("users").child(userId).removeValue()
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    // Now delete the user from FirebaseAuth
                                    user.delete()
                                        .addOnCompleteListener { deleteTask ->
                                            if (deleteTask.isSuccessful) {
                                                Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show()
                                                val intent = Intent(this, LoginPage::class.java)
                                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                startActivity(intent)
                                                finish()
                                            } else {
                                                Toast.makeText(this, "Failed to delete account: ${deleteTask.exception?.message}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                } else {
                                    Toast.makeText(this, "Failed to delete user data: ${dbTask.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Authentication failed: ${reauthTask.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        } else {
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.selectedItemId = R.id.navigation_settings

        bottomNavigationView.setOnItemSelectedListener { item ->
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
