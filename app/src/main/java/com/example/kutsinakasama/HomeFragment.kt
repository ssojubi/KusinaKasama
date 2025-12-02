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
import android.content.Context
import androidx.core.text.HtmlCompat

class HomeFragment : Fragment() {

    private var _binding: HomeBinding? = null
    private val binding get() = _binding!!
    private var currentRecipes: List<RecipePreview> = emptyList()
    private val apiKey = "7f5f9edf502b4b22ab5721822ac8a78d"
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
                    performSearch()
                }
                true
            } else false
        }

        binding.filterBtn.setOnClickListener { openFilterDialog() }

        if (savedInstanceState == null) {
            loadRecipes()
        } else {
            if (searchIngredients.isNotEmpty() || currentDishType != null) {
                performSearch()
            } else {
                loadRecipes()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
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
                    } else showError("Failed to load recipes")
                }

                override fun onFailure(call: Call<RecipeSearchResponse>, t: Throwable) {
                    if (!isAdded) return
                    hideLoading()
                    showError("Network error.")
                }
            })
    }

    private fun performSearch() {
        showLoading()

        val ingredientsQuery =
            if (searchIngredients.isNotEmpty()) searchIngredients.joinToString(",") else null

        RetrofitClient.instance.searchRecipes(
            apiKey = apiKey,
            query = ingredientsQuery,
            type = currentDishType
        ).enqueue(object : Callback<RecipeSearchResponse> {
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
                } else showError("Failed to load recipes")
            }

            override fun onFailure(call: Call<RecipeSearchResponse>, t: Throwable) {
                if (!isAdded) return
                hideLoading()
                showError("Network error.")
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
        if (!isAdded) return

        val container = binding.recipeContainer
        container.removeAllViews()

        val db = DBHelper(requireContext())
        val prefs = requireActivity().getSharedPreferences("userSession", Context.MODE_PRIVATE)
        val userId = prefs.getInt("userId", -1)

        for (recipe in recipes) {

            val itemView = layoutInflater.inflate(R.layout.home_recipe_card, container, false)
            val titleView = itemView.findViewById<TextView>(R.id.tvRecipeTitle)
            val imageView = itemView.findViewById<ImageView>(R.id.imgRecipe)
            val favButton = itemView.findViewById<ImageView>(R.id.btnFavorite)

            titleView.text = recipe.title
            Glide.with(this).load(recipe.image).into(imageView)

            itemView.setOnClickListener {
                val intent = Intent(requireContext(), RecipeActivity::class.java)
                intent.putExtra("RECIPE_ID", recipe.id)
                startActivity(intent)
            }

            favButton.setOnClickListener {

                RetrofitClient.instance.getRecipeInformation(recipe.id, apiKey)
                    .enqueue(object : Callback<RecipeResponse> {

                        override fun onResponse(
                            call: Call<RecipeResponse>,
                            response: Response<RecipeResponse>
                        ) {
                            if (!response.isSuccessful || response.body() == null) return

                            val full = response.body()!!

                            val cleanedIngredients = full.extendedIngredients.joinToString("\n") {
                                HtmlCompat.fromHtml(it.original, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                            }

                            val cleanedInstructions = HtmlCompat.fromHtml(
                                full.instructions ?: "",
                                HtmlCompat.FROM_HTML_MODE_LEGACY
                            ).toString()

                            val localImage = db.saveImageLocally(
                                requireContext(),
                                full.image,
                                full.id)

                            if (db.isFavorite(full.id, userId)) {
                                db.removeFavorite(full.id, userId)
                                favButton.setImageResource(R.drawable.ic_heart_hollow)
                            } else {
                                db.addFavorite(
                                    full.id,
                                    userId,
                                    full.title,
                                    localImage,
                                    cleanedInstructions,
                                    cleanedIngredients
                                )
                                favButton.setImageResource(R.drawable.ic_heart_filled)
                            }
                        }

                        override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                            Log.e("HOME_FAV", "Failed to fetch full recipe", t)
                        }
                    })
            }

            favButton.setImageResource(
                if (db.isFavorite(recipe.id, userId))
                    R.drawable.ic_heart_filled
                else
                    R.drawable.ic_heart_hollow
            )

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
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.filter_dialog, null)

        val spinner = dialogView.findViewById<Spinner>(R.id.dishTypeSpinner)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("All") + dishTypes
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        currentDishType?.let { type ->
            val position = dishTypes.indexOf(type) + 1
            if (position > 0) spinner.setSelection(position)
        }

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Filter by Dish Type")
            .setView(dialogView)
            .setPositiveButton("Apply") { _, _ ->
                val selected = spinner.selectedItem.toString()
                currentDishType = if (selected == "All") null else selected
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