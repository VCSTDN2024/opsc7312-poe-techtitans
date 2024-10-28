package com.example.fusion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.fusion.utils.TranslationUtil
import com.example.fusion.utils.TranslationUtil.loadLanguagePreference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

// Declare Firebase Authentication instance
private lateinit var auth: FirebaseAuth

class SignupPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // Enable edge-to-edge experience (optional)
        setContentView(R.layout.activity_signup_page)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize UI components
        val usernameEditText = findViewById<EditText>(R.id.txtLUsername)
        val emailEditText = findViewById<EditText>(R.id.edtEmail)
        val passwordEditText = findViewById<EditText>(R.id.txtLPassword)
        val confirmPasswordEditText = findViewById<EditText>(R.id.edtCPassword)
        val registerButton = findViewById<Button>(R.id.button)
        val loginTextView = findViewById<TextView>(R.id.btnLoginPage2)

        // Handle user registration
        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            // Check if passwords match
            if (password == confirmPassword) {
                signUp(username, email, password)  // Proceed with signup
            } else {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()  // Show error if passwords don't match
            }
        }

        // Navigate to login page when login button is clicked
        loginTextView.setOnClickListener {
            val intent = Intent(this@SignupPage, LoginPage::class.java)
            startActivity(intent)
        }

        applyTranslations()
    }

    private fun applyTranslations() {
        val textViewsToTranslate = listOf(
            findViewById<TextView>(R.id.container_pass1),
            findViewById<TextView>(R.id.text_email),
            findViewById<TextView>(R.id.textView),
            findViewById<TextView>(R.id.container_pass)
            )

        val buttons = listOf(
            findViewById<Button>(R.id.button),
            findViewById<Button>(R.id.btnSignupPage2),
            findViewById<Button>(R.id.btnLoginPage2)
        )


        if(loadLanguagePreference(this) == "af") {
            // Apply translations to these text views if necessary
            TranslationUtil.translateTextViews(this, textViewsToTranslate, "af")
            TranslationUtil.translateButtons(this, buttons, "af")
        }
    }

    // Function to sign up a new user
    private fun signUp(username: String, email: String, password: String) {
        // Create a new user with email and password in Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d("RegisterPage", "User registration successful")  // Log success
                saveUserToDatabase(username, email)  // Save user details to the Firebase Realtime Database
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()

                // Navigate to login page after successful registration
                val intent = Intent(this@SignupPage, LoginPage::class.java)
                startActivity(intent)
            } else {
                // Handle registration failure
                Log.e("RegisterPage", "User registration failed", task.exception)
                Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to save user information to Firebase Realtime Database
    private fun saveUserToDatabase(username: String, email: String) {
        val userId = auth.currentUser?.uid  // Get the current user's ID
        if (userId == null) {
            Log.e("RegisterPage", "User ID is null")  // Log error if user ID is null
            Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show()
            return
        }

        // Get Firebase Database references
        val database = FirebaseDatabase.getInstance("https://fusion-14429-default-rtdb.firebaseio.com/")
        val userRef = database.getReference("users")
        val usernameRef = database.getReference("usernames")

        // Create a map to store the user's information
        val user = mapOf(
            "username" to username,
            "email" to email
        )

        // Save the user information to the database under the user's UID
        userRef.child(userId).setValue(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("RegisterPage", "User data saved successfully")  // Log success

                // Save the username-to-UID mapping in the database
                usernameRef.child(username).setValue(userId).addOnCompleteListener { usernameTask ->
                    if (usernameTask.isSuccessful) {
                        Log.d("RegisterPage", "Username mapping saved successfully")  // Log success
                        Toast.makeText(this, "User data saved successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        // Handle failure to save the username mapping
                        Log.e("RegisterPage", "Failed to save username mapping", usernameTask.exception)
                        Toast.makeText(this, "Failed to save username mapping: ${usernameTask.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // Handle failure to save user data
                Log.e("RegisterPage", "Failed to save user data", task.exception)
                Toast.makeText(this, "Failed to save user data: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
