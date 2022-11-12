package com.example.rssreader.feed

import kotlinx.coroutines.*
import org.w3c.dom.Element
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory

@OptIn(DelicateCoroutinesApi::class)
class FeedService {

    private val factory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
    private val feedProviders = listOf(
        "illegal://illegal.url",
        "https://feeds.npr.org/1001/rss.xml",
        "https://feeds.foxnews.com/foxnews/politics?format=xml",
    )
    val feedProviderCount: Int = feedProviders.size

    fun fetchAllRssHeadlinesAsync(dispatcher: CoroutineDispatcher): List<Deferred<List<String>>> {
        return feedProviders.map { fetchRssHeadlinesAsync(it, dispatcher) }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun fetchRssHeadlinesAsync(
        feed: String,
        dispatcher: CoroutineDispatcher
    ) = GlobalScope.async(dispatcher) {
        val builder = factory.newDocumentBuilder()
        val xml = builder.parse(feed)
        val news = xml.getElementsByTagName("channel").item(0)
        return@async (0 until news.childNodes.length)
            .map { news.childNodes.item(it) }
            .filter { Node.ELEMENT_NODE == it.nodeType }
            .map { it as Element }
            .filter { "item" == it.tagName }
            .map { it.getElementsByTagName("title").item(0).textContent }
    }
}