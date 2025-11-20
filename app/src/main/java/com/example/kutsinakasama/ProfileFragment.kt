package com.example.kutsinakasama

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kutsinakasama.databinding.ProfileBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import java.io.File
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy


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
        setupFavorites()
        setupEditProfile()
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

            if (user.imageUri != null && user.imageUri != "null" && user.imageUri!!.isNotEmpty()) {

                Picasso.get()
                    .load(File(user.imageUri))
                    .transform(CircleImage())
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(binding.imgProfile)

            }
        }
    }
    private fun setupEditProfile() {
        binding.btnEditProfile.setOnClickListener {
            val sharedPref = requireActivity().getSharedPreferences("userSession", Context.MODE_PRIVATE)
            val userId = sharedPref.getInt("userId", -1)

            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }
    }

    private fun setupFavorites() {
        binding.btnFavorites.setOnClickListener {
            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomNav.selectedItemId = R.id.favorites
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
    //when coming back from edit profile
    override fun onResume() {
        super.onResume()
        loadUserData() // reload user data when fragment becomes visible again
    }
}
