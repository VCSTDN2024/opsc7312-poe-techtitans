package com.VCSDTN.fusion

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.VCSDTN.fusion.utils.TranslationUtil
import com.VCSDTN.fusion.utils.TranslationUtil.loadLanguagePreference
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SettingsPage : AppCompatActivity() {

    // Declare variables for Firebase authentication and bottom navigation
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var auth: FirebaseAuth
    private val databaseUrl = "https://fusion-14429-default-rtdb.firebaseio.com/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Set the layout for the settings page
        setContentView(R.layout.activity_settings_page)

        // Set click listeners for each settings option (MaterialCardViews)

        // Edit Profile Click Listener
        findViewById<MaterialCardView>(R.id.cardEditProfile).setOnClickListener {
            val intent = Intent(this, editProfilePage::class.java)
            startActivity(intent)  // Navigate to the edit profile page
        }

        // Notifications Click Listener
        findViewById<MaterialCardView>(R.id.cardNotifications).setOnClickListener {
            val intent = Intent(this, NotificationsPage::class.java)
            startActivity(intent)  // Navigate to the notifications settings page
        }

        // Language Click Listener
        findViewById<MaterialCardView>(R.id.cardLanguage).setOnClickListener {
            val intent = Intent(this, LanguagePage::class.java)
            startActivity(intent)  // Navigate to the language settings page
        }

        // Convert Measurements Click Listener
        findViewById<MaterialCardView>(R.id.cardUOM).setOnClickListener {
            val intent = Intent(this, ConversionsPage::class.java)
            startActivity(intent)  // Navigate to the conversions page
        }

        // Logout Click Listener
        findViewById<MaterialCardView>(R.id.cardLogout).setOnClickListener {
            auth.signOut()  // Sign out the current user
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginPage::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK  // Clear task stack and start LoginPage
            startActivity(intent)
            finish()
        }

        // Delete Account Click Listener
        findViewById<MaterialCardView>(R.id.cardDeleteAccount).setOnClickListener {
            showDeleteAccountDialog()  // Show confirmation dialog for deleting the account
        }

        // Initialize bottom navigation view
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Set up bottom navigation functionality
        setupBottomNavigation()
        applyTranslations()
    }

    private fun applyTranslations() {
        val textViewsToTranslate = listOf(
            findViewById<TextView>(R.id.text_settings),
            findViewById<TextView>(R.id.txtEditProfile),
            findViewById<TextView>(R.id.txtNotifications),
            findViewById<TextView>(R.id.txtLanguage),
            findViewById<TextView>(R.id.txtUOM),
            findViewById<TextView>(R.id.txtLogout),
            findViewById<TextView>(R.id.txtDelAcc)
        )

        if (loadLanguagePreference(this) == "af") {
            // Apply translations to these text views if necessary
            TranslationUtil.translateTextViews(this, textViewsToTranslate, "af")
        }
    }

    // Function to show a confirmation dialog for account deletion
    private fun showDeleteAccountDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Account")
        builder.setMessage("Are you sure you want to delete your account? This action cannot be undone.")

        // If confirmed, show the password input dialog
        builder.setPositiveButton("Delete") { dialog, which ->
            showPasswordDialog()
        }

        // If canceled, dismiss the dialog
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }

        builder.show()  // Display the dialog
    }

    // Function to prompt the user to enter their password before account deletion
    private fun showPasswordDialog() {
        val passwordInput = EditText(this)
        passwordInput.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD  // Set input type to password

        val passwordDialog = AlertDialog.Builder(this)
            .setTitle("Confirm Password")
            .setMessage("Please enter your password to confirm account deletion:")
            .setView(passwordInput)  // Set password input field in dialog
            .setPositiveButton("Confirm") { dialog, which ->
                val password = passwordInput.text.toString()
                if (password.isNotEmpty()) {
                    reauthenticateAndDelete(password)  // Reauthenticate user and delete account
                } else {
                    Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()  // Dismiss dialog if canceled
            }
            .create()

        passwordDialog.show()  // Display the dialog
    }

    // Function to reauthenticate the user and delete their account
    private fun reauthenticateAndDelete(password: String) {
        val user = auth.currentUser
        if (user != null && user.email != null) {
            // Get credentials for reauthentication using email and password
            val credential = EmailAuthProvider.getCredential(user.email!!, password)

            // Reauthenticate the user
            user.reauthenticate(credential)
                .addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        val userId = user.uid
                        val database = FirebaseDatabase.getInstance(databaseUrl).reference

                        // Retrieve the username from the database
                        database.child("users").child(userId).child("username").get()
                            .addOnSuccessListener { dataSnapshot ->
                                val username = dataSnapshot.getValue(String::class.java)
                                if (username != null) {
                                    // Delete the username mapping from the database
                                    database.child("usernames").child(username).removeValue()
                                        .addOnCompleteListener { usernameDeleteTask ->
                                            if (usernameDeleteTask.isSuccessful) {
                                                // Delete the user's data from the database
                                                database.child("users").child(userId).removeValue()
                                                    .addOnCompleteListener { dbTask ->
                                                        if (dbTask.isSuccessful) {
                                                            // Delete the user's account from FirebaseAuth
                                                            user.delete()
                                                                .addOnCompleteListener { deleteTask ->
                                                                    if (deleteTask.isSuccessful) {
                                                                        Toast.makeText(
                                                                            this,
                                                                            "Account deleted successfully",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                        val intent = Intent(
                                                                            this,
                                                                            LoginPage::class.java
                                                                        )
                                                                        intent.flags =
                                                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                                        startActivity(intent)  // Return to the login page
                                                                        finish()
                                                                    } else {
                                                                        Toast.makeText(
                                                                            this,
                                                                            "Failed to delete account: ${deleteTask.exception?.message}",
                                                                            Toast.LENGTH_LONG
                                                                        ).show()
                                                                    }
                                                                }
                                                        } else {
                                                            Toast.makeText(
                                                                this,
                                                                "Failed to delete user data: ${dbTask.exception?.message}",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        }
                                                    }
                                            } else {
                                                Toast.makeText(
                                                    this,
                                                    "Failed to delete username mapping: ${usernameDeleteTask.exception?.message}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                } else {
                                    Toast.makeText(this, "Username not found", Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(
                                    this,
                                    "Failed to retrieve username: ${exception.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    } else {
                        Toast.makeText(
                            this,
                            "Authentication failed: ${reauthTask.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        } else {
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to set up bottom navigation functionality
    private fun setupBottomNavigation() {
        // Set the current selected item as "Settings"
        bottomNavigationView.selectedItemId = R.id.navigation_settings

        // Handle navigation item selections
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomePage::class.java))  // Navigate to HomePage
                    true
                }

                R.id.navigation_saved -> {
                    startActivity(
                        Intent(
                            this,
                            FavoritesPage::class.java
                        )
                    )  // Navigate to FavoritesPage
                    true
                }

                R.id.navigation_calendar -> {
                    startActivity(
                        Intent(
                            this,
                            MealPlannerPage::class.java
                        )
                    )  // Navigate to MealPlannerPage
                    true
                }

                R.id.navigation_cart -> {
                    startActivity(
                        Intent(
                            this,
                            ShoppingListPage::class.java
                        )
                    )  // Navigate to ShoppingListPage
                    true
                }

                R.id.navigation_settings -> true  // Stay on the current page (Settings)
                else -> false
            }
        }
    }
}
