package dev.team08.movieverse.ui.feedback

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKey.*
import dev.team08.movieverse.databinding.ActivityFeedbackBinding
import dev.team08.movieverse.domain.model.FeedbackRequest
import dev.team08.movieverse.data.api.RetrofitInstance
import dev.team08.movieverse.data.api.ReviewApiClient
import kotlinx.coroutines.launch
import retrofit2.HttpException

class FeedbackActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedbackBinding
    private val reviewApiClient = ReviewApiClient()
    private var movieId: Int? = null
    private var movieTitle: String? = null

    companion object {
        private const val TAG = "FeedbackActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        movieId = intent.getIntExtra("movieId", -1)
        movieTitle = intent.getStringExtra("movieTitle") ?: "Unknown Movie"

        Log.d(TAG, "Received movieId: $movieId, movieTitle: $movieTitle")

        if (movieId == -1) {
            Toast.makeText(this, "Invalid movie data", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupUI()
        loadExistingReview()
    }

    private fun setupUI() {
        binding.titleText.text = "Feedback for: $movieTitle"

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.submitButton.setOnClickListener {
            submitFeedback()
        }

        // Initially disable editing
        binding.reviewContent.isEnabled = false

        // Optional: Enable editing when edit button is clicked
        binding.editButton.setOnClickListener {
            binding.reviewContent.isEnabled = true
            binding.reviewContent.requestFocus()
            binding.submitButton.isEnabled = true
        }
    }

    private fun loadExistingReview() {
        val token = getTokenFromSecureStorage()
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Authentication error", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                setLoadingState(true)
                val result = reviewApiClient.getReviewForMovie(
                    movieId = movieId ?: throw IllegalStateException("Movie ID is null"),
                    token = token
                )

                result.fold(
                    onSuccess = { review ->
                        binding.reviewContent.setText(review.originalReviewText)
                        binding.reviewContent.isEnabled = false // Disable editing initially
                        binding.editButton.visibility = View.VISIBLE // Show edit button
                        binding.submitButton.isEnabled = false // Disable submit button until edit
                    },
                    onFailure = { exception ->
                        Log.d(TAG, "No existing review found or error occurred", exception)
                        binding.reviewContent.isEnabled = true // Enable editing for new review
                        binding.editButton.visibility = View.GONE
                        binding.submitButton.isEnabled = true
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error loading existing review", e)
                binding.reviewContent.isEnabled = true
                binding.editButton.visibility = View.GONE
                binding.submitButton.isEnabled = true
            } finally {
                setLoadingState(false)
            }
        }
    }

    private fun getTokenFromSecureStorage(): String? {
        val masterKey = Builder(this)
            .setKeyScheme(KeyScheme.AES256_GCM)
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

    private fun submitFeedback() {
        setLoadingState(true)

        val reviewText = binding.reviewContent.text.toString().trim()
        val token = getTokenFromSecureStorage()

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Authentication error", Toast.LENGTH_SHORT).show()
            setLoadingState(false)
            return
        }

        if (reviewText.isEmpty()) {
            Toast.makeText(this, "Please provide feedback!", Toast.LENGTH_SHORT).show()
            setLoadingState(false)
            return
        }

        lifecycleScope.launch {
            try {
                val result = reviewApiClient.submitReview(
                    movieId = movieId ?: throw IllegalStateException("Movie ID is null"),
                    reviewText = reviewText,
                    token = token
                )

                result.fold(
                    onSuccess = { response ->
                        Toast.makeText(this@FeedbackActivity,
                            "Feedback submitted successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    },
                    onFailure = { exception ->
                        val errorMessage = when (exception) {
                            is HttpException -> when (exception.code()) {
                                400 -> "Invalid feedback data"
                                401 -> "Please login again"
                                403 -> "You don't have permission to submit feedback"
                                404 -> "Movie not found"
                                500 -> "Server error. Please try again later"
                                else -> "Failed to submit feedback: ${exception.message()}"
                            }
                            else -> "Error: ${exception.localizedMessage ?: "Unknown error occurred"}"
                        }
                        showError(errorMessage)
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error submitting feedback", e)
                showError("Error: ${e.localizedMessage ?: "Unknown error occurred"}")
            } finally {
                setLoadingState(false)
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.apply {
            submitButton.isEnabled = !isLoading
            ratingBar.isEnabled = !isLoading
            reviewContent.isEnabled = !isLoading
        }
    }
}
