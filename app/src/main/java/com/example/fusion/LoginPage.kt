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


private lateinit var auth: FirebaseAuth


class LoginPage : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var rememberMeCheckBox: CheckBox
//testing the workflows
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_page)

        if (isRunningTest()) {
            // Skip login and navigate directly to HomePage
            startActivity(Intent(this, HomePage::class.java))
            finish()
            return
        }

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

        rememberMeCheckBox = findViewById(R.id.checkbox_remember_me)
        checkLogin()

        val usernameEditText = findViewById<EditText>(R.id.txtLUsername)
        val passwordEditText = findViewById<EditText>(R.id.txtLPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val createAccountTextView = findViewById<TextView>(R.id.btnSignupPage)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Username or password cannot be blank", Toast.LENGTH_SHORT)
                    .show()
            } else {
                loginUser(username, password)
            }
        }

        createAccountTextView.setOnClickListener {
            navigateToRegisterPage()
        }
    }

    private fun isRunningTest(): Boolean {
        return "true" == System.getProperty("IS_TESTING")
    }

    private fun checkLogin() {
        // Check if user has chosen to be remembered
        val isRemembered = sharedPreferences.getBoolean("remember_me", false)
        if (isRemembered) {
            startActivity(Intent(this, HomePage::class.java))
            finish()
        }
    }

    private fun loginUser(username: String, password: String) {
        val database = FirebaseDatabase.getInstance("https://fusion-14429-default-rtdb.firebaseio.com/")
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
                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { signInTask ->
                                        if (signInTask.isSuccessful) {
                                            // Save the "Remember Me" state
                                            val editor = sharedPreferences.edit()
                                            editor.putBoolean("remember_me", rememberMeCheckBox.isChecked)
                                            editor.apply()

                                            Log.d("LoginPage", "Login successful")
                                            Toast.makeText(
                                                this,
                                                "Login successful!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            startActivity(Intent(this, HomePage::class.java))
                                            finish()
                                        } else {
                                            Log.e("LoginPage", "Login failed", signInTask.exception)
                                            Toast.makeText(
                                                this,
                                                "Invalid username or password!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(
                                    this,
                                    "Failed to retrieve email for the username",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Log.e("LoginPage", "Failed to retrieve email", emailTask.exception)
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
                Log.e("LoginPage", "Failed to retrieve username mapping", task.exception)
                Toast.makeText(
                    this,
                    "Failed to retrieve username mapping: ${task.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun navigateToRegisterPage() {
        startActivity(Intent(this, SignupPage::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        val editor = sharedPreferences.edit()
        if (!rememberMeCheckBox.isChecked) {
            editor.remove("remember_me")
            editor.apply()
        }
    }
}