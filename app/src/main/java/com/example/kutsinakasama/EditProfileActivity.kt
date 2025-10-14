//package com.example.kutsinakasama
//
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import com.example.kutsinakasama.databinding.ActivityEditProfileBinding
//
//class EditProfileActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityEditProfileBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityEditProfileBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//    }
//}

package com.example.kutsinakasama

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kutsinakasama.databinding.EditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: EditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        // Example back button logic (optional)
//        binding.btnBack.setOnClickListener {
//            finish() // Go back to ProfileFragment
//        }
    }
}
