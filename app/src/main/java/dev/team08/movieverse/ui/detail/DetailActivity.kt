package dev.team08.movieverse.ui.detail

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import dev.team08.movieverse.R
import dev.team08.movieverse.data.api.FavoriteApiClient
import dev.team08.movieverse.data.api.WatchListApiClient
import dev.team08.movieverse.databinding.ActivityDetailBinding
import dev.team08.movieverse.domain.model.Movie
import dev.team08.movieverse.ui.detail.fragments.AboutMovieFragment
import dev.team08.movieverse.ui.detail.fragments.CastFragment
import dev.team08.movieverse.ui.detail.fragments.ReviewsFragment
import dev.team08.movieverse.ui.feedback.FeedbackActivity
import dev.team08.movieverse.ui.login.LoginActivity
import dev.team08.movieverse.utils.AuthManager
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Locale

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val favoriteApiClient = FavoriteApiClient()
    private val watchListApiClient = WatchListApiClient()
    private var movie: Movie? = null
    private var isLiked = false
    private var isInWatchlist = false

    companion object {
        private const val TAG = "DetailActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        movie = intent.getParcelableExtra("movie")

        if (movie == null) {
            Toast.makeText(this, "Error: Movie data is missing", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupUI(movie!!)
        setupTabLayout()
        loadMovieInteraction()

        binding.feedbackButton.setOnClickListener {
            openFeedbackActivity()
        }
    }

    private fun loadMovieInteraction() {
        val token = AuthManager.getAuthToken(this)
        if (token.isNullOrEmpty()) return

        movie?.id?.let { movieId ->
            lifecycleScope.launch {
                try {
                    val result = watchListApiClient.getMovieInteraction(movieId, token)
                    result.fold(
                        onSuccess = { interaction ->
                            isLiked = interaction.favorite
                            isInWatchlist = interaction.watchStatus == "PLANNED"
                            updateLikeButtonUI()
                            updateBookmarkButtonUI()
                        },
                        onFailure = { exception ->
                            Log.e(TAG, "Error loading interaction", exception)
                        }
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error loading interaction", e)
                }
            }
        }
    }

    private fun setupUI(movie: Movie) {
        binding.apply {
            titleDetail.text = "Detail"
            movieTitle.text = movie.title ?: "Unknown Title"

            ratingText.text = movie.voteAverage?.let {
                String.format("%.1f", it)
            } ?: "N/A"

            Glide.with(this@DetailActivity)
                .load("https://image.tmdb.org/t/p/w500${movie.backdropPath}")
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .into(backdropImage)

            Glide.with(this@DetailActivity)
                .load("https://image.tmdb.org/t/p/w500${movie.posterPath}")
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .into(posterImage)

            movieInfo.apply {
                yearText.visibility = View.GONE
                durationText.visibility = View.GONE
                genreText.visibility = View.GONE
                divider1.visibility = View.GONE
                divider2.visibility = View.GONE

                var hasYear = false
                var hasDuration = false

                if (!movie.releaseDate.isNullOrEmpty()) {
                    try {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val date = dateFormat.parse(movie.releaseDate)
                        val year = date?.let { SimpleDateFormat("yyyy", Locale.getDefault()).format(it) }
                        if (!year.isNullOrEmpty()) {
                            yearText.text = year
                            yearText.visibility = View.VISIBLE
                            hasYear = true
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing date", e)
                    }
                }

                if (movie.runtime != null && movie.runtime > 0) {
                    durationText.text = "${movie.runtime} Minutes"
                    durationText.visibility = View.VISIBLE
                    if (hasYear) {
                        divider1.visibility = View.VISIBLE
                    }
                    hasDuration = true
                }

                if (!movie.genres.isNullOrEmpty()) {
                    movie.genres.firstOrNull()?.let { genre ->
                        genreText.text = genre.name
                        genreText.visibility = View.VISIBLE
                        if (hasYear || hasDuration) {
                            divider2.visibility = View.VISIBLE
                        }
                    }
                }
            }

            backButton.setOnClickListener { finish() }

            bookmarkButton.setOnClickListener {
                val token = AuthManager.getAuthToken(this@DetailActivity)
                if (token.isNullOrEmpty()) {
                    val intent = Intent(this@DetailActivity, LoginActivity::class.java)
                    startActivity(intent)
                    return@setOnClickListener
                }
                toggleWatchlist(movie.id, token)
            }

            likeButton.setOnClickListener {
                val token = AuthManager.getAuthToken(this@DetailActivity)
                if (token.isNullOrEmpty()) {
                    val intent = Intent(this@DetailActivity, LoginActivity::class.java)
                    startActivity(intent)
                    return@setOnClickListener
                }
                toggleFavorite(movie.id, token)
            }
        }
    }

    private fun toggleFavorite(movieId: Int, token: String) {
        lifecycleScope.launch {
            try {
                val result = favoriteApiClient.toggleFavorite(movieId, token)

                result.fold(
                    onSuccess = { message ->
                        isLiked = !isLiked
                        updateLikeButtonUI()
                        Toast.makeText(this@DetailActivity, message, Toast.LENGTH_SHORT).show()
                    },
                    onFailure = { exception ->
                        val errorMessage = when {
                            exception is HttpException -> when (exception.code()) {
                                401 -> "Please login again"
                                else -> "Error updating favorite status"
                            }
                            else -> "Error: ${exception.localizedMessage ?: "Unknown error occurred"}"
                        }
                        Toast.makeText(this@DetailActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                )
            } catch (e: Exception) {
                Toast.makeText(
                    this@DetailActivity,
                    "Error: ${e.localizedMessage ?: "Unknown error occurred"}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun toggleWatchlist(movieId: Int, token: String) {
        lifecycleScope.launch {
            try {
                val result = watchListApiClient.toggleWatchlist(movieId, token)

                result.fold(
                    onSuccess = { message ->
                        isInWatchlist = !isInWatchlist
                        updateBookmarkButtonUI()
                        Toast.makeText(this@DetailActivity, message, Toast.LENGTH_SHORT).show()
                    },
                    onFailure = { exception ->
                        val errorMessage = when {
                            exception is HttpException -> when (exception.code()) {
                                401 -> "Please login again"
                                else -> "Error updating watchlist status"
                            }
                            else -> "Error: ${exception.localizedMessage ?: "Unknown error occurred"}"
                        }
                        Toast.makeText(this@DetailActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                )
            } catch (e: Exception) {
                Toast.makeText(
                    this@DetailActivity,
                    "Error: ${e.localizedMessage ?: "Unknown error occurred"}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateLikeButtonUI() {
        binding.likeButton.apply {
            setImageResource(if (isLiked) R.drawable.ic_heart_filled else R.drawable.ic_heart)
            imageTintList = ColorStateList.valueOf(
                if (isLiked)
                    ContextCompat.getColor(this@DetailActivity, R.color.red)
                else
                    ContextCompat.getColor(this@DetailActivity, R.color.white)
            )
        }
    }

    private fun updateBookmarkButtonUI() {
        binding.bookmarkButton.apply {
            setImageResource(
                if (isInWatchlist) R.drawable.ic_bookmark_filled
                else R.drawable.ic_bookmark
            )
            imageTintList = ColorStateList.valueOf(
                if (isInWatchlist)
                    ContextCompat.getColor(this@DetailActivity, R.color.blue_500)
                else
                    ContextCompat.getColor(this@DetailActivity, R.color.white)
            )
        }
    }

    private fun setupTabLayout() {
        val movieId = movie?.id ?: return

        val fragments = listOf(
            AboutMovieFragment.newInstance(movie?.overview ?: "No description available"),
            ReviewsFragment.newInstance(movieId),
            CastFragment.newInstance(movieId)
        )

        val titles = listOf("About Movie", "Reviews", "Cast")

        binding.viewPager.adapter = ViewPagerAdapter(this, fragments)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.feedbackButton.visibility = if (position == 1) View.VISIBLE else View.GONE
            }
        })
    }

    private fun openFeedbackActivity() {
        val token = AuthManager.getAuthToken(this)

        if (token.isNullOrEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, FeedbackActivity::class.java).apply {
                putExtra("movieId", movie?.id)
                putExtra("movieTitle", movie?.title)
            }
            startActivity(intent)
        }
    }

    inner class ViewPagerAdapter(
        activity: AppCompatActivity,
        private val fragments: List<Fragment>
    ) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = fragments.size
        override fun createFragment(position: Int): Fragment = fragments[position]
    }
}
