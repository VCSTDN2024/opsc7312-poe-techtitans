package com.example.fusion

import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.ImageView
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
    private lateinit var bottomNavigationView: BottomNavigationView // Declare it here
    private val currentUser = FirebaseAuth.getInstance().currentUser

    // Set to keep track of expanded categories by name
    private val expandedCategories = HashSet<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list_page)

        // Initialize views
        shoppingListContainer = findViewById(R.id.linearLayout_shopping_list)
        // Set up Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        setupBottomNavigation()
        val addToListIcon: ImageView = findViewById(R.id.add_to_list_icon)

        // Set click listener on the add icon to open AddItemActivity
        addToListIcon.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
        }



        // Initialize Firebase reference
        if (currentUser != null) {
            databaseReference = FirebaseDatabase.getInstance()
                .getReference("users/${currentUser.uid}/shoppingList")

            // Fetch and display categories with dropdown functionality
            fetchShoppingListCategories()
        } else {
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show()
        }
    }
    private fun setupBottomNavigation() {
        bottomNavigationView.selectedItemId = R.id.navigation_cart

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
                R.id.navigation_cart -> {
                    true
                }
                R.id.navigation_settings -> {
                    startActivity(Intent(this, SettingsPage::class.java))
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh the shopping list when returning to this activity
        fetchShoppingListCategories()
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
                            if (!categoryItemsMap.containsKey(category)) {
                                categoryItemsMap[category] = mutableSetOf()
                            }
                            categoryItemsMap[category]?.add(Pair(itemName, isChecked))
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
                                    toggleItemsVisibility(this, category, items)
                                }
                            }
                            shoppingListContainer.addView(categoryTextView)

                            // If category is expanded, automatically expand it
                            if (expandedCategories.contains(category)) {
                                expandCategory(categoryTextView, category, items)
                                categoryTextView.tag = true
                            } else {
                                categoryTextView.tag = false
                            }
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

    private fun toggleItemsVisibility(categoryView: TextView, categoryName: String, items: Set<Pair<String, Boolean>>) {
        val isExpanded = categoryView.tag as? Boolean ?: false

        if (isExpanded) {
            // Collapse the category
            val index = shoppingListContainer.indexOfChild(categoryView)
            for (i in items.indices) {
                shoppingListContainer.removeViewAt(index + 1)
            }
            categoryView.tag = false
            expandedCategories.remove(categoryName)
        } else {
            // Expand the category
            expandCategory(categoryView, categoryName, items)
            categoryView.tag = true
            expandedCategories.add(categoryName)
        }
    }

    private fun expandCategory(categoryView: TextView, categoryName: String, items: Set<Pair<String, Boolean>>) {
        val index = shoppingListContainer.indexOfChild(categoryView)
        var position = index + 1
        for (item in items) {
            val itemView = CheckBox(this).apply {
                text = item.first
                isChecked = item.second
                setPadding(32, 8, 16, 8)
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        removeIngredientFromFirebase(item.first, categoryName)
                    }
                }
            }
            shoppingListContainer.addView(itemView, position)
            position++
        }
    }

    private fun removeIngredientFromFirebase(ingredientName: String, categoryName: String) {
        if (currentUser != null) {
            val shoppingListRef = FirebaseDatabase.getInstance()
                .getReference("users/${currentUser.uid}/shoppingList")

            shoppingListRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val itemsToRemove = mutableListOf<DatabaseReference>()
                    for (itemSnapshot in snapshot.children) {
                        val itemName = itemSnapshot.child("name").getValue(String::class.java)
                        if (itemName == ingredientName) {
                            // Collect references to items to remove
                            itemsToRemove.add(itemSnapshot.ref)
                        }
                    }
                    // Remove all matching items
                    for (ref in itemsToRemove) {
                        ref.removeValue()
                    }
                    // Refresh the UI
                    fetchShoppingListCategories()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ShoppingListPage, "Failed to remove item", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show()
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
