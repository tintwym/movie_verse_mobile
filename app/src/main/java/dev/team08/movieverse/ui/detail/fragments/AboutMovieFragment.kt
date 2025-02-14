package dev.team08.movieverse.ui.detail.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.team08.movieverse.databinding.FragmentAboutMovieBinding

class AboutMovieFragment : Fragment() {
    private lateinit var binding: FragmentAboutMovieBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAboutMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve movie description from arguments
        val movieDescription = arguments?.getString(ARG_MOVIE_DESCRIPTION) ?: "No description available"
        binding.movieDescription.text = movieDescription
    }

    companion object {
        private const val ARG_MOVIE_DESCRIPTION = "movie_description"

        fun newInstance(description: String): AboutMovieFragment {
            val fragment = AboutMovieFragment()
            val args = Bundle()
            args.putString(ARG_MOVIE_DESCRIPTION, description)
            fragment.arguments = args
            return fragment
        }
    }
}
