package com.example.fusion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

private lateinit var auth: FirebaseAuth


class SignupPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup_page)

        auth = FirebaseAuth.getInstance()

        val usernameEditText = findViewById<EditText>(R.id.txtLUsername)
        val emailEditText = findViewById<EditText>(R.id.edtEmail)
        val passwordEditText = findViewById<EditText>(R.id.txtLPassword)
        val confirmPasswordEditText = findViewById<EditText>(R.id.edtCPassword)
        val registerButton = findViewById<Button>(R.id.button)
        val loginTextView = findViewById<TextView>(R.id.BtnLoginPage)

        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (password == confirmPassword) {
                signUp(username, email, password)
            } else {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
            }
        }

        loginTextView.setOnClickListener {
            val intent = Intent(this@SignupPage, LoginPage::class.java)
            startActivity(intent)
        }
    }

    private fun signUp(username: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d("RegisterPage", "User registration successful")
                saveUserToDatabase(username, email)
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@SignupPage, LoginPage::class.java)
                startActivity(intent)
            } else {
                Log.e("RegisterPage", "User registration failed", task.exception)
                Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserToDatabase(username: String, email: String) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("RegisterPage", "User ID is null")
            Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show()
            return
        }

        val database = FirebaseDatabase.getInstance("https://fusion-14429-default-rtdb.firebaseio.com/")
        val userRef = database.getReference("users")
        val usernameRef = database.getReference("usernames")

        val user = mapOf(
            "username" to username,
            "email" to email
        )
        userRef.child(userId).setValue(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("RegisterPage", "User data saved successfully")
                usernameRef.child(username).setValue(userId).addOnCompleteListener { usernameTask ->
                    if (usernameTask.isSuccessful) {
                        Log.d("RegisterPage", "Username mapping saved successfully")
                        Toast.makeText(this, "User data saved successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("RegisterPage", "Failed to save username mapping", usernameTask.exception)
                        Toast.makeText(this, "Failed to save username mapping: ${usernameTask.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Log.e("RegisterPage", "Failed to save user data", task.exception)
                Toast.makeText(this, "Failed to save user data: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
