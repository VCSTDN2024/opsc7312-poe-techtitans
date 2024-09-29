package com.example.fusion.model

import java.io.Serializable

// Data class representing a nutrient in a food item
data class Nutrient(
    val name: String,                 // Name of the nutrient (e.g., Protein, Fat)
    val amount: Double,               // Amount of the nutrient
    val unit: String,                 // Unit of measurement (e.g., grams, milligrams)
    val percentOfDailyNeeds: Double   // Percentage of daily recommended intake
) : Serializable                      // Implements Serializable to allow passing this object between activities

// Data class representing the caloric breakdown of a food item
data class CaloricBreakdown(
    val percentProtein: Double,       // Percentage of calories from protein
    val percentFat: Double,           // Percentage of calories from fat
    val percentCarbs: Double          // Percentage of calories from carbohydrates
) : Serializable                      // Implements Serializable to allow passing this object between activities

// Data class representing the weight of a serving of a food item
data class WeightPerServing(
    val amount: Int,                  // Amount of the serving
    val unit: String                  // Unit of measurement (e.g., grams, ounces)
) : Serializable                      // Implements Serializable to allow passing this object between activities