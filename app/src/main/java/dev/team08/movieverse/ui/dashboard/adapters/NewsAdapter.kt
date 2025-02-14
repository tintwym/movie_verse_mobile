package dev.team08.movieverse.ui.dashboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.team08.movieverse.databinding.ItemNewsBinding
import dev.team08.movieverse.domain.model.News

class NewsAdapter(private val news: List<News>) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsItem = news[position]
        holder.binding.apply {
            newsTitle.text = newsItem.title
            newsDescription.text = newsItem.description
        }
    }

    override fun getItemCount() = news.size
}
