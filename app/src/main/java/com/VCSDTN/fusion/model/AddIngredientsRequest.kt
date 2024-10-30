package com.VCSDTN.fusion.model

// Data class representing a request to add ingredients
data class AddIngredientsRequest(
    val userID: String,  // User identifier
    val recipeId: Int    // Identifier of the recipe
)

// Data class representing the response after adding ingredients
data class AddIngredientsResponse(
    val message: String  // Response message
)

// Data class representing an item in the shopping list
data class ShoppingListItem(
    val id: String,      // Unique identifier for the shopping list item
    val name: String,    // Name of the ingredient
    val amount: Double,  // Amount of the ingredient
    val unit: String,    // Unit of measurement for the ingredient (e.g., grams, liters)
    val category: String // Category of the ingredient (e.g., dairy, vegetables)
)
