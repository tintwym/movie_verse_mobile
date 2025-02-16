package dev.team08.movieverse.ui.detail.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.team08.movieverse.R
import dev.team08.movieverse.databinding.ItemReviewBinding
import dev.team08.movieverse.domain.model.Review
import java.text.SimpleDateFormat
import java.util.Locale

class ReviewAdapter : ListAdapter<Review, ReviewAdapter.ReviewViewHolder>(ReviewDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ReviewViewHolder(private val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(review: Review) {
            binding.apply {
                username.text = review.user.username
                reviewText.text = review.originalReviewText
                createdAt.text = formatDate(review.createdAt)

                val sentiment = review.reviewSentiment?.toIntOrNull() ?: -1

                // Set color based on review text
                when (sentiment) {
                    1 -> { // Positive Review
                        root.setBackgroundColor(ContextCompat.getColor(root.context, R.color.review_positive))
                        createdAt.setTextColor(ContextCompat.getColor(root.context, R.color.white)) // White text for date
                    }
                    0 -> { // Negative Review
                        root.setBackgroundColor(ContextCompat.getColor(root.context, R.color.review_negative))
                        createdAt.setTextColor(ContextCompat.getColor(root.context, R.color.black)) // Black text for date
                    }
                    else -> { // Default Review
                        root.setBackgroundColor(ContextCompat.getColor(root.context, R.color.review_default))
                        createdAt.setTextColor(ContextCompat.getColor(root.context, R.color.default_date_color)) // Default text color
                    }
                }
            }
        }

        private fun formatDate(dateString: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
                val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val date = inputFormat.parse(dateString)
                outputFormat.format(date ?: return dateString)
            } catch (e: Exception) {
                dateString
            }
        }
    }

    private class ReviewDiffCallback : DiffUtil.ItemCallback<Review>() {
        override fun areItemsTheSame(oldItem: Review, newItem: Review) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Review, newItem: Review) =
            oldItem == newItem
    }
}
