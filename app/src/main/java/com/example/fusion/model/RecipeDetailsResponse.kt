package com.example.fusion.model

import java.io.Serializable

data class RecipeDetailsResponse(
    val id: Int,
    val title: String,
    val image: String,
    val summary: String,
    val instructions: String?,
    val extendedIngredients: List<ExtendedIngredient>,
    val nutrition: Nutrition  // Added nutrition field
) : Serializable

data class ExtendedIngredient(
    val id: Int,
    val aisle: String?,
    val image: String?,
    val name: String,
    val original: String,
    val amount: Float?,
    val unit: String?
) : Serializable

data class Nutrition(
    val nutrients: List<Nutrient>,
    val caloricBreakdown: CaloricBreakdown,
    val weightPerServing: WeightPerServing
) : Serializable {
    // Function to categorize nutrients
    fun getCategorizedNutrients(): Map<String, List<Nutrient>> {
        val categories = mutableMapOf<String, MutableList<Nutrient>>()

        // Define nutrient categories
        val macronutrients = listOf("Calories", "Fat", "Saturated Fat", "Carbohydrates", "Net Carbohydrates", "Sugar", "Cholesterol", "Protein", "Fiber")
        val vitamins = listOf("Vitamin A", "Vitamin C", "Vitamin D", "Vitamin E", "Vitamin K", "Thiamin (B1)", "Riboflavin (B2)", "Niacin (B3)", "Vitamin B6", "Vitamin B12", "Folate", "Pantothenic Acid")
        val minerals = listOf("Calcium", "Iron", "Magnesium", "Phosphorus", "Potassium", "Sodium", "Zinc", "Copper", "Manganese", "Selenium")

        // Initialize categories
        categories["Macronutrients"] = mutableListOf()
        categories["Vitamins"] = mutableListOf()
        categories["Minerals"] = mutableListOf()
        categories["Others"] = mutableListOf()

        for (nutrient in nutrients) {
            when (nutrient.name) {
                in macronutrients -> categories["Macronutrients"]?.add(nutrient)
                in vitamins -> categories["Vitamins"]?.add(nutrient)
                in minerals -> categories["Minerals"]?.add(nutrient)
                else -> categories["Others"]?.add(nutrient)
            }
        }

        return categories
    }
}

