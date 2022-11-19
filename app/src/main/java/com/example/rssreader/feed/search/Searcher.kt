package com.example.rssreader.feed.search

import com.example.rssreader.feed.ArticleServiceFactory
import com.example.rssreader.feed.model.Article
import com.example.rssreader.feed.model.Feed
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext

@OptIn(DelicateCoroutinesApi::class)
class Searcher {

    private val dispatcher = newFixedThreadPoolContext(3, "IO-Search")
    private val articleService = ArticleServiceFactory.newInstance()
    private val feeds = listOf(
        Feed("trigger-exception", "illegal://illegal.url"),
        Feed("npr", "https://feeds.npr.org/1001/rss.xml"),
        Feed("fox", "https://feeds.foxnews.com/foxnews/politics?format=xml"),
    )

    fun search(query: String): ReceiveChannel<Article> {
        val channel = Channel<Article>(Channel.BUFFERED)
        feeds.forEach { feed ->
            GlobalScope.launch(dispatcher) {
                search(feed, channel, query)
            }
        }
        return channel
    }

    private suspend fun search(
        feed: Feed,
        channel: SendChannel<Article>,
        query: String
    ) {
        runCatching { articleService.fetchArticlesByFeed(feed) }
            .onSuccess {
                it.forEach { article ->
                    if (article.title.contains(query) || article.summary.contains(query)) {
                        channel.send(article)
                    }
                }
            }
    }
}

suspend fun main() {
    val searcher = Searcher()

    val receiver = searcher.search("test")
    val received = receiver.receive()

    println(received)
}