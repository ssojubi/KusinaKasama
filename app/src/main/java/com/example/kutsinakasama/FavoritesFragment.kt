package com.example.kutsinakasama

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kutsinakasama.databinding.FavoritesBinding

class FavoritesFragment : Fragment() {

    private var _binding: FavoritesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Back button behavior
//        binding.btnBack.setOnClickListener {
//            requireActivity().onBackPressedDispatcher.onBackPressed()
//        }

        // Example recipe button toasts
        binding.recipeBtn.setOnClickListener {
            Toast.makeText(requireContext(), "Redirect to recipe page!", Toast.LENGTH_SHORT).show()
        }

        binding.recipeBtn2.setOnClickListener {
            Toast.makeText(requireContext(), "Redirect to recipe page2!", Toast.LENGTH_SHORT).show()
        }

        binding.recipeBtn3.setOnClickListener {
            Toast.makeText(requireContext(), "Redirect to recipe page3!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
