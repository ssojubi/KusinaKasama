package com.example.kutsinakasama

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kutsinakasama.databinding.FavoritesBinding

class FavoritesActivity : AppCompatActivity()  {
    private lateinit var binding: FavoritesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.recipeBtn.setOnClickListener {
            Toast.makeText(this, "Redirect to recipe page!", Toast.LENGTH_SHORT).show()
        }
        binding.recipeBtn2.setOnClickListener {
            Toast.makeText(this, "Redirect to recipe page2!", Toast.LENGTH_SHORT).show()
        }
        binding.recipeBtn3.setOnClickListener {
            Toast.makeText(this, "Redirect to recipe page3!", Toast.LENGTH_SHORT).show()
        }
    }
}