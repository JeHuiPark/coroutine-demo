package com.example.rssreader.feed

import com.example.rssreader.feed.model.Article
import com.example.rssreader.feed.model.Feed
import kotlinx.coroutines.*
import org.w3c.dom.Element
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory

@OptIn(DelicateCoroutinesApi::class)
class ArticleService {

    private val factory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
    private val feeds = listOf(
        Feed("trigger-exception", "illegal://illegal.url"),
        Feed("npr", "https://feeds.npr.org/1001/rss.xml"),
        Feed("fox", "https://feeds.foxnews.com/foxnews/politics?format=xml"),
    )
    val feedProviderCount: Int = feeds.size

    fun fetchAllArticlesAsync(dispatcher: CoroutineDispatcher): List<Deferred<List<Article>>> {
        return feeds.map { fetchArticleAsync(it, dispatcher) }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun fetchArticleAsync(
        feed: Feed,
        dispatcher: CoroutineDispatcher
    ) = GlobalScope.async(dispatcher) {
        val builder = factory.newDocumentBuilder()
        val xml = builder.parse(feed.url)
        val news = xml.getElementsByTagName("channel").item(0)
        return@async (0 until news.childNodes.length)
            .map { news.childNodes.item(it) }
            .filter { Node.ELEMENT_NODE == it.nodeType }
            .map { it as Element }
            .filter { "item" == it.tagName }
            .map {
                val title = it.getElementsByTagName("title").item(0).textContent
                val summary = it.getElementsByTagName("description").item(0).textContent
                Article(
                    feed = feed.name,
                    title = title,
                    summary = summary
                )
            }
    }
}