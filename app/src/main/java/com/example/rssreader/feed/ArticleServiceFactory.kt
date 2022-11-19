package com.example.rssreader.feed

object ArticleServiceFactory {

    fun newInstance(): ArticleService {
        return ArticleService()
    }
}