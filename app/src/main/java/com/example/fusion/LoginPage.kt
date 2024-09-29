package com.example.fusion

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.FirebaseApp

// Declare Firebase Authentication instance
private lateinit var auth: FirebaseAuth

class LoginPage : AppCompatActivity() {
    // Declare shared preferences and "Remember Me" checkbox
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var rememberMeCheckBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enable edge-to-edge display
        setContentView(R.layout.activity_login_page)

        // Check if running a test and bypass login
        if (isRunningTest()) {
            // If running a test, skip the login and go directly to HomePage
            startActivity(Intent(this, HomePage::class.java))
            finish()
            return
        }

        // Initialize Firebase and authentication
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        // Initialize shared preferences to store login data
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

        // Get reference to the "Remember Me" checkbox and check login status
        rememberMeCheckBox = findViewById(R.id.checkbox_remember_me)
        checkLogin() // Check if the user has opted to be remembered

        // Set up references to UI components
        val usernameEditText = findViewById<EditText>(R.id.txtLUsername)
        val passwordEditText = findViewById<EditText>(R.id.txtLPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val createAccountTextView = findViewById<TextView>(R.id.btnSignupPage)

        // Set the login button click listener
        loginButton.setOnClickListener {
            // Get username and password from the text fields
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Check if username or password is empty
            if (username.isEmpty() || password.isEmpty()) {
                // Show a Toast if any field is empty
                Toast.makeText(this, "Username or password cannot be blank", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // Attempt to log the user in with the provided credentials
                loginUser(username, password)
            }
        }

        // Set the click listener for creating a new account
        createAccountTextView.setOnClickListener {
            navigateToRegisterPage() // Navigate to the signup page
        }
    }

    // Function to check if the app is running in a test environment
    private fun isRunningTest(): Boolean {
        return "true" == System.getProperty("IS_TESTING")
    }

    // Function to check if the user has opted to be remembered for login
    private fun checkLogin() {
        // Retrieve the "remember_me" preference from shared preferences
        val isRemembered = sharedPreferences.getBoolean("remember_me", false)
        if (isRemembered) {
            // If user is remembered, go to HomePage and finish the login activity
            startActivity(Intent(this, HomePage::class.java))
            finish()
        }
    }

    // Function to handle user login
    private fun loginUser(username: String, password: String) {
        // Get a reference to the Firebase Realtime Database
        val database = FirebaseDatabase.getInstance("https://fusion-14429-default-rtdb.firebaseio.com/")
        val usernameRef = database.getReference("usernames").child(username)

        // Attempt to retrieve the user ID associated with the username
        usernameRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = task.result.getValue(String::class.java)
                if (userId != null) {
                    // If the user ID is found, retrieve the email for that user
                    val userRef = database.getReference("users").child(userId)
                    userRef.child("email").get().addOnCompleteListener { emailTask ->
                        if (emailTask.isSuccessful) {
                            val email = emailTask.result.getValue(String::class.java)
                            if (email != null) {
                                // Attempt to sign in with the retrieved email and password
                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { signInTask ->
                                        if (signInTask.isSuccessful) {
                                            // Save "Remember Me" preference if checked
                                            val editor = sharedPreferences.edit()
                                            editor.putBoolean("remember_me", rememberMeCheckBox.isChecked)
                                            editor.apply()

                                            // Log success and navigate to HomePage
                                            Log.d("LoginPage", "Login successful")
                                            Toast.makeText(
                                                this,
                                                "Login successful!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            startActivity(Intent(this, HomePage::class.java))
                                            finish()
                                        } else {
                                            // Log failure and show error message
                                            Log.e("LoginPage", "Login failed", signInTask.exception)
                                            Toast.makeText(
                                                this,
                                                "Invalid username or password!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            } else {
                                // Show error if email retrieval failed
                                Toast.makeText(
                                    this,
                                    "Failed to retrieve email for the username",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            // Log and show error if email retrieval failed
                            Log.e("LoginPage", "Failed to retrieve email", emailTask.exception)
                            Toast.makeText(
                                this,
                                "Failed to retrieve email: ${emailTask.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    // Show error if the username is not found
                    Toast.makeText(this, "Invalid username", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Log and show error if username retrieval failed
                Log.e("LoginPage", "Failed to retrieve username mapping", task.exception)
                Toast.makeText(
                    this,
                    "Failed to retrieve username mapping: ${task.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Function to navigate to the registration page
    private fun navigateToRegisterPage() {
        startActivity(Intent(this, SignupPage::class.java))
    }

    // Override onDestroy to clear "Remember Me" preference if not checked
    override fun onDestroy() {
        super.onDestroy()
        val editor = sharedPreferences.edit()
        if (!rememberMeCheckBox.isChecked) {
            // Remove "remember_me" from shared preferences if checkbox is unchecked
            editor.remove("remember_me")
            editor.apply()
        }
    }
}
