package com.example.rssreader.feed.adapter

interface ArticleLoader {

    suspend fun loadMore()
}