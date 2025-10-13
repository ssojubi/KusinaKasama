package com.example.kutsinakasama

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.kutsinakasama.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Connect bottom nav to navController
        binding.bottomNavigationView.setupWithNavController(navController)

        // Hide bottom nav on edit profile
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.editProfileFragment) {
                binding.bottomNavigationView.visibility = android.view.View.GONE
            } else {
                binding.bottomNavigationView.visibility = android.view.View.VISIBLE
            }
        }
    }
}
