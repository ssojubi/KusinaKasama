package com.example.kutsinakasama

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kutsinakasama.databinding.SignupBinding


class SignUpActivity : AppCompatActivity() {

    private lateinit var binding : SignupBinding

    private lateinit var databaseHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DBHelper(this)

        binding.signupButton.setOnClickListener {
            val firstName = binding.signupFirstNameInput.text.toString().trim()
            val lastName = binding.signupLastNameInput.text.toString().trim()
            val signupName = "$firstName $lastName"
            val signupEmail = binding.signupEmailInput.text.toString().trim()
            val signupPassword = binding.signupPassInput.text.toString()
            val signupConfirmPassword = binding.signupRePassInput.text.toString()

            if (firstName.isNotEmpty() && lastName.isNotEmpty() && signupEmail.isNotEmpty() && signupPassword.isNotEmpty() && signupConfirmPassword.isNotEmpty()) {
                if (signupPassword == signupConfirmPassword) {
                    if (!databaseHelper.isEmailTaken(signupEmail)) {
                        // Email is not taken, proceed with signup
                        signupDatabase(signupName, signupEmail, signupPassword)
                    } else {
                        Toast.makeText(this, "This email is already registered.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.bttnGoToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun signupDatabase(name : String, email : String, password : String){
        val insertedRowId = databaseHelper.insertUser(name, email, password)
        if (insertedRowId != -1L){
            Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            Toast.makeText(this, "Signup failed", Toast.LENGTH_SHORT).show()
        }
    }
}