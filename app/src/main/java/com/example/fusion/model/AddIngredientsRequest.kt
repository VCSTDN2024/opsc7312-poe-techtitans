package com.example.fusion.model

data class AddIngredientsRequest(
    val recipeId: Int
)
data class AddIngredientsResponse(
    val message: String
)
data class ShoppingListItem(
    val id: String,
    val name: String,
    val amount: Double,
    val unit: String,
    val category: String
)
