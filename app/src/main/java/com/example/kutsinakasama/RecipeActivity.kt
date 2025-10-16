package com.example.kutsinakasama

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RecipeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recipe)

        val titleTextView = findViewById<TextView>(R.id.tvRecipeTitle)
        titleTextView.text = "The Perfect Egg"
    }
}
