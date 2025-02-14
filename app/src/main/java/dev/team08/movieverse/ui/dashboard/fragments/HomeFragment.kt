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
import androidx.recyclerview.widget.GridLayoutManager
import dev.team08.movieverse.databinding.FragmentHomeBinding
import dev.team08.movieverse.domain.model.Movie
import dev.team08.movieverse.ui.dashboard.adapters.MovieAdapter
import dev.team08.movieverse.ui.dashboard.viewmodel.MovieViewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MovieViewModel by activityViewModels()
    private lateinit var movieAdapter: MovieAdapter
    private var movieClickListener: MovieClickListener? = null

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
        setupRecyclerView()
        observeViewModel()
    }

    fun updateMovies(movies: List<Movie>) {
        movieAdapter.submitList(movies)
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter { movie ->
            movieClickListener?.onMovieClicked(movie)
        }

        binding.moviesRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = movieAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.movies.observe(viewLifecycleOwner) { movies ->
            movieAdapter.submitList(movies)
            binding.moviesRecyclerView.isVisible = movies.isNotEmpty()
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.moviesRecyclerView.adapter = null
        _binding = null
    }

    interface MovieClickListener {
        fun onMovieClicked(movie: Movie)
    }
}
