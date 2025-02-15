package dev.team08.movieverse.ui.dashboard

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import dev.team08.movieverse.R
import dev.team08.movieverse.databinding.ActivityDashboardBinding
import dev.team08.movieverse.domain.model.Movie
import dev.team08.movieverse.ui.dashboard.adapters.MovieAdapter
import dev.team08.movieverse.ui.dashboard.fragments.HomeFragment
import dev.team08.movieverse.ui.dashboard.fragments.NewsFragment
import dev.team08.movieverse.ui.dashboard.fragments.ProfileFragment
import dev.team08.movieverse.ui.dashboard.viewmodel.MovieViewModel
import dev.team08.movieverse.ui.detail.DetailActivity
import dev.team08.movieverse.utils.AuthManager

class DashboardActivity : AppCompatActivity(), HomeFragment.MovieClickListener {
    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: MovieViewModel by viewModels()
    private lateinit var searchResultsAdapter: MovieAdapter
    private var searchResultsVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupBottomNavigation()
        setupSearch()
        setupSearchResultsRecyclerView()
        observeViewModel()

        if (savedInstanceState == null) {
            showToolbarAndSearch(true)
            loadFragment(HomeFragment())
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupSearchResultsRecyclerView() {
        // Initialize the RecyclerView for search results
        searchResultsAdapter = MovieAdapter { movie ->
            onMovieClicked(movie)
        }

        binding.searchResultsRecyclerView.apply {
            layoutManager = GridLayoutManager(this@DashboardActivity, 2)
            adapter = searchResultsAdapter
            visibility = View.GONE
        }
    }

    private fun setupSearch() {
        val searchEditText = binding.searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.apply {
            setTextColor(Color.WHITE)
            setHintTextColor(Color.WHITE)
        }

        val searchIcon = binding.searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        searchIcon.setColorFilter(Color.WHITE)

        val closeIcon = binding.searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        closeIcon.setColorFilter(Color.WHITE)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    performSearch(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    showSearchResults(false)
                } else if (newText.length >= 3) {
                    performSearch(newText)
                }
                return true
            }
        })

        binding.searchView.setOnCloseListener {
            showSearchResults(false)
            true
        }
    }

    private fun performSearch(query: String) {
        viewModel.searchMovies(query)
        showSearchResults(true)
    }

    private fun showSearchResults(show: Boolean) {
        searchResultsVisible = show
        binding.fragmentContainer.visibility = if (show) View.GONE else View.VISIBLE
        binding.searchResultsRecyclerView.visibility = if (show) View.VISIBLE else View.GONE
        binding.noResultsView.visibility = View.GONE

        if (!show) {
            viewModel.loadPopularMovies() // Reset to popular movies when exiting search
        }
    }

    private fun observeViewModel() {
        viewModel.movies.observe(this) { movies ->
            if (searchResultsVisible) {
                searchResultsAdapter.submitList(movies)
                binding.noResultsView.visibility =
                    if (movies.isEmpty()) View.VISIBLE else View.GONE
            } else {
                // Update your HomeFragment's movie list
                val homeFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as? HomeFragment
                homeFragment?.updateMovies(movies)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    showToolbarAndSearch(true)
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_news -> {
                    showToolbarAndSearch(true)
                    loadFragment(NewsFragment())
                    true
                }
                R.id.nav_profile -> {
                    showToolbarAndSearch(false)
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun showToolbarAndSearch(show: Boolean) {
        binding.toolbar.visibility = if (show) View.VISIBLE else View.GONE
        binding.searchView.parent.let { parent ->
            (parent as? View)?.visibility = if (show) View.VISIBLE else View.GONE
        }
        binding.scrollView.scrollTo(0, 0)
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commitNow()
    }

    override fun onBackPressed() {
        when {
            searchResultsVisible -> {
                showSearchResults(false)
                binding.searchView.setQuery("", false)
                binding.searchView.clearFocus()
            }
            else -> super.onBackPressed()
        }
    }

    override fun onMovieClicked(movie: Movie) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("movie", movie)
        }
        startActivity(intent)
    }
}
