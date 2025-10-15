package com.example.kutsinakasama

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kutsinakasama.databinding.ProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: ProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfileBinding.inflate(inflater, container, false)

        // Button to go to EditProfileActivity
        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }
        binding.btnViewFavorites.setOnClickListener {
            val intent = Intent(requireContext(), FavoritesActivity::class.java)
            startActivity(intent)
        }
        binding.btnLogout.setOnClickListener {
        // Handle logout logic here will redirect to login/signup
//            val intent = Intent(requireContext(), LoginActivity::class.java)
//            startActivity(intent)
            Toast.makeText(requireContext(), "Logout Pressed", Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
