package com.example.rssreader.feed.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rssreader.R
import com.example.rssreader.feed.model.Article
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ArticleAdapter(
    private val articleLoader: ArticleLoader? = null,
) : RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {

    private val articles: MutableList<Article> = mutableListOf()
    private var loading: Boolean = false

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

    @OptIn(DelicateCoroutinesApi::class)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]
        holder.feed.text = article.feed
        holder.title.text = article.title
        holder.summary.text = article.summary

        // request more articles when needed
        articleLoader?.let {
            if (!loading && position >= articles.size -2) {
                loading = true

                GlobalScope.launch {
                    articleLoader.loadMore()
                    loading = false
                }
            }
        }
    }

    fun addAll(articles: List<Article>) {
        this.articles.addAll(articles)
        notifyItemInserted(this.articles.size - 1)
    }

    fun add(article: Article) {
        this.articles.add(article)
        notifyItemInserted(this.articles.size - 1)
    }

    fun clear() {
        val articleSize = this.articles.size
        this.articles.clear()
        notifyItemRemoved(articleSize - 1)
    }

    class ViewHolder(
        layout: LinearLayout,
        val feed: TextView,
        val title: TextView,
        val summary: TextView,
    ) : RecyclerView.ViewHolder(layout)
}