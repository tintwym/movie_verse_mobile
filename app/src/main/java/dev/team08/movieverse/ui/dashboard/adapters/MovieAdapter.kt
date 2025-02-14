package dev.team08.movieverse.ui.dashboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.team08.movieverse.databinding.ItemMovieBinding
import dev.team08.movieverse.domain.model.Movie

class MovieAdapter(private val onClick: (Movie) -> Unit) :
    ListAdapter<Movie, MovieAdapter.MovieViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        holder.bind(movie)
    }

    inner class MovieViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.apply {
                movieTitle.text = movie.title
                movieRating.text = movie.voteAverage?.let {
                    String.format("%.1f", it)
                } ?: "N/A"

                val posterUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"

                Glide.with(root)
                    .load(posterUrl)
                    .placeholder(dev.team08.movieverse.R.drawable.ic_image_placeholder)
                    .error(dev.team08.movieverse.R.drawable.ic_image_placeholder)
                    .into(moviePoster)

                root.setOnClickListener {
                    onClick(movie)
                }
            }
        }
    }
}

class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
        oldItem == newItem
}
