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
            loginButton.setOnClickListener {
                val username = usernameInput.editText?.text.toString()
                val password = passwordInput.editText?.text.toString()

                if (username.length >= 3 && password.length >= 6) {
                    performLogin(username, password)
                }
            }

            guestButton.setOnClickListener {
                AuthManager.clearAuthToken(this@LoginActivity)
                navigateToDashboard()
            }

            registerText.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
        }
    }

    private fun performLogin(username: String, password: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.authApiService.login(LoginRequest(username, password))
                response.body()?.let {
                    AuthManager.saveAuthToken(this@LoginActivity, it.token)
                    navigateToDashboard()
                } ?: throw Exception("Empty response body")
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Login failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}
