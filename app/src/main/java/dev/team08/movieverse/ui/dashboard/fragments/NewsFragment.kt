package dev.team08.movieverse.ui.dashboard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dev.team08.movieverse.databinding.FragmentNewsBinding
import dev.team08.movieverse.ui.dashboard.adapters.MovieAdapter
import dev.team08.movieverse.ui.dashboard.viewmodel.MovieViewModel

class NewsFragment : Fragment() {
    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MovieViewModel by activityViewModels()
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        viewModel.loadUpcomingMovies()
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter { movie ->
            // Handle movie click
            (activity as? HomeFragment.MovieClickListener)?.onMovieClicked(movie)
        }

        binding.newsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = movieAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        viewModel.upcomingMovies.observe(viewLifecycleOwner) { movies ->
            movieAdapter.submitList(movies)
            binding.newsRecyclerView.isVisible = movies.isNotEmpty()
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding.newsRecyclerView.adapter = null
        _binding = null
    }

    companion object {
        fun newInstance() = NewsFragment()
    }
}
