package com.example.fusion

import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.fusion.utils.TranslationUtil
import com.example.fusion.utils.TranslationUtil.loadLanguagePreference
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class editProfilePage : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var tvEmail: TextView
    private lateinit var btnUpdateProfile: Button
    private lateinit var btnChangePassword: Button
    private lateinit var imgChangePfp: ImageView
    private lateinit var imgProfilePicture: ImageView
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile_page)

        // Initialize views
        etUsername = findViewById(R.id.etUsername)
        tvEmail = findViewById(R.id.tvEmail)
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile)
        btnChangePassword = findViewById(R.id.btnChangePassword)
        imgChangePfp = findViewById(R.id.image_ellipse)
        imgProfilePicture = findViewById(R.id.image_ellipse)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            onBackPressed() // Go back when the back arrow is clicked

        }


            // Initialize image picker launcher
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                // Handle the image URI
                uploadProfilePicture(uri)
            }
        }

        // Load current user data
        loadUserData()

        // Set up listeners
        btnUpdateProfile.setOnClickListener {
            updateProfile()
        }

        imgChangePfp.setOnClickListener {
            updatePfp()
        }

        imgProfilePicture.setOnClickListener {
            updatePfp()
        }

        btnChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }
        applyTranslations()
    }

    private fun applyTranslations() {
        val textViewsToTranslate = listOf(
            findViewById<TextView>(R.id.text_edit_profile),
            findViewById<TextView>(R.id.text_name),
            findViewById<TextView>(R.id.text_email)
        )

        val buttons = listOf(
            findViewById<Button>(R.id.btnChangePassword),
            findViewById<Button>(R.id.btnUpdateProfile),
        )


        if(loadLanguagePreference(this) == "af") {
            // Apply translations to these text views if necessary
            TranslationUtil.translateTextViews(this, textViewsToTranslate, "af")
            TranslationUtil.translateButtons(this, buttons, "af")
        }
    }


    private fun updatePfp() {
        // Launch image picker
        imagePickerLauncher.launch("image/*")
    }

    private fun uploadProfilePicture(uri: Uri) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val userId = user.uid

        // Reference to Firebase Storage
        val storageRef = FirebaseStorage.getInstance().getReference("profilePictures/$userId.jpg")

        // Upload file to Firebase Storage
        val uploadTask = storageRef.putFile(uri)

        // Show a progress indicator if needed

        uploadTask.addOnSuccessListener {
            // Get the download URL
            storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                // Save the download URL to the user's profile in the database
                val databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
                databaseRef.child("profilePictureUrl").setValue(downloadUrl.toString())

                // Load the image into the ImageView using Glide
                Glide.with(this)
                    .load(downloadUrl)
                    .into(imgProfilePicture)

                Toast.makeText(this, "Profile picture updated successfully", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Failed to upload profile picture: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUserData() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            val databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

            databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val username = snapshot.child("username").getValue(String::class.java)
                    val email = snapshot.child("email").getValue(String::class.java)
                    val profilePictureUrl = snapshot.child("profilePictureUrl").getValue(String::class.java)

                    etUsername.setText(username)
                    etUsername.tag = username // Store original username
                    tvEmail.text = email

                    // Load profile picture if it exists
                    if (!profilePictureUrl.isNullOrEmpty()) {
                        Glide.with(this@editProfilePage)
                            .load(profilePictureUrl)
                            .placeholder(R.drawable.image_ellipse) // Optional placeholder
                            .into(imgProfilePicture)
                    } else {
                        // Set default profile picture
                        imgProfilePicture.setImageResource(R.drawable.image_ellipse)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@editProfilePage,
                        "Failed to load user data: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    // Rest of your existing code (updateProfile, checkUsernameAvailability, etc.)

    private fun updateProfile() {
        val newUsername = etUsername.text.toString().trim()

        if (newUsername.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val user = FirebaseAuth.getInstance().currentUser ?: return

        if (user.email != null) {
            // Re-authenticate user
            val passwordInput = EditText(this).apply {
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                hint = "Enter your password"
            }

            AlertDialog.Builder(this)
                .setTitle("Re-authentication")
                .setMessage("Please enter your password to continue:")
                .setView(passwordInput)
                .setPositiveButton("Confirm") { _, _ ->
                    val password = passwordInput.text.toString()
                    if (password.isNotEmpty()) {
                        val credential = EmailAuthProvider.getCredential(user.email!!, password)
                        user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                            if (reauthTask.isSuccessful) {
                                // Proceed to update username
                                val oldUsername = etUsername.tag as? String ?: ""
                                if (newUsername != oldUsername) {
                                    // Check if username is available
                                    checkUsernameAvailability(newUsername) { isAvailable ->
                                        if (isAvailable) {
                                            proceedToUpdate(oldUsername, newUsername)
                                        } else {
                                            Toast.makeText(
                                                this,
                                                "Username already taken",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Username is unchanged",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    this,
                                    "Password is incorrect",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkUsernameAvailability(username: String, callback: (Boolean) -> Unit) {
        val usernamesRef = FirebaseDatabase.getInstance().getReference("usernames")
        usernamesRef.child(username).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                callback(!snapshot.exists())
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@editProfilePage,
                    "Error checking username: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
                callback(false)
            }
        })
    }

    private fun proceedToUpdate(oldUsername: String, newUsername: String) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val userId = user.uid
        val updates = HashMap<String, Any>()

        // Update username in the database
        if (newUsername != oldUsername) {
            val databaseRef = FirebaseDatabase.getInstance().getReference()
            // Remove old username mapping
            databaseRef.child("usernames").child(oldUsername).removeValue()

            updates["users/$userId/username"] = newUsername
            updates["usernames/$newUsername"] = userId

            databaseRef.updateChildren(updates).addOnCompleteListener { dbUpdateTask ->
                if (dbUpdateTask.isSuccessful) {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    // Update the tag to the new username
                    etUsername.tag = newUsername
                } else {
                    Toast.makeText(
                        this,
                        "Error updating profile: ${dbUpdateTask.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showChangePasswordDialog() {
        val currentPasswordInput = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            hint = "Current Password"
        }

        val newPasswordInput = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            hint = "New Password"
        }

        val confirmPasswordInput = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            hint = "Confirm New Password"
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
            addView(currentPasswordInput)
            addView(newPasswordInput)
            addView(confirmPasswordInput)
        }

        AlertDialog.Builder(this)
            .setTitle("Change Password")
            .setView(layout)
            .setPositiveButton("Confirm") { _, _ ->
                val currentPassword = currentPasswordInput.text.toString()
                val newPassword = newPasswordInput.text.toString()
                val confirmPassword = confirmPasswordInput.text.toString()

                if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (newPassword != confirmPassword) {
                    Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Call the method to change the password
                changePassword(currentPassword, newPassword)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun changePassword(currentPassword: String, newPassword: String) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null && user.email != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            Toast.makeText(
                                this,
                                "Password updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this,
                                "Error updating password: ${updateTask.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }


        }
}

