package com.example.fusion

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.fusion.utils.TranslationUtil
import com.example.fusion.utils.TranslationUtil.loadLanguagePreference
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AddItemActivity : AppCompatActivity() {

    // Declare views for input fields and buttons
    private lateinit var ingredientInputLayout: TextInputLayout
    private lateinit var ingredientEditText: TextInputEditText
    private lateinit var categoryInputLayout: TextInputLayout
    private lateinit var categoryAutoComplete: AutoCompleteTextView
    private lateinit var newCategoryInputLayout: TextInputLayout
    private lateinit var newCategoryEditText: TextInputEditText
    private lateinit var addButton: MaterialButton
    private lateinit var backButton: ImageButton

    // Firebase database reference for current user
    private lateinit var databaseReference: DatabaseReference
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val categories = mutableListOf<String>() // List to store categories
    private var selectedCategory: String? = null // Holds the selected category
    private var isNewCategory = false // Flag to check if a new category is being created

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the activity's layout
        setContentView(R.layout.activity_add_item)

        // Initialize views by finding them in the layout
        ingredientInputLayout = findViewById(R.id.ingredient_input_layout)
        ingredientEditText = findViewById(R.id.ingredient_edit_text)
        categoryInputLayout = findViewById(R.id.category_input_layout)
        categoryAutoComplete = findViewById(R.id.category_auto_complete)
        newCategoryInputLayout = findViewById(R.id.new_category_input_layout)
        newCategoryEditText = findViewById(R.id.new_category_edit_text)
        addButton = findViewById(R.id.add_button)
        backButton = findViewById(R.id.back_button)

        // Initialize Firebase database reference for the user's shopping list
        if (currentUser != null) {
            databaseReference = FirebaseDatabase.getInstance()
                .getReference("users/${currentUser.uid}/shoppingList")
            fetchCategories() // Fetch categories from the database
        } else {
            // Show error if user is not signed in
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show()
        }

        // Set up the back button to close the activity when clicked
        backButton.setOnClickListener {
            finish()
        }

        // Set click listener on the add button to add the ingredient
        addButton.setOnClickListener {
            addIngredient()
        }
        applyTranslations()
    }

    private fun applyTranslations() {
        val textViewsToTranslate = listOf(
            findViewById<TextView>(R.id.header_text),
            findViewById<TextView>(R.id.ingredient_edit_text),
            findViewById<TextView>(R.id.category_auto_complete)
        )

        val buttons = listOf(
            findViewById<Button>(R.id.add_button)
        )


        if (loadLanguagePreference(this) == "af") {
            // Apply translations to these text views if necessary
            TranslationUtil.translateTextViews(this, textViewsToTranslate, "af")
            TranslationUtil.translateButtons(this, buttons, "af")
        }
    }

    // Function to fetch categories from Firebase
    private fun fetchCategories() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categorySet = HashSet<String>() // Use a set to avoid duplicates
                for (itemSnapshot in snapshot.children) {
                    // Get the category for each item in the snapshot
                    val category = itemSnapshot.child("category").getValue(String::class.java)
                    if (category != null) {
                        categorySet.add(category)
                    }
                }
                // Clear and update categories list
                categories.clear()
                categories.add("Create new category")  // Add "Create new category" option at the top
                categories.addAll(categorySet.sorted()) // Add and sort categories
                setupCategoryDropdown() // Set up dropdown with updated categories
            }

            override fun onCancelled(error: DatabaseError) {
                // Show error message if fetching categories fails
                Toast.makeText(
                    this@AddItemActivity,
                    "Failed to load categories",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    // Function to set up the category dropdown list
    private fun setupCategoryDropdown() {
        // Create an ArrayAdapter for the categories dropdown
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        categoryAutoComplete.setAdapter(adapter)

        // Handle category selection from dropdown
        categoryAutoComplete.setOnItemClickListener { parent, view, position, id ->
            val category = categories[position]
            if (category == "Create new category") {
                // Show input for new category if "Create new category" is selected
                isNewCategory = true
                newCategoryInputLayout.visibility = View.VISIBLE
                selectedCategory = null
            } else {
                // Hide new category input and set selected category
                isNewCategory = false
                newCategoryInputLayout.visibility = View.GONE
                selectedCategory = category
            }
            categoryInputLayout.error = null // Clear any error on category input
        }

        // Clear error on touch of the category dropdown
        categoryAutoComplete.setOnTouchListener { _, _ ->
            categoryInputLayout.error = null
            false
        }
    }

    // Function to add the ingredient to the shopping list
    private fun addIngredient() {
        var isValid = true

        // Get the ingredient name and category
        val ingredientName = ingredientEditText.text.toString().trim()
        val categoryName =
            if (isNewCategory) newCategoryEditText.text.toString().trim() else selectedCategory

        // Validate ingredient name
        if (ingredientName.isEmpty()) {
            ingredientInputLayout.error = "Please enter an ingredient"
            isValid = false
        } else {
            ingredientInputLayout.error = null // Clear error if valid
        }

        // Validate category selection or new category input
        if (categoryName.isNullOrEmpty()) {
            if (isNewCategory) {
                newCategoryInputLayout.error = "Please enter a new category"
            } else {
                categoryInputLayout.error = "Please select or enter a category"
            }
            isValid = false
        } else {
            categoryInputLayout.error = null
            newCategoryInputLayout.error = null // Clear error if valid
        }

        if (!isValid) {
            return // Stop if validation fails
        }

        // Save the ingredient to Firebase if the user is signed in
        if (currentUser != null) {
            // Create a new entry in the database
            val newItemRef = databaseReference.push()
            val itemData = mapOf(
                "name" to ingredientName,  // Ingredient name
                "category" to categoryName,  // Category of the ingredient
                "checked" to false  // Mark the ingredient as unchecked by default
            )
            // Add the item to the database
            newItemRef.setValue(itemData).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Show success message and close the activity
                    Toast.makeText(this, "Item added", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    // Show failure message
                    Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // Show error if user is not signed in
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show()
        }
    }
}
