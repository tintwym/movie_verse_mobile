package dev.team08.movieverse.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dev.team08.movieverse.databinding.ActivityProfileBinding
import dev.team08.movieverse.data.api.ProfileApiClient
import dev.team08.movieverse.domain.model.UpdateEmailRequest
import dev.team08.movieverse.domain.model.UpdateProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        val currentEmail = intent.getStringExtra("current_email") ?: ""
        binding.emailEditText.setText(currentEmail)
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.saveButton.setOnClickListener {
            val newEmail = binding.emailEditText.text.toString()
            if (newEmail.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                updateEmail(newEmail)
            } else {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateEmail(newEmail: String) {
        val token = getTokenFromSecureStorage()
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Authentication error", Toast.LENGTH_SHORT).show()
            return
        }

        val updateRequest = UpdateEmailRequest(newEmail)

        ProfileApiClient.instance.updateProfile("Bearer $token", updateRequest)
            .enqueue(object : Callback<UpdateProfileResponse> {
                override fun onResponse(
                    call: Call<UpdateProfileResponse>,
                    response: Response<UpdateProfileResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ProfileActivity, "Email updated successfully", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this@ProfileActivity, "Failed to update email", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                    Toast.makeText(this@ProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getTokenFromSecureStorage(): String? {
        val masterKey = MasterKey.Builder(this)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPreferences = EncryptedSharedPreferences.create(
            this,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        return sharedPreferences.getString("auth_token", null)
    }
}
