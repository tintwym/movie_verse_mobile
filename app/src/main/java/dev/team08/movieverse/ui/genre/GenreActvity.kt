package dev.team08.movieverse.ui.genre

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import dev.team08.movieverse.R
import dev.team08.movieverse.data.api.RegisterApiClient
import dev.team08.movieverse.databinding.ActivityGenreBinding
import dev.team08.movieverse.domain.model.Genre
import dev.team08.movieverse.domain.model.RegisterRequest
import dev.team08.movieverse.ui.dashboard.DashboardActivity
import dev.team08.movieverse.utils.AuthManager
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class GenreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGenreBinding
    private val selectedGenres = mutableSetOf<Genre>()

    companion object {
        const val EXTRA_USERNAME = "extra_username"
        const val EXTRA_EMAIL = "extra_email"
        const val EXTRA_PASSWORD = "extra_password"
    }

    private val username by lazy { intent.getStringExtra(EXTRA_USERNAME) ?: "" }
    private val email by lazy { intent.getStringExtra(EXTRA_EMAIL) ?: "" }
    private val password by lazy { intent.getStringExtra(EXTRA_PASSWORD) ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCheckboxListeners()
        setupSignUpButton()
    }

    private fun setupCheckboxListeners() {
        val checkboxMap = mapOf(
            binding.checkboxAction to Triple(Genre(28, "Action"), binding.checkIconAction, binding.textAction),
            binding.checkboxDrama to Triple(Genre(18, "Drama"), binding.checkIconDrama, binding.textDrama),
            binding.checkboxComedy to Triple(Genre(35, "Comedy"), binding.checkIconComedy, binding.textComedy),
            binding.checkboxDocumentary to Triple(Genre(99, "Documentary"), binding.checkIconDocumentary, binding.textDocumentary),
            binding.checkboxHorror to Triple(Genre(27, "Horror"), binding.checkIconHorror, binding.textHorror),
            binding.checkboxSciFi to Triple(Genre(878, "Science Fiction"), binding.checkIconSciFi, binding.textSciFi),
            binding.checkboxAnimation to Triple(Genre(16, "Animation"), binding.checkIconAnimation, binding.textAnimation),
            binding.checkboxFantasy to Triple(Genre(14, "Fantasy"), binding.checkIconFantasy, binding.textFantasy)
        )

        checkboxMap.forEach { (cardView, triple) ->
            val (genre, checkIcon, textView) = triple
            cardView.setOnClickListener {
                cardView.isChecked = !cardView.isChecked
                val isChecked = cardView.isChecked

                // Update card background
                cardView.setCardBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        if (isChecked) R.color.blue_100 else R.color.card_background
                    )
                )

                // Update check icon visibility
                checkIcon.visibility = if (isChecked) View.VISIBLE else View.INVISIBLE

                // Update text color
                textView.setTextColor(
                    ContextCompat.getColor(
                        this,
                        if (isChecked) android.R.color.black else android.R.color.white
                    )
                )

                if (isChecked) selectedGenres.add(genre)
                else selectedGenres.remove(genre)

                updateProgressBar()
            }
        }
    }

    private fun updateProgressBar() {
        val progress = (selectedGenres.size.toFloat() / 3 * 100).toInt()
        binding.progressBar.progress = minOf(progress, 100)
    }

    private fun setupSignUpButton() {
        binding.buttonSignUp.setOnClickListener {
            if (selectedGenres.isEmpty()) {
                Toast.makeText(this, "Please select at least one genre", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerUser()
        }
    }

    private fun registerUser() {
        lifecycleScope.launch {
            try {
                showLoading(true)

                val request = RegisterRequest(
                    username = username,
                    email = email,
                    password = password,
                    favoriteGenres = selectedGenres.map { it.name }
                )

                val response = RegisterApiClient.registerApiService.register(request)

                if (response.isSuccessful) {
                    response.body()?.let { registerResponse ->
                        // Save auth token
                        AuthManager.saveAuthToken(this@GenreActivity, registerResponse.token)

                        // Navigate to dashboard
                        startActivity(Intent(this@GenreActivity, DashboardActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                        finishAffinity()
                    } ?: throw Exception("Empty response body")
                } else {
                    throw HttpException(response)
                }
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is HttpException -> when (e.code()) {
                        409 -> "Username or email already exists"
                        400 -> "Invalid registration data"
                        else -> "Registration failed (${e.code()})"
                    }
                    is IOException -> "Network error. Please check your connection"
                    else -> "Registration failed: ${e.localizedMessage}"
                }
                showError(errorMessage)
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun showLoading(show: Boolean) {
        binding.buttonSignUp.isEnabled = !show
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE

        // Disable all genre cards during loading
        binding.root.findViewById<View>(R.id.scrollView).isEnabled = !show
    }
}
