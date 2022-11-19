package com.example.rssreader.feed

import com.example.rssreader.feed.model.Article
import com.example.rssreader.feed.model.Feed
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce

@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
object ArticleProducerFactory {

    private val dispatcher: CoroutineDispatcher = newFixedThreadPoolContext(2, "IO")
    private val articleService: ArticleService = ArticleServiceFactory.newInstance()
    private val feeds = listOf(
        Feed("trigger-exception", "illegal://illegal.url"),
        Feed("npr", "https://feeds.npr.org/1001/rss.xml"),
        Feed("fox", "https://feeds.foxnews.com/foxnews/politics?format=xml"),
    )

    private val producer = GlobalScope.produce(dispatcher) {
        feeds.forEach {
            runCatching { send(articleService.fetchArticlesByFeed(it)) }
        }
    }

    fun getProducer(): ReceiveChannel<List<Article>> = producer
}