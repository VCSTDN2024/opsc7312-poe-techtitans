package com.example.fusion.model

data class RecipeResponse(
    val results: List<Recipe>
)

data class Recipe(
    val title: String,
    val image: String
)
