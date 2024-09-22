package com.example.fusion.model

data class RecipeResponse(
    val results: List<Recipe>
)

data class Recipe(
    val id: Int,
    val title: String,
    val image: String,
    var isSaved: Boolean = false //track saved state
)