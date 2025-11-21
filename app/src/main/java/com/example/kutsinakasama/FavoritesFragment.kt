package com.example.kutsinakasama

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kutsinakasama.databinding.FavoritesBinding

class FavoritesFragment : Fragment() {

    private lateinit var binding: FavoritesBinding
    private lateinit var db: DBHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FavoritesBinding.inflate(inflater, container, false)
        db = DBHelper(requireContext())
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadFavorites()
    }

    private fun loadFavorites() {
        val favorites = db.getAllFavorites()

        val container = binding.favoritesContainer
        container.removeAllViews()

        for ((id, title) in favorites) {
            val itemView = layoutInflater.inflate(R.layout.favorite_item, container, false)

            val titleView = itemView.findViewById<TextView>(R.id.tvFavTitle)
            titleView.text = title

            itemView.setOnClickListener {
                val intent = Intent(requireContext(), RecipeActivity::class.java)
                intent.putExtra("RECIPE_ID", id)
                startActivity(intent)
            }

            container.addView(itemView)
        }
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
}
