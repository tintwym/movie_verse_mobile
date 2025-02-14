package dev.team08.movieverse.ui.dashboard.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dev.team08.movieverse.R
import dev.team08.movieverse.data.api.ProfileApiClient
import dev.team08.movieverse.databinding.FragmentProfileBinding
import dev.team08.movieverse.domain.model.UserResponse
import dev.team08.movieverse.ui.login.LoginActivity
import dev.team08.movieverse.ui.profile.ProfileActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkAuthAndUpdateUI()
        setupClickListeners()
    }

    private fun checkAuthAndUpdateUI() {
        val token = getTokenFromSecureStorage()

        if (token.isNullOrEmpty()) {
            // Show guest information
            binding.apply {
                profileName.text = "Guest"
                profileUsername.text = "Not logged in"
                logoutButton.text = "Login"
            }
        } else {
            fetchUserProfile(token)
        }
    }

    private fun fetchUserProfile(token: String) {
        ProfileApiClient.instance.getUserProfile("Bearer $token").enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    binding.profileName.text = user?.username ?: "Unknown User"
                    binding.profileUsername.text = user?.email ?: "Unknown Email"
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getTokenFromSecureStorage(): String? {
        val masterKey = MasterKey.Builder(requireContext())
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPreferences = EncryptedSharedPreferences.create(
            requireContext(),
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        return sharedPreferences.getString("auth_token", null)
    }

    private fun setupClickListeners() {
        binding.apply {
            profileButton.setOnClickListener {
                val token = getTokenFromSecureStorage()
                if (token.isNullOrEmpty()) {
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                } else {
                    // Launch ProfileActivity with current email
                    val intent = Intent(requireContext(), ProfileActivity::class.java).apply {
                        putExtra("current_email", binding.profileUsername.text.toString())
                    }
                    startActivityForResult(intent, PROFILE_UPDATE_REQUEST)
                }
            }

            logoutButton.setOnClickListener {
                if (getTokenFromSecureStorage().isNullOrEmpty()) {
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                } else {
                    showLogoutDialog()
                }
            }
        }
    }

    private fun showLogoutDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_logout, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<TextView>(R.id.cancelButton).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<TextView>(R.id.logoutButton).setOnClickListener {
            clearTokenFromSecureStorage()
            checkAuthAndUpdateUI() // Update UI after logout
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun clearTokenFromSecureStorage() {
        val masterKey = MasterKey.Builder(requireContext())
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPreferences = EncryptedSharedPreferences.create(
            requireContext(),
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        with(sharedPreferences.edit()) {
            remove("auth_token")
            apply()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PROFILE_UPDATE_REQUEST && resultCode == Activity.RESULT_OK) {
            // Refresh profile data after update
            val token = getTokenFromSecureStorage()
            if (!token.isNullOrEmpty()) {
                fetchUserProfile(token)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val PROFILE_UPDATE_REQUEST = 100
    }
}
