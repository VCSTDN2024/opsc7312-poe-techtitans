package com.VCSDTN.fusion.model

// Data class representing the response for a list of recipes
data class RecipeResponse(
    val results: List<Recipe> // List of recipes returned in the response
)

// Data class representing an individual recipe
data class Recipe(
    val id: Int,               // Unique identifier for the recipe
    val title: String,         // Title of the recipe
    val image: String,         // URL for the recipe image
    var isSaved: Boolean = false // Boolean flag to track if the recipe is saved by the user
) {
    // Secondary constructor to create an empty Recipe object with default values
    constructor() : this(0, "", "", false)
}