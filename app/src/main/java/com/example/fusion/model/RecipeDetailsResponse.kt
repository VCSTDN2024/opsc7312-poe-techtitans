package com.example.fusion.model

data class RecipeDetailsResponse(
    val id: Int,
    val title: String,
    val image: String,
    val summary: String,
    val instructions: String?,
    val extendedIngredients: List<ExtendedIngredient>
)

data class ExtendedIngredient(
    val id: Int,
    val aisle: String?,
    val image: String?,
    val name: String,
    val original: String,
    val amount: Float?,
    val unit: String?
)
