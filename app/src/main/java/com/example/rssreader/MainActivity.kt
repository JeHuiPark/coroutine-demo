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

    private val dispatcher: CoroutineDispatcher = newSingleThreadContext(name = "ServiceCall")
    private val factory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GlobalScope.launch(dispatcher) {
            val headlines = fetchRssHeadlines()
            val newsCount = findViewById<TextView>(R.id.newsCount)
            GlobalScope.launch(Dispatchers.Main) {
                newsCount.text = "Found ${headlines.size} News"
            }
        }
    }

    private fun fetchRssHeadlines(): List<String> {
        val builder = factory.newDocumentBuilder()
        val xml = builder.parse("https://feeds.npr.org/1001/rss.xml")
        val news = xml.getElementsByTagName("channel").item(0)
        return (0 until news.childNodes.length)
            .map { news.childNodes.item(it) }
            .filter { Node.ELEMENT_NODE == it.nodeType }
            .map { it as Element }
            .filter { "item" == it.tagName }
            .map { it.getElementsByTagName("title").item(0).textContent }
    }
}
