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
        @Query("number") number: Int = 10
    ): Call<RecipeSearchResponse>
}

