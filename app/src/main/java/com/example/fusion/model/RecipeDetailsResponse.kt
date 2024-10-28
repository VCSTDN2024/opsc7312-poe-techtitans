package com.example.fusion.model

import java.io.Serializable

// Data class representing the response for recipe details
data class RecipeDetailsResponse(
    val id: Int,                         // Unique identifier for the recipe
    val title: String,                   // Title of the recipe
    val image: String,                   // URL for the recipe image
    val summary: String,                 // Summary description of the recipe
    val instructions: String?,           // Cooking instructions, can be null
    val extendedIngredients: List<ExtendedIngredient>, // List of extended ingredients used in the recipe
    val nutrition: Nutrition,            // Nutrition information for the recipe
    val readyInMinutes: Int              // <-- Ensure this field exists
) : Serializable                         // Implements Serializable for passing between activities

// Data class representing details of an extended ingredient used in a recipe
data class ExtendedIngredient(
    val id: Int,                         // Unique identifier for the ingredient
    val aisle: String?,                  // Aisle where the ingredient can be found, can be null
    val image: String?,                  // URL for the ingredient image, can be null
    val name: String,                    // Name of the ingredient
    val original: String,                // Original description of the ingredient (e.g., "2 cups of flour")
    val amount: Float?,                  // Amount of the ingredient, can be null
    val unit: String?                    // Unit of measurement for the ingredient, can be null
) : Serializable                         // Implements Serializable for passing between activities

// Data class representing nutrition information for a recipe
data class Nutrition(
    val nutrients: List<Nutrient>,       // List of nutrients present in the recipe
    val caloricBreakdown: CaloricBreakdown, // Caloric breakdown showing percentages of protein, fat, and carbs
    val weightPerServing: WeightPerServing  // Weight per serving information
) : Serializable {                       // Implements Serializable for passing between activities

    // Function to categorize nutrients into different types
    fun getCategorizedNutrients(): Map<String, List<Nutrient>> {
        val categories = mutableMapOf<String, MutableList<Nutrient>>()

        // Define nutrient categories
        val macronutrients = listOf("Calories", "Fat", "Saturated Fat", "Carbohydrates", "Net Carbohydrates", "Sugar", "Cholesterol", "Protein", "Fiber")
        val vitamins = listOf("Vitamin A", "Vitamin C", "Vitamin D", "Vitamin E", "Vitamin K", "Thiamin (B1)", "Riboflavin (B2)", "Niacin (B3)", "Vitamin B6", "Vitamin B12", "Folate", "Pantothenic Acid")
        val minerals = listOf("Calcium", "Iron", "Magnesium", "Phosphorus", "Potassium", "Sodium", "Zinc", "Copper", "Manganese", "Selenium")

        // Initialize categories for macronutrients, vitamins, minerals, and others
        categories["Macronutrients"] = mutableListOf()
        categories["Vitamins"] = mutableListOf()
        categories["Minerals"] = mutableListOf()
        categories["Others"] = mutableListOf()

        // Categorize each nutrient into the appropriate category
        for (nutrient in nutrients) {
            when (nutrient.name) {
                in macronutrients -> categories["Macronutrients"]?.add(nutrient)
                in vitamins -> categories["Vitamins"]?.add(nutrient)
                in minerals -> categories["Minerals"]?.add(nutrient)
                else -> categories["Others"]?.add(nutrient)
            }
        }

        return categories // Return the categorized nutrients as a map
    }
}

