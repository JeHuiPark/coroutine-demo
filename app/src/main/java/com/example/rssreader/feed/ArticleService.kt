package com.example.rssreader.feed

import com.example.rssreader.feed.model.Article
import com.example.rssreader.feed.model.Feed
import org.w3c.dom.Element
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory

class ArticleService {

    private val factory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()

    fun fetchArticlesByFeed(
        feed: Feed,
    ): List<Article> {
        val builder = factory.newDocumentBuilder()
        val xml = builder.parse(feed.url)
        val news = xml.getElementsByTagName("channel").item(0)
        return (0 until news.childNodes.length)
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