package com.example.kutsinakasama

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {
    lateinit var usernameInput : EditText

    lateinit var emailInput : EditText
    lateinit var passwordInput : EditText
    lateinit var signupBtn: Button

    lateinit var loginBtn: Button

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

        usernameInput = findViewById(R.id.signupUserInput)
        emailInput = findViewById(R.id.signupEmailInput)
        passwordInput = findViewById(R.id.signupPassInput)
        signupBtn = findViewById(R.id.signupButton)
        loginBtn = findViewById(R.id.bttnGoToLogin)

        signupBtn.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        loginBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }
}