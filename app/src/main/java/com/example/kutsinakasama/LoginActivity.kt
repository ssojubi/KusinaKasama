package com.example.kutsinakasama

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    lateinit var usernameInput : EditText
    lateinit var passwordInput : EditText
    lateinit var loginBtn: Button

    lateinit var signupBtn: Button

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        var isLoggedIn = intent.getBooleanExtra("isLoggedIn", false)


        usernameInput = findViewById(R.id.lginEmailInput)
        passwordInput = findViewById(R.id.lginPassInput)
        loginBtn = findViewById(R.id.lginButton)
        signupBtn = findViewById(R.id.bttnGoToSignUp)

        loginBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            isLoggedIn = true
            intent.putExtra("isLoggedIn", isLoggedIn)
            startActivity(intent)
            finish()
        }

        signupBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

    }
}