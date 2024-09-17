package com.example.fusion

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_page)

        val loginButton = findViewById<Button>(R.id.btnLogin)
        val signupButton = findViewById<Button>(R.id.btnSignup2)
        val usernameTextView = findViewById<EditText>(R.id.txtLUsername)
        val passwordTextView = findViewById<EditText>(R.id.txtLPassword)
        val navigateToSignupButton = findViewById<Button>(R.id.btnSignupPage)

        loginButton.setOnClickListener {
            val username = usernameTextView.text.toString()
            val password = passwordTextView.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomePage::class.java)
                startActivity(intent)
            }
        }

        signupButton.setOnClickListener {
            Toast.makeText(this, "Navigating to Signup Page", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SignupPage::class.java)
            startActivity(intent)
        }

        navigateToSignupButton.setOnClickListener {
            val intent = Intent(this, SignupPage::class.java)
            startActivity(intent)
        }
    }
}
