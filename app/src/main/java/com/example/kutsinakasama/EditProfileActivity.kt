package com.example.kutsinakasama

import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kutsinakasama.databinding.EditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: EditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.imgEditIcon.setOnClickListener {
            Toast.makeText(this, "Edit profile picture clicked", Toast.LENGTH_SHORT).show()
            // TODO: will add image picker (MCO3)
        }

        binding.forgotPassword.setOnClickListener {
            Toast.makeText(this, "Password reset clicked", Toast.LENGTH_SHORT).show()
            // TODO: Will add navigation for password resets (MCO3)
        }

        binding.btnSave.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // TODO: will add logic for saving profile data later on (MCO3)
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        // italic when empty, normal when typing
        setupHintStyleBehavior()
    }
    private fun setupHintStyleBehavior() {
        val emailEditText = binding.editTextEmail

        // Initial hint style
        emailEditText.setTypeface(null, Typeface.ITALIC)

        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    emailEditText.setTypeface(null, Typeface.ITALIC)
                } else {
                    emailEditText.setTypeface(null, Typeface.NORMAL)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}
