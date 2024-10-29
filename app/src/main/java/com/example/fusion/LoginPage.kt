package com.example.fusion

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.core.content.ContextCompat
import com.example.fusion.utils.TranslationUtil
import com.example.fusion.utils.TranslationUtil.loadLanguagePreference
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.Executor

// Declare Firebase Authentication instance
private lateinit var auth: FirebaseAuth

class LoginPage : AppCompatActivity() {
    // Declare shared preferences and "Remember Me" checkbox
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var rememberMeCheckBox: CheckBox

    // Biometric variables
    private lateinit var biometricPrompt: androidx.biometric.BiometricPrompt
    private lateinit var promptInfo: androidx.biometric.BiometricPrompt.PromptInfo
    private lateinit var executor: Executor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enable edge-to-edge display
        setContentView(R.layout.activity_login_page)

        // Initialize Firebase and authentication
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        // Initialize shared preferences to store login data
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

        // Initialize biometric components
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = androidx.biometric.BiometricPrompt(
            this,
            executor,
            object : androidx.biometric.BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        applicationContext,
                        "Authentication error: $errString",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("LoginPage", "Authentication error: $errString")
                }

                override fun onAuthenticationSucceeded(result: androidx.biometric.BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(
                        applicationContext,
                        "Authentication succeeded!",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("LoginPage", "Authentication succeeded")
                    // Perform biometric login
                    biometricLogin()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT)
                        .show()
                    Log.d("LoginPage", "Authentication failed")
                }
            })

        promptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Login")
            .setSubtitle("Log in using your fingerprint")
            .setNegativeButtonText("Cancel")
            .build()

        // Get reference to the "Remember Me" checkbox and check login status
        rememberMeCheckBox = findViewById(R.id.checkbox_remember_me)
        checkLogin() // Check if biometric login is enabled

        // Set up references to UI components
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val createAccountTextView = findViewById<TextView>(R.id.btnSignupPage)

        // Set the login button click listener
        loginButton.setOnClickListener {
            updatedLoginButtonClickListener()
        }

        // Set the click listener for creating a new account
        createAccountTextView.setOnClickListener {
            navigateToRegisterPage() // Navigate to the signup page
        }

        applyTranslations()
    }

    private fun applyTranslations() {
        val textViewsToTranslate = listOf(
            findViewById<TextView>(R.id.container_pass),
            findViewById<TextView>(R.id.text_view_password)
        )

        val buttons = listOf(
            findViewById<Button>(R.id.btnLogin),
            findViewById<Button>(R.id.btnSignupPage),
            findViewById<Button>(R.id.btnLoginPage)
        )

        val checkBoxes = listOf(
            findViewById<CheckBox>(R.id.checkbox_remember_me)
        )

        if (loadLanguagePreference(this) == "af") {
            // Apply translations to these text views if necessary
            TranslationUtil.translateTextViews(this, textViewsToTranslate, "af")
            TranslationUtil.translateCheckBoxes(this, checkBoxes, "af")
            TranslationUtil.translateButtons(this, buttons, "af")
        }
    }

    // Function to check if the user has opted to use biometric login
    private fun checkLogin() {
        Log.d("LoginPage", "checkLogin() called")
        if (isBiometricLoginEnabled()) {
            Log.d("LoginPage", "Biometric login is enabled")
            if (isBiometricSupported()) {
                Log.d("LoginPage", "Biometric is supported")
                // Delay the biometric prompt to avoid conflicts with system dialogs
                Handler(Looper.getMainLooper()).postDelayed({
                    showBiometricPrompt()
                }, 500)
            } else {
                Log.d("LoginPage", "Biometric authentication not supported")
                Toast.makeText(this, "Biometric authentication not supported", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Log.d("LoginPage", "Biometric login is not enabled")
        }
    }

    // Check if the device supports biometric authentication
    private fun isBiometricSupported(): Boolean {
        val biometricManager = BiometricManager.from(this)
        val result =
            biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        return when (result) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.d("LoginPage", "App can authenticate using biometrics.")
                true
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.e("LoginPage", "No biometric hardware available.")
                false
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.e("LoginPage", "Biometric hardware is currently unavailable.")
                false
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.e("LoginPage", "No biometric credentials enrolled.")
                false
            }

            else -> {
                Log.e("LoginPage", "Unknown biometric error.")
                false
            }
        }
    }

    // Check if biometric login is enabled in preferences
    private fun isBiometricLoginEnabled(): Boolean {
        val enabled = sharedPreferences.getBoolean("biometric_login_enabled", false)
        Log.d("LoginPage", "Biometric login enabled: $enabled")
        return enabled
    }

    // Show the biometric prompt to the user
    private fun showBiometricPrompt() {
        biometricPrompt.authenticate(promptInfo)
    }

    // Prompt user to enable biometric login
    private fun showEnableBiometricDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enable Biometric Login")
        builder.setMessage("Would you like to enable biometric login for faster access?")
        builder.setPositiveButton("Yes") { _, _ ->
            // Save preference
            val editor = sharedPreferences.edit()
            editor.putBoolean("biometric_login_enabled", true)
            editor.putString("username_biometric", sharedPreferences.getString("username", null))
            editor.putString("password_biometric", sharedPreferences.getString("password", null))
            editor.apply()
            Log.d("LoginPage", "Biometric login enabled and credentials saved.")
            proceedToHomePage()
        }
        builder.setNegativeButton("No") { _, _ ->
            proceedToHomePage()
        }
        builder.setCancelable(false)
        builder.show()
    }

    // Navigate to HomePage
    private fun proceedToHomePage() {
        startActivity(Intent(this, HomePage::class.java))
        finish()
    }

    // Function to handle user login
    private fun loginUser(username: String, password: String) {
        // Get a reference to the Firebase Realtime Database
        val database =
            FirebaseDatabase.getInstance("https://fusion-14429-default-rtdb.firebaseio.com/")
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
                                            // Save login credentials
                                            val editor = sharedPreferences.edit()
                                            editor.putString("username", username)
                                            editor.putString("password", password)
                                            // Save "Remember Me" preference
                                            editor.putBoolean(
                                                "remember_me",
                                                rememberMeCheckBox.isChecked
                                            )
                                            editor.apply()
                                            // Log success
                                            Log.d("LoginPage", "Login successful")
                                            Toast.makeText(
                                                this,
                                                "Login successful!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            // Prompt to enable biometric login
                                            if (isBiometricSupported()) {
                                                showEnableBiometricDialog()
                                            } else {
                                                proceedToHomePage()
                                            }
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

    // Function to perform offline login using shared preferences
    private fun offlineLogin(username: String, password: String): Boolean {
        val savedUsername = sharedPreferences.getString("username", null)
        val savedPassword = sharedPreferences.getString("password", null)
        Log.d("offlineLogin", "Username = $savedUsername")
        Log.d("offlineLogin", "Password = $savedPassword")

        return savedUsername == username && savedPassword == password
    }

    // Function to perform biometric login using stored credentials
    private fun biometricLogin() {
        val username = sharedPreferences.getString("username_biometric", null)
        val password = sharedPreferences.getString("password_biometric", null)
        if (username != null && password != null) {
            // Fetch email from the database using the username
            val database =
                FirebaseDatabase.getInstance("https://fusion-14429-default-rtdb.firebaseio.com/")
            val usernameRef = database.getReference("usernames").child(username)

            usernameRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result.getValue(String::class.java)
                    if (userId != null) {
                        val userRef = database.getReference("users").child(userId)
                        userRef.child("email").get().addOnCompleteListener { emailTask ->
                            if (emailTask.isSuccessful) {
                                val email = emailTask.result.getValue(String::class.java)
                                if (email != null) {
                                    // Authenticate with Firebase Auth
                                    auth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener { signInTask ->
                                            if (signInTask.isSuccessful) {
                                                Toast.makeText(
                                                    this,
                                                    "Login successful!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                proceedToHomePage()
                                            } else {
                                                Toast.makeText(
                                                    this,
                                                    "Authentication failed: ${signInTask.exception?.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Failed to retrieve email",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    this,
                                    "Failed to retrieve email: ${emailTask.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Invalid username", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Failed to retrieve username mapping: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(this, "No credentials stored for biometric login", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // Update the login button click listener to support offline login
    private fun updatedLoginButtonClickListener() {
        val usernameEditText = findViewById<EditText>(R.id.txtLUsername)
        val passwordEditText = findViewById<EditText>(R.id.txtLPassword)

        val username = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username or password cannot be blank", Toast.LENGTH_SHORT).show()
        } else {
            if (offlineLogin(username, password)) {
                // Offline login successful
                Toast.makeText(this, "Offline login successful!", Toast.LENGTH_SHORT).show()
                // Prompt to enable biometric login
                if (isBiometricSupported()) {
                    showEnableBiometricDialog()
                } else {
                    proceedToHomePage()
                }
            } else {
                // Attempt online login if offline login fails
                loginUser(username, password)
            }
        }
    }

    // Override onDestroy to clear "Remember Me" preference if not checked
    override fun onDestroy() {
        super.onDestroy()
        val editor = sharedPreferences.edit()
        if (!rememberMeCheckBox.isChecked) {
            // Remove "remember_me" from shared preferences
            editor.remove("remember_me")
            editor.apply()
        }
    }
}