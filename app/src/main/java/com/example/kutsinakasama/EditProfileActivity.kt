package com.example.kutsinakasama

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.kutsinakasama.databinding.EditProfileBinding
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: EditProfileBinding
    private lateinit var dbHelper: DBHelper

    private var userId: Int = -1
    private var currentEmail: String = ""
    private var currentPassword: String = ""
    private var currentImageUri: String? = null
    private var selectedImageUri: String? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = saveImageToInternalStorage(it)

                Picasso.get()
                    .load(File(selectedImageUri))
                    .transform(CircleImage())
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(binding.imgProfile)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DBHelper(this)

        // get userId from intent extras; if not present try SharedPreferences keys used
        userId = intent.getIntExtra("userId", -1)

        if (userId == -1) {
            val prefs2 = getSharedPreferences("userSession", Context.MODE_PRIVATE)
            userId = prefs2.getInt("userId", -1)
        }

        if (userId == -1) {
            Toast.makeText(this, "No user found.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadUserData()
        binding.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.btnCancel.setOnClickListener { finish() }

        // image picker
        binding.imgEditIcon.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
        binding.forgotPassword.setOnClickListener {
            Toast.makeText(this, "Password reset clicked", Toast.LENGTH_SHORT).show()
        }
        binding.btnSave.setOnClickListener { saveUpdatedProfile() }

        setupHintStyleBehavior()
    }

    private fun loadUserData() {
        val user = dbHelper.getUserById(userId)

        if (user == null) {
            Toast.makeText(this, "Error loading user data", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.tvName.text = user.name
        binding.editTextEmail.setText(user.email)
        currentEmail = user.email ?: ""
        currentPassword = user.password ?: "" //for verification purposes
        currentImageUri = user.imageUri

        // load existing image (if present) into imgProfile, otherwise the placeholder will stay
        if (!currentImageUri.isNullOrEmpty() && currentImageUri != "null") {
            try {
                Picasso.get()
                    .load(File(currentImageUri))
                    .transform(CircleImage())
                    .placeholder(R.drawable.ic_user_icon_placeholder)
                    .into(binding.imgProfile)
            } catch (e: Exception) {
                binding.imgProfile.setImageResource(R.drawable.ic_user_icon_placeholder)
            }
        }

    }

    private fun saveUpdatedProfile() {
        val newEmail = binding.editTextEmail.text.toString().trim()
        val oldPasswordInput = binding.editTextOldPassword.text.toString().trim()
        val newPasswordInput = binding.editTextNewPassword.text.toString().trim()

        // Check if anything is being changed
        val isEmailChanged = newEmail.isNotEmpty() && newEmail != currentEmail
        val isPasswordChanged = newPasswordInput.isNotEmpty()
        val isImageChanged = selectedImageUri != null

        // If nothing is changed, just go back
        if (!isEmailChanged && !isPasswordChanged && !isImageChanged) {
            Toast.makeText(this, "No changes to save", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // If changing password, verify old password
        if (isPasswordChanged) {
            if (oldPasswordInput.isEmpty()) {
                Toast.makeText(this, "Please enter old password to change password", Toast.LENGTH_SHORT).show()
                return
            }
            if (oldPasswordInput != currentPassword) {
                Toast.makeText(this, "Old password is incorrect", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Build ContentValues with only changed fields
        val values = ContentValues()

        if (isEmailChanged) {
            // Check if new email is already taken by another user
            if (dbHelper.isEmailTaken(newEmail)) {
                val existingUserId = dbHelper.getUserIdByEmail(newEmail)
                if (existingUserId != userId) {
                    Toast.makeText(this, "Email already in use", Toast.LENGTH_SHORT).show()
                    return
                }
            }
            values.put(DBHelper.COL_EMAIL, newEmail)
        }

        if (isPasswordChanged) {
            values.put(DBHelper.COL_PASSWORD, newPasswordInput)
        }

        if (isImageChanged) {
            values.put(DBHelper.COL_IMAGE_URI, selectedImageUri)
        }

        val db = dbHelper.writableDatabase

        val rowsUpdated = db.update(
            DBHelper.TABLE_USERS,
            values,
            "${DBHelper.COL_ID} = ?",
            arrayOf(userId.toString())
        )

        db.close()

        if (rowsUpdated > 0) {
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()

            // update session email is changed
            if (isEmailChanged) {
                val prefs = getSharedPreferences("userSession", Context.MODE_PRIVATE)
                prefs.edit().putString("userEmail", newEmail).apply()
            }
            // update session img is changed
            if (isImageChanged) {
                val prefs = getSharedPreferences("userSession", Context.MODE_PRIVATE)
                prefs.edit().putString("userImage", selectedImageUri).apply()
            }

            finish()
        } else {
            Toast.makeText(this, "Failed to update profile.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val fileName = "profile_${userId}.jpg"
            val file = File(filesDir, fileName)

            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)

            inputStream?.close()
            outputStream.close()

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun setupHintStyleBehavior() {
        val emailEditText = binding.editTextEmail
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