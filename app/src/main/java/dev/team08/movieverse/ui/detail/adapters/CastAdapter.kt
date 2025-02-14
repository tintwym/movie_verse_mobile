package dev.team08.movieverse.ui.detail.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.team08.movieverse.databinding.ItemCastBinding
import dev.team08.movieverse.domain.model.Cast

class CastAdapter : ListAdapter<Cast, CastAdapter.CastViewHolder>(CastDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        val binding = ItemCastBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CastViewHolder(
        private val binding: ItemCastBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cast: Cast) {
            binding.apply {
                actorName.text = cast.name
                characterName.text = cast.character

                // Load actor image using Glide
                if (!cast.profile_path.isNullOrEmpty()) {
                    Glide.with(actorImage)
                        .load("https://image.tmdb.org/t/p/w185${cast.profile_path}")
                        .centerCrop()
                        .into(actorImage)
                }
            }
        }
    }

    private class CastDiffCallback : DiffUtil.ItemCallback<Cast>() {
        override fun areItemsTheSame(oldItem: Cast, newItem: Cast): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Cast, newItem: Cast): Boolean {
            return oldItem == newItem
        }
    }
}
