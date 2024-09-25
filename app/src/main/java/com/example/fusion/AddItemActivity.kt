package com.example.fusion

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AddItemActivity : AppCompatActivity() {

    // Declare views
    private lateinit var ingredientInputLayout: TextInputLayout
    private lateinit var ingredientEditText: TextInputEditText
    private lateinit var categoryInputLayout: TextInputLayout
    private lateinit var categoryAutoComplete: AutoCompleteTextView
    private lateinit var newCategoryInputLayout: TextInputLayout
    private lateinit var newCategoryEditText: TextInputEditText
    private lateinit var addButton: MaterialButton
    private lateinit var backButton: ImageButton

    private lateinit var databaseReference: DatabaseReference
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val categories = mutableListOf<String>()
    private var selectedCategory: String? = null
    private var isNewCategory = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the activity's layout
        setContentView(R.layout.activity_add_item)

        // Initialize views
        ingredientInputLayout = findViewById(R.id.ingredient_input_layout)
        ingredientEditText = findViewById(R.id.ingredient_edit_text)
        categoryInputLayout = findViewById(R.id.category_input_layout)
        categoryAutoComplete = findViewById(R.id.category_auto_complete)
        newCategoryInputLayout = findViewById(R.id.new_category_input_layout)
        newCategoryEditText = findViewById(R.id.new_category_edit_text)
        addButton = findViewById(R.id.add_button)
        backButton = findViewById(R.id.back_button)

        // Initialize Firebase reference
        if (currentUser != null) {
            databaseReference = FirebaseDatabase.getInstance()
                .getReference("users/${currentUser.uid}/shoppingList")
            fetchCategories()
        } else {
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show()
        }

        // Set up the back button functionality
        backButton.setOnClickListener {
            finish()
        }

        // Set click listener on the add button
        addButton.setOnClickListener {
            addIngredient()
        }
    }

    private fun fetchCategories() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categorySet = HashSet<String>()
                for (itemSnapshot in snapshot.children) {
                    val category = itemSnapshot.child("category").getValue(String::class.java)
                    if (category != null) {
                        categorySet.add(category)
                    }
                }
                categories.clear()
                categories.add("Create new category")  // Add "Create new category" at the top
                categories.addAll(categorySet.sorted())
                setupCategoryDropdown()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AddItemActivity, "Failed to load categories", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupCategoryDropdown() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        categoryAutoComplete.setAdapter(adapter)

        categoryAutoComplete.setOnItemClickListener { parent, view, position, id ->
            val category = categories[position]
            if (category == "Create new category") {
                isNewCategory = true
                newCategoryInputLayout.visibility = View.VISIBLE
                selectedCategory = null
            } else {
                isNewCategory = false
                newCategoryInputLayout.visibility = View.GONE
                selectedCategory = category
            }
            categoryInputLayout.error = null
        }

        // Clear any error when user starts typing
        categoryAutoComplete.setOnTouchListener { _, _ ->
            categoryInputLayout.error = null
            false
        }
    }

    private fun addIngredient() {
        var isValid = true

        val ingredientName = ingredientEditText.text.toString().trim()
        val categoryName = if (isNewCategory) newCategoryEditText.text.toString().trim() else selectedCategory

        // Validate ingredient name
        if (ingredientName.isEmpty()) {
            ingredientInputLayout.error = "Please enter an ingredient"
            isValid = false
        } else {
            ingredientInputLayout.error = null
        }

        // Validate category
        if (categoryName.isNullOrEmpty()) {
            if (isNewCategory) {
                newCategoryInputLayout.error = "Please enter a new category"
            } else {
                categoryInputLayout.error = "Please select or enter a category"
            }
            isValid = false
        } else {
            categoryInputLayout.error = null
            newCategoryInputLayout.error = null
        }

        if (!isValid) {
            return
        }

        // Save to Firebase
        if (currentUser != null) {
            val newItemRef = databaseReference.push()
            val itemData = mapOf(
                "name" to ingredientName,
                "category" to categoryName,
                "checked" to false
            )
            newItemRef.setValue(itemData).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Item added", Toast.LENGTH_SHORT).show()
                    finish()  // Close activity
                } else {
                    Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show()
        }
    }
}
