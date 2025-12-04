package com.example.kutsinakasama

import android.os.Bundle
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.example.kutsinakasama.databinding.RecipeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class RecipeActivity : AppCompatActivity() {

    private lateinit var binding: RecipeBinding
    private lateinit var db: DBHelper

    private val apiKey = "4b57797b271a4bc8811a5711d3425d9a"
    private var recipeId = -1
    private var lastRecipe: RecipeResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recipeId = intent.getIntExtra("RECIPE_ID", -1)
        binding = RecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DBHelper(this)

        binding.btnBack.setOnClickListener {
            finish()
        }

        loadRecipe()
        binding.btnFavorite.setOnClickListener {
            toggleFavorite()
        }
    }

    private fun loadRecipe() {
        showLoading()

        RetrofitClient.instance.getRecipeInformation(recipeId, apiKey)
            .enqueue(object : Callback<RecipeResponse> {
                override fun onResponse(call: Call<RecipeResponse>, r: Response<RecipeResponse>) {
                    hideLoading()

                    if (r.isSuccessful && r.body() != null) {
                        val recipe = r.body()!!
                        lastRecipe = recipe

                        val cleanedInstructions = HtmlCompat.fromHtml(
                            recipe.instructions ?: "",
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        ).toString()

                        val cleanedIngredients = recipe.extendedIngredients.joinToString("\n") {
                            "• " + HtmlCompat.fromHtml(it.original, HtmlCompat.FROM_HTML_MODE_LEGACY)
                                .toString()
                        }

                        binding.tvRecipeTitle.text = recipe.title
                        binding.tvMethod.text = cleanedInstructions
                        binding.tvIngredients.text = cleanedIngredients

                        Glide.with(this@RecipeActivity)
                            .load(recipe.image)
                            .into(binding.imgRecipe)

                        updateFavoriteButton()
                    } else {
                        loadOfflineRecipe()
                    }
                }

                override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                    hideLoading()
                    loadOfflineRecipe()
                }
            })
    }

    private fun showLoading() {
        binding.progressCircular.visibility = View.VISIBLE
        binding.scrollView.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.progressCircular.visibility = View.GONE
        binding.scrollView.visibility = View.VISIBLE
    }

    private fun updateFavoriteButton() {
        val prefs = getSharedPreferences("userSession", Context.MODE_PRIVATE)
        val userId = prefs.getInt("userId", -1)

        val isFav = db.isFavorite(recipeId, userId)
        binding.btnFavorite.setImageResource(
            if (isFav) R.drawable.ic_heart_filled else R.drawable.ic_heart_hollow
        )
    }

    private fun toggleFavorite() {
        val prefs = getSharedPreferences("userSession", Context.MODE_PRIVATE)
        val userId = prefs.getInt("userId", -1)
        val recipe = lastRecipe ?: return

        val ingredientsString = recipe.extendedIngredients.joinToString("\n") {
            HtmlCompat.fromHtml(it.original, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
        }

        val instructionsString = HtmlCompat.fromHtml(
            recipe.instructions ?: "",
            HtmlCompat.FROM_HTML_MODE_LEGACY
        ).toString()

        if (db.isFavorite(recipeId, userId)) {
            db.removeFavorite(recipeId, userId)
            binding.btnFavorite.setImageResource(R.drawable.ic_heart_hollow)
        } else {
            binding.btnFavorite.setImageResource(R.drawable.ic_heart_filled)

            // Download and save image in background thread
            Thread {
                val localImagePath = db.saveImageLocally(this, recipe.image, recipe.id)

                runOnUiThread {
                    db.addFavorite(
                        recipe.id,
                        userId,
                        recipe.title,
                        localImagePath,
                        instructionsString,
                        ingredientsString
                    )

                    Log.d("RecipeActivity", "Image saved to: $localImagePath")
                }
            }.start()
        }
    }

    private fun loadOfflineRecipe() {
        val prefs = getSharedPreferences("userSession", Context.MODE_PRIVATE)
        val userId = prefs.getInt("userId", -1)

        val r = db.getOfflineRecipe(recipeId, userId)
        if (r == null) {
            Toast.makeText(this, "No offline copy available", Toast.LENGTH_SHORT).show()
            return
        }

        binding.tvRecipeTitle.text = r.title
        binding.tvMethod.text = r.instructions
        binding.tvIngredients.text = r.ingredients

        // Load the locally saved image
        if (!r.image.isNullOrEmpty()) {
            val imageFile = File(r.image)
            if (imageFile.exists()) {
                Glide.with(this)
                    .load(imageFile)
                    .placeholder(R.drawable.egg_sample)
                    .into(binding.imgRecipe)
            } else {
                Glide.with(this)
                    .load(R.drawable.offline_recipeimg)
                    .into(binding.imgRecipe)
            }
        } else {
            Glide.with(this)
                .load(R.drawable.offline_recipeimg)
                .into(binding.imgRecipe)
        }

        binding.btnFavorite.setImageResource(R.drawable.ic_heart_filled)
    }
}