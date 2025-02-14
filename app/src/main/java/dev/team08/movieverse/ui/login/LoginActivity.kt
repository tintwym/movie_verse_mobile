package dev.team08.movieverse.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dev.team08.movieverse.data.api.RetrofitClient
import dev.team08.movieverse.databinding.ActivityLoginBinding
import dev.team08.movieverse.domain.model.LoginRequest
import dev.team08.movieverse.domain.model.LoginResponse
import dev.team08.movieverse.ui.dashboard.DashboardActivity
import dev.team08.movieverse.ui.register.RegisterActivity
import dev.team08.movieverse.utils.AuthManager
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.apply {
            // Login button click
            loginButton.setOnClickListener {
                val username = usernameInput.editText?.text.toString()
                val password = passwordInput.editText?.text.toString()

                if (validateInputs(username, password)) {
                    performLogin(username, password)
                }
            }

            // Guest button click
            guestButton.setOnClickListener {
                // Clear any existing auth token to ensure clean guest state
                AuthManager.clearAuthToken(this@LoginActivity)
                navigateToDashboard()
            }

            // Forgot password click
            forgotPasswordText.setOnClickListener {
                // Handle forgot password click
            }

            // Register now click
            registerText.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
        }
    }

    private fun validateInputs(username: String, password: String): Boolean {
        var isValid = true

        // Validate username
        if (username.isEmpty()) {
            binding.usernameInput.error = "Please enter your username"
            isValid = false
        } else if (username.length < 3) {
            binding.usernameInput.error = "Username must be at least 3 characters"
            isValid = false
        } else {
            binding.usernameInput.error = null
        }

        // Validate password
        if (password.isEmpty()) {
            binding.passwordInput.error = "Please enter your password"
            isValid = false
        } else if (password.length < 6) {
            binding.passwordInput.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            binding.passwordInput.error = null
        }

        return isValid
    }

    private fun performLogin(username: String, password: String) {
        setLoadingState(true)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.authApiService.login(LoginRequest(username, password))
                if (response.isSuccessful) {
                    response.body()?.let {
                        handleSuccessfulLogin(it)
                    } ?: throw Exception("Empty response body")
                } else {
                    throw Exception("Login failed: ${response.message()}")
                }
            } catch (e: Exception) {
                handleLoginError(e)
            } finally {
                setLoadingState(false)
            }
        }
    }

    private fun handleSuccessfulLogin(response: LoginResponse) {
        // Save token using AuthManager
        AuthManager.saveAuthToken(this, response.token)
        navigateToDashboard()
    }

    private fun navigateToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    private fun handleLoginError(error: Exception) {
        val errorMessage = when (error) {
            is HttpException -> when (error.code()) {
                401 -> "Invalid username or password"
                403 -> "Account locked. Please contact support"
                else -> "Login failed. Please try again"
            }
            is IOException -> "Network error. Please check your connection"
            else -> "Login failed: ${error.localizedMessage ?: "Unknown error"}"
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.apply {
            loginButton.isEnabled = !isLoading
            guestButton.isEnabled = !isLoading
            usernameInput.isEnabled = !isLoading
            passwordInput.isEnabled = !isLoading
        }
    }
}
