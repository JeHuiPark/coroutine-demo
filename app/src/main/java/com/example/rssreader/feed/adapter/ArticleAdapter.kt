package com.example.rssreader.feed.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rssreader.R
import com.example.rssreader.feed.model.Article

class ArticleAdapter : RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {

    private val articles: MutableList<Article> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context) .inflate(R.layout.article, parent, false) as LinearLayout
        val feed = layout.findViewById<TextView>(R.id.feed)
        val title = layout.findViewById<TextView>(R.id.title)
        val summary = layout.findViewById<TextView>(R.id.summary)
        return ViewHolder(
            layout = layout,
            feed = feed,
            title = title,
            summary = summary,
        )
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]
        holder.feed.text = article.feed
        holder.title.text = article.title
        holder.summary.text = article.summary
    }

    fun addAll(articles: List<Article>) {
        this.articles.addAll(articles)
        notifyItemInserted(this.articles.size - 1)
    }

    class ViewHolder(
        layout: LinearLayout,
        val feed: TextView,
        val title: TextView,
        val summary: TextView,
    ) : RecyclerView.ViewHolder(layout)
}