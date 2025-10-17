//package com.example.kutsinakasama
//
//import android.content.Intent
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import com.example.kutsinakasama.databinding.ActivityMainBinding
//import kotlin.jvm.java
//
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityMainBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        binding.bottomNavigationView.setOnItemSelectedListener { item ->
//            when (item.itemId) {
////                R.id.homeFragment -> {
////                    startActivity(Intent(this, HomeActivity::class.java))
////                    true
////                }
////                R.id.favoritesFragment -> {
////                    startActivity(Intent(this, FavoritesActivity::class.java))
////                    true
////                }
//                R.id.profileFragment -> {
//                    startActivity(Intent(this, ProfileActivity::class.java))
//                    true
//                }
//                else -> false
//            }
//        }
//    }
//}

package com.example.kutsinakasama

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.kutsinakasama.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var isLoggedIn = intent.getBooleanExtra("isLoggedIn", false)

        if (!isLoggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
            intent.putExtra("isLoggedIn", isLoggedIn)
            finish() // Prevent going back to MainActivity
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(HomeFragment())

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.favorites -> {
                    replaceFragment(FavoritesFragment())
                    true
                }
                R.id.profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .commit()
    }
}

