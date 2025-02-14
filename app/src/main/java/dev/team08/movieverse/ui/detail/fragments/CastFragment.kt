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
import dev.team08.movieverse.common.Constants
import dev.team08.movieverse.databinding.FragmentCastBinding
import dev.team08.movieverse.ui.detail.adapters.CastAdapter
import dev.team08.movieverse.data.api.CastApiService
import kotlinx.coroutines.launch

class CastFragment : Fragment() {
    private var _binding: FragmentCastBinding? = null  // Fixed asterisk
    private val binding get() = _binding!!  // Fixed asterisk

    private val castAdapter = CastAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCastBinding.inflate(inflater, container, false)  // Fixed asterisk
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("CastFragment", "onViewCreated called")

        binding.castRecyclerView.apply {
            adapter = castAdapter
            layoutManager = LinearLayoutManager(context)
        }

        val movieId = arguments?.getInt(ARG_MOVIE_ID)  // Fixed asterisk
        Log.d("CastFragment", "Movie ID from arguments: $movieId")

        if (movieId == null) {
            Log.e("CastFragment", "No movie ID provided")
            return
        }

        fetchMovieCast(movieId)
    }

    private fun fetchMovieCast(movieId: Int) {
        lifecycleScope.launch {
            try {
                Log.d("CastFragment", "Fetching cast for movie ID: $movieId")

                val response = CastApiService.instance.getMovieCast(
                    movieId = movieId,
                    apiKey = Constants.API_KEY
                )

                Log.d("CastFragment", "Cast response received: ${response.cast.size} items")

                if (response.cast.isNotEmpty()) {
                    Log.d("CastFragment", "Submitting cast list to adapter")
                    castAdapter.submitList(response.cast)
                } else {
                    Log.d("CastFragment", "No cast available")
                    Toast.makeText(requireContext(), "No cast available.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("CastFragment", "Error fetching cast", e)
                Toast.makeText(requireContext(), "Failed to load cast: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Fixed asterisk
    }

    companion object {
        private const val ARG_MOVIE_ID = "movie_id"  // Fixed asterisk

        fun newInstance(movieId: Int): CastFragment {
            val fragment = CastFragment()
            val args = Bundle()
            args.putInt(ARG_MOVIE_ID, movieId)
            fragment.arguments = args
            return fragment
        }
    }
}
