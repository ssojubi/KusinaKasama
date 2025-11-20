package com.example.kutsinakasama

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import com.example.kutsinakasama.databinding.HomeBinding
import com.google.android.material.chip.Chip

class HomeFragment : Fragment() {

    private var _binding: HomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchBar.setOnEditorActionListener { _, _, _ ->
            val input = binding.searchBar.text.toString().trim()

            if (input.isNotEmpty()) {
                addChip(input)
                binding.searchBar.text.clear()   // clear after enter
            }

            true
        }


//        binding.recipeCard.setOnClickListener {
//            val intent = Intent(requireContext(), RecipeActivity::class.java)
//            startActivity(intent)
//        }


    }


//    private fun setupIngredientChipInput() {
//
//        val searchBar = binding.searchBar
//        val chipGroup = binding.ingredientChipGroup
//
//        // Detect Enter / Search key
//        searchBar.setOnEditorActionListener { _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_DONE ||
//                actionId == EditorInfo.IME_ACTION_SEARCH
//            ) {
//                val ingredient = searchBar.text.toString().trim()
//                if (ingredient.isNotEmpty()) {
//                    addIngredientChip(ingredient)
//                    searchBar.text.clear()
//                }
//                true
//            } else false
//        }
//    }
//
//    private fun addIngredientChip(text: String) {
//        val chip = layoutInflater.inflate(
//            R.layout.ingredient_chip,
//            binding.ingredientChipGroup,
//            false
//        ) as Chip
//
//        chip.text = text
//        chip.setOnCloseIconClickListener {
//            binding.ingredientChipGroup.removeView(chip)
//        }
//
//        binding.ingredientChipGroup.addView(chip)
//    }

    private fun addChip(text: String) {
        val chip = layoutInflater.inflate(R.layout.ingredient_chip, null) as Chip
        chip.text = text

        // Disable chip selection toggle
        chip.isCheckable = false
        chip.isClickable = true

        // Remove chip when X is pressed
        chip.setOnCloseIconClickListener {
            binding.ingredientChipGroup.removeView(chip)
        }

        binding.ingredientChipGroup.addView(chip)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
