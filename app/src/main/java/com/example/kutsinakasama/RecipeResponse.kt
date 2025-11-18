package com.example.kutsinakasama

data class RecipeResponse(
    val id: Int,
    val title: String,
    val image: String,
    val instructions: String?,
    val extendedIngredients: List<Ingredient>
)

data class Ingredient(
    val original: String
)
