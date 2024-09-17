package com.example.fusion

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

private lateinit var auth: FirebaseAuth


class LoginPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_page)

        auth = FirebaseAuth.getInstance()
        //sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)

        val usernameEditText = findViewById<EditText>(R.id.txtLUsername)
        val passwordEditText = findViewById<EditText>(R.id.txtLPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val createAccountTextView = findViewById<TextView>(R.id.btnSignupPage)
        val createAccountTextView2 = findViewById<TextView>(R.id.btnSignup2)


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

        createAccountTextView2.setOnClickListener {
            navigateToRegisterPage()
        }
    }

    private fun loginUser(username: String, password: String) {
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
                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { signInTask ->
                                        if (signInTask.isSuccessful) {
                                            Log.d("MainActivity", "Login successful")
                                            Toast.makeText(
                                                this,
                                                "Login successful!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            startActivity(Intent(this, HomePage::class.java))
                                            finish()
                                        } else {
                                            Log.e(
                                                "MainActivity",
                                                "Login failed",
                                                signInTask.exception
                                            )
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
                            Log.e("MainActivity", "Failed to retrieve email", emailTask.exception)
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
                Log.e("MainActivity", "Failed to retrieve username mapping", task.exception)
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
}