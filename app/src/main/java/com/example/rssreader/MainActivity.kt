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
    }
}
