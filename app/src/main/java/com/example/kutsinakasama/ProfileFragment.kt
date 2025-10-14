//package com.example.kutsinakasama
//
//import android.content.Intent
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import com.example.kutsinakasama.databinding.ActivityProfileBinding
//
//class ProfileActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityProfileBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityProfileBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        binding.btnEditProfile.setOnClickListener {
//            startActivity(Intent(this, EditProfileActivity::class.java))
//        }
//    }
//}
package com.example.kutsinakasama

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
