package com.example.rssreader

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rssreader.feed.FeedService
import com.example.rssreader.feed.FeedServiceFactory
import com.example.rssreader.feed.adapter.ArticleAdapter
import kotlinx.coroutines.*

@SuppressLint("SetTextI18n")
@OptIn(DelicateCoroutinesApi::class)
class MainActivity : AppCompatActivity() {

    private val dispatcher: CoroutineDispatcher = newFixedThreadPoolContext(2, "IO")
    private val feedService: FeedService = FeedServiceFactory.newInstance()

    private lateinit var viewManager: LinearLayoutManager
    private lateinit var viewAdapter: ArticleAdapter
    private lateinit var articles: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewManager = LinearLayoutManager(this)
        viewAdapter = ArticleAdapter()
        articles = findViewById<RecyclerView>(R.id.articles).apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
        loadNews()
    }

    private fun loadNews() =  GlobalScope.launch(dispatcher) {
        val articleDeferredList = feedService.fetchAllArticlesAsync(dispatcher)
        articleDeferredList.joinAll()
        val articles = articleDeferredList.filter { !it.isCancelled }
            .flatMap { it.await() }
        launch(Dispatchers.Main) {
            findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
            viewAdapter.addAll(articles)
        }
    }
}
