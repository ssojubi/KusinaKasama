package com.example.kutsinakasama

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kutsinakasama.databinding.ProfileBinding
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private lateinit var binding: ProfileBinding
    private lateinit var dbHelper: DBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ProfileBinding.inflate(inflater, container, false)
        dbHelper = DBHelper(requireContext())

        loadUserData()
        setupLogout()

        return binding.root
    }

    private fun loadUserData() {
        // Retrieve logged-in userId from SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("userSession", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("userId", -1)

        if (userId == -1) {
            binding.tvName.text = "Unknown User"
            binding.tvEmail.text =  "Unknown Email"
            return
        }

        val user = dbHelper.getUserById(userId)

        if (user != null) {
            binding.tvName.text = user.name
            binding.tvEmail.text = user.email

            if (!user.imageUri.isNullOrEmpty()) {
                Picasso.get()
                    .load(Uri.parse(user.imageUri))
                    .into(binding.imgProfile)
            }
        }
    }

    private fun setupLogout() {
        binding.btnLogout.setOnClickListener {
            val sharedPref = requireActivity().getSharedPreferences("userSession", Context.MODE_PRIVATE)
            sharedPref.edit().clear().apply()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
