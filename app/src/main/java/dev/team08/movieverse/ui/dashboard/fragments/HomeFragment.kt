package dev.team08.movieverse.ui.dashboard.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dev.team08.movieverse.databinding.FragmentHomeBinding
import dev.team08.movieverse.domain.model.Movie
import dev.team08.movieverse.ui.dashboard.adapters.MovieAdapter
import dev.team08.movieverse.ui.dashboard.viewmodel.MovieViewModel
import dev.team08.movieverse.utils.AuthManager

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MovieViewModel by activityViewModels()
    private lateinit var popularMoviesAdapter: MovieAdapter
    private lateinit var recommendedMoviesAdapter: MovieAdapter
    private var movieClickListener: MovieClickListener? = null
    private var isUserLoggedIn = false  // Track login status

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MovieClickListener) {
            movieClickListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isUserLoggedIn = !AuthManager.getAuthToken(requireContext()).isNullOrEmpty() // Check login status

        setupRecyclerViews()
        observeViewModel()

        // Always load popular movies
        viewModel.loadPopularMovies()

        if (isUserLoggedIn) {
            viewModel.loadRecommendedMovies() // Only load recommended movies if logged in
        } else {
            hideRecommendedMovies()
        }
    }

    private fun setupRecyclerViews() {
        popularMoviesAdapter = MovieAdapter { movie ->
            movieClickListener?.onMovieClicked(movie)
        }

        recommendedMoviesAdapter = MovieAdapter { movie ->
            movieClickListener?.onMovieClicked(movie)
        }

        binding.moviesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = popularMoviesAdapter
        }

        binding.recommendedMoviesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = recommendedMoviesAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.movies.observe(viewLifecycleOwner) { movies ->
            popularMoviesAdapter.submitList(movies)
            binding.moviesRecyclerView.isVisible = movies.isNotEmpty()
            binding.popularMoviesLabel.isVisible = movies.isNotEmpty()
        }

        viewModel.recommendedMovies.observe(viewLifecycleOwner) { movies ->
            if (isUserLoggedIn) {  // Only update if user is logged in
                recommendedMoviesAdapter.submitList(movies)
                binding.recommendedMoviesRecyclerView.isVisible = movies.isNotEmpty()
                binding.recommendedMoviesLabel.isVisible = movies.isNotEmpty()
                binding.noRecommendationsText.isVisible = movies.isEmpty() // Show text when no movies
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun hideRecommendedMovies() {
        binding.recommendedMoviesRecyclerView.isVisible = false
        binding.recommendedMoviesLabel.isVisible = false
        binding.noRecommendationsText.isVisible = false  // Hide "No recommendations" text for guests
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.moviesRecyclerView.adapter = null
        binding.recommendedMoviesRecyclerView.adapter = null
        _binding = null
    }

    interface MovieClickListener {
        fun onMovieClicked(movie: Movie)
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}