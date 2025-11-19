package com.example.kutsinakasama

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kutsinakasama.databinding.LoginBinding
import android.content.Context
class LoginActivity : AppCompatActivity() {

    private lateinit var binding : LoginBinding
    private lateinit var databaseHelper : DBHelper

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DBHelper(this)

        binding.lginButton.setOnClickListener{
            val email = binding.lginEmailInput.text.toString()
            val password = binding.lginPassInput.text.toString()

            loginDatabase(email, password)

        }

        binding.bttnGoToSignUp.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun loginDatabase(email: String, password: String){
        val userExists = databaseHelper.readUser(email, password)
        if(userExists){
            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

            val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("is_logged_in", true)
            editor.apply()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
        }
    }
}