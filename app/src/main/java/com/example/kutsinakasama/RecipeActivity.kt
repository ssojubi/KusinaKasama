package com.example.kutsinakasama

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kutsinakasama.databinding.RecipeBinding

class RecipeActivity : AppCompatActivity() {

    private lateinit var binding: RecipeBinding

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
        binding.tvRecipeTitle.text = "The Perfect Egg"
    }
}


