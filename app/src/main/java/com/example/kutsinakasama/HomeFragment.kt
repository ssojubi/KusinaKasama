package com.example.kutsinakasama

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.kutsinakasama.databinding.HomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.android.material.chip.Chip
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Spinner

class HomeFragment : Fragment() {

    private var _binding: HomeBinding? = null
    private val binding get() = _binding!!
    private var currentRecipes: List<RecipePreview> = emptyList()
    private val apiKey = "35472b15244d43878a94806d0a677d4a"
    private val dishTypes = listOf(
        "Mains",
        "Desserts",
        "Appetizers",
        "Salads",
        "Breakfasts",
        "Beverages",
        "Snacks"
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null && currentRecipes.isNotEmpty()) {
            displayRecipes(currentRecipes)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchBar.setOnEditorActionListener { _, _, _ ->
            val input = binding.searchBar.text.toString().trim()

            if (input.isNotEmpty()) {
                addChip(input)
                binding.searchBar.text.clear()
            }

            true
        }

        binding.filterBtn.setOnClickListener {
            openFilterDialog()
        }
        loadRecipes()
    }
    private fun loadRecipes() {
        RetrofitClient.instance.getRecipes(apiKey)
            .enqueue(object : Callback<RecipeSearchResponse> {
                override fun onResponse(
                    call: Call<RecipeSearchResponse>,
                    response: Response<RecipeSearchResponse>
                ) {
                    if (response.isSuccessful) {
                        val recipes = response.body()?.results ?: emptyList()
                        displayRecipes(recipes)
                    } else {
                        Log.e("API_ERROR", "Response not successful: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<RecipeSearchResponse>, t: Throwable) {
                    Log.e("API_ERROR", "Failed to load recipes", t)
                }
            })
    }

    private fun displayRecipes(recipes: List<RecipePreview>) {
        val container = binding.recipeContainer
        container.removeAllViews()

        for (recipe in recipes) {
            val itemView = layoutInflater.inflate(R.layout.home_recipe_card, container, false)
            val titleView = itemView.findViewById<TextView>(R.id.tvRecipeTitle)
            val imageView = itemView.findViewById<ImageView>(R.id.imgRecipe)


            titleView.text = recipe.title ?: "No Title"
            Glide.with(this@HomeFragment)
                .load(recipe.image)
                .into(imageView)


            itemView.setOnClickListener {
                val intent = Intent(requireContext(), RecipeActivity::class.java)
                intent.putExtra("RECIPE_ID", recipe.id)
                startActivity(intent)
            }

            container.addView(itemView)

            val favButton = itemView.findViewById<ImageView>(R.id.btnFavorite)
            val db = DBHelper(requireContext())


            if (db.isFavorite(recipe.id)) {
                favButton.setImageResource(R.drawable.ic_heart_filled)
            } else {
                favButton.setImageResource(R.drawable.ic_heart_hollow)
            }
            favButton.setOnClickListener {
                if (db.isFavorite(recipe.id)) {
                    db.removeFavorite(recipe.id)
                    favButton.setImageResource(R.drawable.ic_heart_hollow)
                } else {
                    db.addFavorite(recipe.id, recipe.title ?: "")
                    favButton.setImageResource(R.drawable.ic_heart_filled)
                }
            }

        }
    }

    private fun addChip(text: String) {
        val chip = layoutInflater.inflate(R.layout.ingredient_chip, null) as Chip
        chip.text = text
        chip.isCheckable = false
        chip.isClickable = true

        chip.setOnCloseIconClickListener {
            binding.ingredientChipGroup.removeView(chip)
        }
        binding.ingredientChipGroup.addView(chip)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun fetchRecipesByType(type: String) {
        RetrofitClient.instance.getRecipesByType(apiKey, type)
            .enqueue(object : Callback<RecipeSearchResponse> {
                override fun onResponse(
                    call: Call<RecipeSearchResponse>,
                    response: Response<RecipeSearchResponse>
                ) {
                    if (response.isSuccessful) {
                        val recipes = response.body()?.results ?: emptyList()
                        currentRecipes = recipes
                        displayRecipes(recipes)
                    }
                }

                override fun onFailure(call: Call<RecipeSearchResponse>, t: Throwable) {
                    Log.e("API_ERROR", "Failed to load recipes", t)
                }
            })
    }
    private fun filterRecipesByDishType(type: String) {
        fetchRecipesByType(type)
    }
    private fun openFilterDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.filter_dialog, null)

        val spinner = dialogView.findViewById<Spinner>(R.id.dishTypeSpinner)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            dishTypes
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Filter by Dish Type")
            .setView(dialogView)
            .setPositiveButton("Apply") { _, _ ->
                val selectedType = spinner.selectedItem.toString()
                filterRecipesByDishType(selectedType)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

}

