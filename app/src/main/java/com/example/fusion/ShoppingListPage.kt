package com.example.fusion

import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ShoppingListPage : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var shoppingListContainer: LinearLayout
    private lateinit var bottomNavigationView: BottomNavigationView
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list_page)

        // Initialize the container where the categories will be displayed
        shoppingListContainer = findViewById(R.id.linearLayout_shopping_list)

        // Initialize the bottom navigation view
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Setup navigation item selection listener
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomePage::class.java))
                    true
                }
                R.id.navigation_saved -> {
                    startActivity(Intent(this, FavoritesPage::class.java))
                    true
                }
                R.id.navigation_calendar -> {
                    startActivity(Intent(this, MealPlannerPage::class.java))
                    true
                }
                R.id.navigation_cart -> true
                R.id.navigation_settings -> {
                    startActivity(Intent(this, SettingsPage::class.java))
                    true
                }
                else -> false
            }
        }

        // Firebase Database Reference (User's shopping list path)
        if (currentUser != null) {
            databaseReference = FirebaseDatabase.getInstance()
                .getReference("users/${currentUser.uid}/shoppingList")

            // Fetch and display categories with dropdown functionality
            fetchShoppingListCategories()
        } else {
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchShoppingListCategories() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                shoppingListContainer.removeAllViews()  // Clear previous views

                if (snapshot.exists()) {
                    val categoryItemsMap = HashMap<String, MutableSet<Pair<String, Boolean>>>()

                    // Loop through each item in the shopping list
                    for (itemSnapshot in snapshot.children) {
                        val category = itemSnapshot.child("category").getValue(String::class.java)
                        val itemName = itemSnapshot.child("name").getValue(String::class.java)
                        val isChecked = itemSnapshot.child("checked").getValue(Boolean::class.java) ?: false

                        if (category != null && itemName != null) {
                            // Check if the item is already in the category set
                            val itemExists = categoryItemsMap[category]?.any { it.first == itemName } ?: false
                            if (!itemExists) {
                                if (!categoryItemsMap.containsKey(category)) {
                                    categoryItemsMap[category] = mutableSetOf()
                                }
                                categoryItemsMap[category]?.add(Pair(itemName, isChecked))
                            }
                        }
                    }

                    // Display categories and items with dropdown functionality
                    if (categoryItemsMap.isNotEmpty()) {
                        for ((category, items) in categoryItemsMap) {
                            val categoryTextView = TextView(this@ShoppingListPage).apply {
                                text = "$category (${items.size})"
                                textSize = 18f
                                setPadding(16, 16, 16, 16)
                                setOnClickListener {
                                    toggleItemsVisibility(this, items)
                                }
                            }
                            shoppingListContainer.addView(categoryTextView)
                        }
                    } else {
                        showEmptyMessage()
                    }
                } else {
                    showEmptyMessage()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ShoppingListPage, "Failed to load shopping list", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun toggleItemsVisibility(categoryView: TextView, items: Set<Pair<String, Boolean>>) {
        val isExpanded = categoryView.tag as? Boolean ?: false

        if (isExpanded) {
            val index = shoppingListContainer.indexOfChild(categoryView)
            for (i in 1..items.size) {
                shoppingListContainer.removeViewAt(index + 1)
            }
            categoryView.tag = false
        } else {
            val index = shoppingListContainer.indexOfChild(categoryView)
            for (item in items) {
                val itemView = CheckBox(this).apply {
                    text = item.first
                    isChecked = item.second
                    setPadding(32, 8, 16, 8)
                }
                shoppingListContainer.addView(itemView, index + 1)
            }
            categoryView.tag = true
        }
    }

    private fun showEmptyMessage() {
        val emptyMessageTextView = TextView(this).apply {
            text = "Your shopping list is empty"
            textSize = 24f
            setPadding(16, 16, 16, 16)
        }
        shoppingListContainer.addView(emptyMessageTextView)
    }
}
