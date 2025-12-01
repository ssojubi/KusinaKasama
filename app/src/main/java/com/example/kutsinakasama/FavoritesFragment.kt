package com.example.kutsinakasama

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
        val prefs = requireActivity().getSharedPreferences("userSession", Context.MODE_PRIVATE)
        val userId = prefs.getInt("userId", -1)

        val container = binding.favoritesContainer
        container.removeAllViews()

        val cursor = db.readableDatabase.rawQuery(
            "SELECT id, title FROM favorites WHERE user_id=?",
            arrayOf(userId.toString())
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val title = cursor.getString(1)

                val itemView =
                    layoutInflater.inflate(R.layout.favorite_item, container, false)
                itemView.findViewById<TextView>(R.id.tvFavTitle).text = title

                itemView.setOnClickListener {
                    val intent = Intent(requireContext(), RecipeActivity::class.java)
                    intent.putExtra("RECIPE_ID", id)
                    startActivity(intent)
                }

                container.addView(itemView)
            } while (cursor.moveToNext())
        }

        cursor.close()
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
}
