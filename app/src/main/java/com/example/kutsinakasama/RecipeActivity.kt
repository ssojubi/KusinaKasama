package com.example.kutsinakasama

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.kutsinakasama.databinding.RecipeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeActivity : AppCompatActivity() {

    private lateinit var binding: RecipeBinding
    private lateinit var db: DBHelper

    private val apiKey = "f3051e4d59ff4d2daebd550ced762374"
    private var recipeId = -1
    private var recipeTitle = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recipeId = intent.getIntExtra("RECIPE_ID", -1)
        if (recipeId == -1) {
            finish()
            return
        }

        binding = RecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DBHelper(this)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Update the favorite button state
        updateFavoriteButton()

        binding.btnFavorite.setOnClickListener {
            toggleFavorite()
        }

        loadRecipeFromApi()
    }

    private fun updateFavoriteButton() {
        if (db.isFavorite(recipeId)) {
            binding.btnFavorite.setImageResource(R.drawable.ic_heart_filled)
        } else {
            binding.btnFavorite.setImageResource(R.drawable.ic_heart_hollow)
        }
    }

    private fun toggleFavorite() {
        if (db.isFavorite(recipeId)) {
            db.removeFavorite(recipeId)
            binding.btnFavorite.setImageResource(R.drawable.ic_heart_hollow)
        } else {
            db.addFavorite(recipeId, recipeTitle)
            binding.btnFavorite.setImageResource(R.drawable.ic_heart_filled)
        }
    }

    private fun loadRecipeFromApi() {
        RetrofitClient.instance.getRecipeInformation(recipeId, apiKey)
            .enqueue(object : Callback<RecipeResponse> {

                override fun onResponse(
                    call: Call<RecipeResponse>,
                    response: Response<RecipeResponse>
                ) {
                    if (response.isSuccessful) {
                        val recipe = response.body()
                        if (recipe != null) {
                            recipeTitle = recipe.title ?: "No Title"

                            binding.tvRecipeTitle.text = recipeTitle

                            binding.tvMethod.text =
                                recipe.instructions ?: "No instructions available."

                            binding.tvIngredients.text =
                                recipe.extendedIngredients.joinToString("\n") {
                                    "• ${it.original}"
                                }

                            Glide.with(this@RecipeActivity)
                                .load(recipe.image)
                                .placeholder(R.drawable.egg_sample)
                                .error(R.drawable.egg_sample)
                                .into(binding.imgRecipe)

                            // Update favorite button
                            updateFavoriteButton()
                        } else {
                            Log.e("API_ERROR", "No recipe data found")
                        }
                    } else {
                        Log.e("API_ERROR", "Response not successful: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                    Log.e("API_ERROR", "Failed to load recipe", t)
                }
            })
    }
}