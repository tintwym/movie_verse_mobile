package dev.team08.movieverse.ui.detail.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dev.team08.movieverse.data.api.ReviewApiClient
import dev.team08.movieverse.databinding.FragmentReviewsBinding
import dev.team08.movieverse.domain.model.Review
import dev.team08.movieverse.ui.detail.adapters.ReviewAdapter
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ReviewsFragment : Fragment() {
    private var _binding: FragmentReviewsBinding? = null
    private val binding get() = _binding!!
    private val reviewAdapter = ReviewAdapter()
    private val reviewApiClient = ReviewApiClient()
    private var movieId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movieId = arguments?.getInt("movieId")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        fetchReviews()
    }

    private fun setupRecyclerView() {
        binding.reviewsRecyclerView.apply {
            adapter = reviewAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }

    private fun fetchReviews() {
        val movieId = this.movieId ?: run {
            showError("Invalid movie ID")
            return
        }

        val token = getTokenFromSecureStorage() ?: run {
            showError("Please login to view reviews")
            return
        }

        showLoading()

        lifecycleScope.launch {
            try {
                val result = reviewApiClient.getReviews(movieId, token)
                result.fold(
                    onSuccess = { reviews ->
                        hideLoading()
                        if (reviews.isEmpty()) {
                            showEmptyState()
                        } else {
                            showReviews(reviews)
                        }

                        // Add logging to debug response
                        Log.d(TAG, "Reviews received: ${reviews.size}")
                        reviews.forEach { review ->
                            Log.d(TAG, "Review: ${review.originalReviewText} by ${review.user.username}")
                        }
                    },
                    onFailure = { exception ->
                        hideLoading()
                        handleError(exception)
                        Log.e(TAG, "Error fetching reviews", exception)
                    }
                )
            } catch (e: Exception) {
                hideLoading()
                Log.e(TAG, "Error fetching reviews", e)
                showError("Error loading reviews")
            }
        }
    }

    private fun showLoading() {
        binding.apply {
            loadingProgressBar.visibility = View.VISIBLE
            reviewsRecyclerView.visibility = View.GONE
            errorText.visibility = View.GONE
            emptyStateText.visibility = View.GONE
        }
    }

    private fun hideLoading() {
        binding.loadingProgressBar.visibility = View.GONE
    }

    private fun showReviews(reviews: List<Review>) {
        binding.apply {
            reviewsRecyclerView.visibility = View.VISIBLE
            errorText.visibility = View.GONE
            emptyStateText.visibility = View.GONE
            reviewAdapter.submitList(reviews)
        }
    }

    private fun showEmptyState() {
        binding.apply {
            emptyStateText.visibility = View.VISIBLE
            reviewsRecyclerView.visibility = View.GONE
            errorText.visibility = View.GONE
        }
    }

    private fun showError(message: String) {
        binding.apply {
            errorText.apply {
                visibility = View.VISIBLE
                text = message
            }
            reviewsRecyclerView.visibility = View.GONE
            emptyStateText.visibility = View.GONE
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun handleError(exception: Throwable) {
        val errorMessage = when (exception) {
            is HttpException -> when (exception.code()) {
                401 -> "Please login to view reviews"
                403 -> "You don't have permission to view reviews"
                404 -> "No reviews found"
                else -> "Error loading reviews"
            }
            else -> "Error: ${exception.localizedMessage ?: "Unknown error occurred"}"
        }
        showError(errorMessage)
    }

    private fun getTokenFromSecureStorage(): String? {
        return try {
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

            sharedPreferences.getString("auth_token", null)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting token", e)
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "ReviewsFragment"

        fun newInstance(movieId: Int) = ReviewsFragment().apply {
            arguments = Bundle().apply {
                putInt("movieId", movieId)
            }
        }
    }
}
