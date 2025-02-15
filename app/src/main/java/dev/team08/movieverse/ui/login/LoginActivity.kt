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
    private val usernamePattern = "^[a-zA-Z0-9_]{5,}$".toRegex()
    private val passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[~!@#\$%^&*()_+\\[\\]{}\\|\\'\":;<>?,./]).{6,}$".toRegex()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.apply {
            loginButton.setOnClickListener {
                val username = usernameInput.editText?.text.toString()
                val password = passwordInput.editText?.text.toString()

                if (validateInputs(username, password)) {
                    performLogin(username, password)
                }
            }

            guestButton.setOnClickListener {
                AuthManager.clearAuthToken(this@LoginActivity)
                navigateToDashboard()
            }

            forgotPasswordText.setOnClickListener {
                // Handle forgot password click
            }

            registerText.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
        }
    }

    private fun validateInputs(username: String, password: String): Boolean {
        var isValid = true

        if (!username.matches(usernamePattern)) {
            binding.usernameInput.error = "Username must be at least 5 characters, no spaces, no dashes, only underscore allowed"
            isValid = false
        } else {
            binding.usernameInput.error = null
        }

        if (!password.matches(passwordPattern)) {
            binding.passwordInput.error = "Password must be at least 6 characters, include 1 uppercase, 1 lowercase, 1 number, and 1 special character"
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
