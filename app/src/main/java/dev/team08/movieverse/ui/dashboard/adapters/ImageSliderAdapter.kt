package dev.team08.movieverse.ui.dashboard.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import dev.team08.movieverse.R

class ImageSliderAdapter : RecyclerView.Adapter<ImageSliderAdapter.SliderViewHolder>() {

    class SliderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.sliderImage)

        fun bind(position: Int) {
            // Load image using your preferred image loading library
            // Example: Glide.with(imageView).load(imageUrl).into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_slider, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = 5 // Number of slider images
}
