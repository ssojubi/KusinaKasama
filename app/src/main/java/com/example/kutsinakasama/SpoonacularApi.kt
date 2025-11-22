package com.example.kutsinakasama

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpoonacularApi {

    @GET("recipes/{id}/information")
    fun getRecipeInformation(
        @Path("id") recipeId: Int,
        @Query("apiKey") apiKey: String
    ): Call<RecipeResponse>

    @GET("recipes/complexSearch")
    fun getRecipes(
        @Query("apiKey") apiKey: String,
        @Query("number") number: Int = 10,
        @Query("addRecipeInformation") addRecipeInformation: Boolean = true
    ): Call<RecipeSearchResponse>

    @GET("recipes/complexSearch")
    fun getRecipesByType(
        @Query("apiKey") apiKey: String,
        @Query("type") type: String
    ): Call<RecipeSearchResponse>

//    @GET("recipes/search")
//    fun searchRecipes(
//        @Query("apiKey") apiKey: String,
//        @Query("query") query: String?,     // search
//        @Query("type") type: String?        // filter
//    ): Call<RecipeSearchResponse>
    @GET("recipes/complexSearch")
    fun searchRecipes(
        @Query("apiKey") apiKey: String,
        @Query("includeIngredients") query: String?,  // Comma-separated ingredients
        @Query("type") type: String?,                  // Dish type filter
        @Query("number") number: Int = 10,
        @Query("addRecipeInformation") addRecipeInformation: Boolean = true
    ): Call<RecipeSearchResponse>
}

