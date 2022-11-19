package com.example.rssreader

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rssreader.feed.ArticleProducerFactory
import com.example.rssreader.feed.adapter.ArticleAdapter
import com.example.rssreader.feed.adapter.ArticleLoader
import kotlinx.coroutines.*

@SuppressLint("SetTextI18n")
@OptIn(DelicateCoroutinesApi::class)
class MainActivity : AppCompatActivity(), ArticleLoader {

    private lateinit var viewManager: LinearLayoutManager
    private lateinit var viewAdapter: ArticleAdapter
    private lateinit var articles: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewManager = LinearLayoutManager(this)
        viewAdapter = ArticleAdapter(this)
        articles = findViewById<RecyclerView>(R.id.articles).apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

        GlobalScope.launch { loadMore() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun loadMore() {
        val producer = ArticleProducerFactory.getProducer()

        if (!producer.isClosedForReceive) {
            val articles = producer.receive()

            GlobalScope.launch(Dispatchers.Main) {
                findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                viewAdapter.addAll(articles)
            }
        }
    }
}
