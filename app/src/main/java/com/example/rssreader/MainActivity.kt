package com.example.rssreader

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.rssreader.feed.FeedService
import com.example.rssreader.feed.FeedServiceFactory
import kotlinx.coroutines.*

@SuppressLint("SetTextI18n")
@OptIn(DelicateCoroutinesApi::class)
class MainActivity : AppCompatActivity() {

    private val dispatcher: CoroutineDispatcher = newFixedThreadPoolContext(2, "IO")
    private val feedService: FeedService = FeedServiceFactory.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadNews()
    }

    private fun loadNews() = GlobalScope.launch(dispatcher) {
        val headlineDeferredList = feedService.fetchAllRssHeadlinesAsync(dispatcher)
        headlineDeferredList.joinAll()
        val headlines = headlineDeferredList.filter { !it.isCancelled }
            .flatMap { it.await() }
        val failed = headlineDeferredList.filter { it.isCancelled }.size

        val newsCount = findViewById<TextView>(R.id.newsCount)
        val warnings = findViewById<TextView>(R.id.warnings)
        val obtained = feedService.feedProviderCount - failed
        launch(Dispatchers.Main) {
            newsCount.text = "Found ${headlines.size} News " +
                    "in $obtained feeds"
            if (failed > 0) {
                warnings.text = "Failed to fetch $failed feeds"
            }
        }
    }
}
