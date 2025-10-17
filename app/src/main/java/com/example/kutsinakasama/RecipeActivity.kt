package com.example.kutsinakasama

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kutsinakasama.databinding.EditProfileBinding

class RecipeActivity : AppCompatActivity() {

    private lateinit var binding: EditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recipe)

        val titleTextView = findViewById<TextView>(R.id.tvRecipeTitle)
        titleTextView.text = "The Perfect Egg"

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
