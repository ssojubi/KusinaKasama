package com.example.kutsinakasama

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kutsinakasama.databinding.HomeBinding

class HomeFragment : Fragment() {

    // ViewBinding variable
    private var _binding: HomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout with ViewBinding
        _binding = HomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Example: Button opens ProfileActivity
//        binding.btnGoProfile.setOnClickListener {
//            val intent = Intent(requireContext(), ProfileActivity::class.java)
//            startActivity(intent)
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // prevent memory leaks
    }
}
