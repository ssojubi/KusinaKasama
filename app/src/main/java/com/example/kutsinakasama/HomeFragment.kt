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
    private val apiKey = "c1ec907a9bc0427798843f1468393ba9"
    private val dishTypes = listOf(
        "Mains",
        "Desserts",
        "Appetizers",
        "Salads",
        "Breakfasts",
        "Beverages",
        "Snacks"
    )

    // Track current filters
    private var currentDishType: String? = null
    private val searchIngredients = mutableListOf<String>()

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

        // Restore saved state if exists
        savedInstanceState?.let {
            val savedIngredients = it.getStringArrayList(KEY_INGREDIENTS)
            savedIngredients?.forEach { ingredient ->
                searchIngredients.add(ingredient)
                addChip(ingredient)
            }
            currentDishType = it.getString(KEY_DISH_TYPE)
        }

        binding.searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val input = binding.searchBar.text.toString().trim()

                if (input.isNotEmpty()) {
                    addChip(input)
                    searchIngredients.add(input)
                    binding.searchBar.text.clear()

                    // Trigger search with updated ingredients
                    performSearch()
                }
                true
            } else {
                false
            }
        }

        binding.filterBtn.setOnClickListener {
            openFilterDialog()
        }

        // Only load recipes if we don't have saved state
        if (savedInstanceState == null) {
            loadRecipes()
        } else {
            // Restore previous search results
            if (searchIngredients.isNotEmpty() || currentDishType != null) {
                performSearch()
            } else {
                loadRecipes()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save current search state
        outState.putStringArrayList(KEY_INGREDIENTS, ArrayList(searchIngredients))
        outState.putString(KEY_DISH_TYPE, currentDishType)
    }

    private fun loadRecipes() {
        showLoading()

        RetrofitClient.instance.getRecipes(apiKey)
            .enqueue(object : Callback<RecipeSearchResponse> {
                override fun onResponse(
                    call: Call<RecipeSearchResponse>,
                    response: Response<RecipeSearchResponse>
                ) {
                    if (!isAdded) return

                    hideLoading()

                    if (response.isSuccessful) {
                        val recipes = response.body()?.results ?: emptyList()
                        currentRecipes = recipes
                        displayRecipes(recipes)
                    } else {
                        Log.e("API_ERROR", "Response not successful: ${response.code()}")
                        showError("Failed to load recipes")
                    }
                }

                override fun onFailure(call: Call<RecipeSearchResponse>, t: Throwable) {
                    if (!isAdded) return

                    hideLoading()
                    Log.e("API_ERROR", "Failed to load recipes", t)
                    showError("Network error. Please check your connection.")
                }
            })
    }

    private fun performSearch() {
        showLoading()

        // Combine ingredients into a comma-separated string for the API
        val ingredientsQuery = if (searchIngredients.isNotEmpty()) {
            searchIngredients.joinToString(",")
        } else {
            null
        }

        // Call API with both ingredients and dish type filter
        RetrofitClient.instance.searchRecipes(
            apiKey = apiKey,
            query = ingredientsQuery,
            type = currentDishType
        ).enqueue(object : Callback<RecipeSearchResponse> {
            override fun onResponse(
                call: Call<RecipeSearchResponse>,
                response: Response<RecipeSearchResponse>
            ) {
                // Ensure we're still attached to activity
                if (!isAdded) return

                hideLoading()

                if (response.isSuccessful) {
                    val recipes = response.body()?.results ?: emptyList()
                    currentRecipes = recipes
                    displayRecipes(recipes)
                } else {
                    Log.e("API_ERROR", "Search failed: ${response.code()}")
                    showError("Failed to load recipes")
                }
            }

            override fun onFailure(call: Call<RecipeSearchResponse>, t: Throwable) {
                // Ensure we're still attached to activity
                if (!isAdded) return

                hideLoading()
                Log.e("API_ERROR", "Search request failed", t)
                showError("Network error. Please check your connection.")
            }
        })
    }

    private fun showLoading() {
        binding.progressCircular.visibility = View.VISIBLE
        binding.recipeContainer.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.progressCircular.visibility = View.GONE
        binding.recipeContainer.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        binding.recipeContainer.removeAllViews()
        val errorView = TextView(requireContext()).apply {
            text = message
            textSize = 16f
            setPadding(16, 32, 16, 16)
        }
        binding.recipeContainer.addView(errorView)
    }

    private fun displayRecipes(recipes: List<RecipePreview>) {
        // Ensure we're still attached
        if (!isAdded) return

        val container = binding.recipeContainer
        container.removeAllViews()

        if (recipes.isEmpty()) {
            // Show "no results" message
            val noResultsView = TextView(requireContext()).apply {
                text = "No recipes found"
                textSize = 16f
                setPadding(16, 32, 16, 16)
            }
            container.addView(noResultsView)
            return
        }

        // Initialize DB once outside loop
        val db = DBHelper(requireContext())

        for (recipe in recipes) {
            val itemView = layoutInflater.inflate(R.layout.home_recipe_card, container, false)
            val titleView = itemView.findViewById<TextView>(R.id.tvRecipeTitle)
            val imageView = itemView.findViewById<ImageView>(R.id.imgRecipe)

            titleView.text = recipe.title ?: "No Title"

            // Load images with placeholder and error handling
            Glide.with(this@HomeFragment)
                .load(recipe.image)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(imageView)

            itemView.setOnClickListener {
                val intent = Intent(requireContext(), RecipeActivity::class.java)
                intent.putExtra("RECIPE_ID", recipe.id)
                startActivity(intent)
            }

            val favButton = itemView.findViewById<ImageView>(R.id.btnFavorite)

            // Check favorite status once
            val isFavorite = db.isFavorite(recipe.id)
            favButton.setImageResource(
                if (isFavorite) R.drawable.ic_heart_filled
                else R.drawable.ic_heart_hollow
            )

            favButton.setOnClickListener {
                if (db.isFavorite(recipe.id)) {
                    db.removeFavorite(recipe.id)
                    favButton.setImageResource(R.drawable.ic_heart_hollow)
                } else {
                    db.addFavorite(recipe.id, recipe.title ?: "")
                    favButton.setImageResource(R.drawable.ic_heart_filled)
                }
            }

            container.addView(itemView)
        }
    }

    private fun addChip(text: String) {
        val chip = layoutInflater.inflate(R.layout.ingredient_chip, null) as Chip
        chip.text = text
        chip.isCheckable = false
        chip.isClickable = true

        chip.setOnCloseIconClickListener {
            binding.ingredientChipGroup.removeView(chip)
            // Remove from search list and re-search
            searchIngredients.remove(text)
            performSearch()
        }

        binding.ingredientChipGroup.addView(chip)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun filterRecipesByDishType(type: String) {
        currentDishType = type
        performSearch()
    }

    private fun openFilterDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.filter_dialog, null)

        val spinner = dialogView.findViewById<Spinner>(R.id.dishTypeSpinner)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("All") + dishTypes  // Add "All" option to clear filter
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Set current selection if filter is active
        currentDishType?.let { type ->
            val position = dishTypes.indexOf(type) + 1 // +1 for "All" option
            if (position > 0) spinner.setSelection(position)
        }

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Filter by Dish Type")
            .setView(dialogView)
            .setPositiveButton("Apply") { _, _ ->
                val selected = spinner.selectedItem.toString()
                if (selected == "All") {
                    currentDishType = null
                } else {
                    currentDishType = selected
                }
                performSearch()
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    companion object {
        private const val KEY_INGREDIENTS = "search_ingredients"
        private const val KEY_DISH_TYPE = "dish_type"
    }
}