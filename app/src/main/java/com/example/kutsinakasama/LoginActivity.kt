package com.example.kutsinakasama

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kutsinakasama.databinding.LoginBinding
import android.content.Context
import android.util.Log

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

//    private fun loginDatabase(email: String, password: String){
//        val userExists = databaseHelper.readUser(email, password)
//        if(userExists){
//            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
//            val userId = databaseHelper.getUserIdByEmail(email)
//
//            val sharedPreferences = getSharedPreferences("userSession", Context.MODE_PRIVATE)
//            val editor = sharedPreferences.edit()
//            editor.putBoolean("is_logged_in", true)
//            editor.putInt("userId", userId) // save userId here to be retrieved on profile page
//            editor.apply()
//
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//
//        else{
//            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
//        }
//    }
private fun loginDatabase(email: String, password: String){
    Log.d("LOGIN_DEBUG", "=== LOGIN ATTEMPT ===")
    Log.d("LOGIN_DEBUG", "Email: $email")

    val userExists = databaseHelper.readUser(email, password)
    Log.d("LOGIN_DEBUG", "User exists: $userExists")

    if(userExists){
        // Get the userId from database
        val userId = databaseHelper.getUserIdByEmail(email)

        Log.d("LOGIN_DEBUG", "User ID retrieved: $userId")

        if (userId == -1) {
            Toast.makeText(this, "Error retrieving user ID", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

        // Save to SharedPreferences
        val sharedPreferences = getSharedPreferences("userSession", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_logged_in", true)
        editor.putInt("userId", userId)
        editor.apply()

        Log.d("LOGIN_DEBUG", "About to start MainActivity")

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    else{
        Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
    }
}
}