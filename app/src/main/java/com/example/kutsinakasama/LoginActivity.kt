package com.example.kutsinakasama


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kutsinakasama.databinding.LoginBinding
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : LoginBinding
    private lateinit var databaseHelper : DBHelper

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DBHelper(this)

        loadRememberedCredentials()

        binding.lginButton.setOnClickListener{
            val email = binding.lginEmailInput.text.toString().trim() // Use trim()
            val password = binding.lginPassInput.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginDatabase(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
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
            val user = databaseHelper.getUserByEmail(email)
            if (user == null) {
                Toast.makeText(this, "Error retrieving user data", Toast.LENGTH_SHORT).show()
                return
            }

            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

            if (binding.checkBox.isChecked) {
                saveRememberedCredentials(user.email, user.imageUri) // Pass image URI
            } else {
                clearRememberedCredentials()
            }

            val sessionPrefs = getSharedPreferences("userSession", Context.MODE_PRIVATE)
            val editor = sessionPrefs.edit()
            editor.putBoolean("is_logged_in", true)
            editor.putInt("userId", user.id)
            editor.apply()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        else{
            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadRememberedCredentials() {
        val loginPrefs = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        val shouldRemember = loginPrefs.getBoolean("remember_me", false)

        if (shouldRemember) {
            val savedEmail = loginPrefs.getString("email", null)
            val savedImageUri = loginPrefs.getString("imageUri", null)

            binding.lginEmailInput.setText(savedEmail)
            binding.checkBox.isChecked = true

            if (!savedImageUri.isNullOrEmpty()) {
                try {
                    val imageUri = Uri.parse(savedImageUri)
                    val inputStream = contentResolver.openInputStream(imageUri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    binding.imageView.setImageBitmap(bitmap)
                    inputStream?.close()
                } catch (e: Exception) {
                    Log.e("LoginActivity", "Failed to load image URI: $savedImageUri", e)
                    binding.imageView.setImageResource(R.drawable.baseline_account_circle_24)
                }
            } else {
                binding.imageView.setImageResource(R.drawable.baseline_account_circle_24)
            }
        }
    }

    private fun saveRememberedCredentials(email: String, imageUri: String?) {
        val loginPrefs = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        val editor = loginPrefs.edit()
        editor.putBoolean("remember_me", true)
        editor.putString("email", email)
        editor.putString("imageUri", imageUri)
        editor.apply()
    }

    private fun clearRememberedCredentials() {
        val loginPrefs = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        val editor = loginPrefs.edit()
        editor.clear()
        editor.apply()

        binding.imageView.setImageResource(R.drawable.baseline_account_circle_24)
    }
}