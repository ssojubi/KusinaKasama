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

    private val apiKey = "YOUR_SPOONACULAR_API_KEY"
    private val recipeId = 716429

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = RecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnFavorite.setOnClickListener {
            it.isSelected = !it.isSelected
        }

        loadRecipeFromApi()
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

                            binding.tvRecipeTitle.text = recipe.title

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
