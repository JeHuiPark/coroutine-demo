package com.example.rssreader

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import org.w3c.dom.Element
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory

@SuppressLint("SetTextI18n")
@OptIn(DelicateCoroutinesApi::class)
class MainActivity : AppCompatActivity() {

    private val dispatcher: CoroutineDispatcher = newFixedThreadPoolContext(2, "IO")
    private val factory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
    private val feedProviders = listOf(
        "illegal://illegal.url",
        "https://feeds.npr.org/1001/rss.xml",
        "https://feeds.foxnews.com/foxnews/politics?format=xml",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadNews()
    }

    private fun loadNews() = GlobalScope.launch(dispatcher) {
        val headlineDeferredList = feedProviders.map { fetchRssHeadlinesAsync(it, dispatcher) }
        headlineDeferredList.joinAll()
        val headlines = headlineDeferredList.filter { !it.isCancelled }
            .flatMap { it.await() }
        val failed = headlineDeferredList.filter { it.isCancelled }.size

        val newsCount = findViewById<TextView>(R.id.newsCount)
        val warnings = findViewById<TextView>(R.id.warnings)
        val obtained = feedProviders.size - failed
        launch(Dispatchers.Main) {
            newsCount.text = "Found ${headlines.size} News " +
                    "in $obtained feeds"
            if (failed > 0) {
                warnings.text = "Failed to fetch $failed feeds"
            }
        }
    }

    private fun fetchRssHeadlinesAsync(
        feed: String,
        dispatcher: CoroutineDispatcher
    ) = GlobalScope.async(dispatcher) {
            fetchRssHeadlines(feed)
    }
    private fun fetchRssHeadlines(feed: String): List<String> {
        val builder = factory.newDocumentBuilder()
        val xml = builder.parse(feed)
        val news = xml.getElementsByTagName("channel").item(0)
        return (0 until news.childNodes.length)
            .map { news.childNodes.item(it) }
            .filter { Node.ELEMENT_NODE == it.nodeType }
            .map { it as Element }
            .filter { "item" == it.tagName }
            .map { it.getElementsByTagName("title").item(0).textContent }
    }
}
