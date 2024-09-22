package com.example.fusion.model

import java.io.Serializable

data class Nutrient(
    val name: String,
    val amount: Double,
    val unit: String,
    val percentOfDailyNeeds: Double
) : Serializable

data class CaloricBreakdown(
    val percentProtein: Double,
    val percentFat: Double,
    val percentCarbs: Double
) : Serializable

data class WeightPerServing(
    val amount: Int,
    val unit: String
) : Serializable
