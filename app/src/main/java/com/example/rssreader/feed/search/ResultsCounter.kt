package com.example.rssreader.feed.search

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.newSingleThreadContext

@OptIn(DelicateCoroutinesApi::class, ObsoleteCoroutinesApi::class)
object ResultsCounter {

    private val context = newSingleThreadContext("counter")
    private var value = 0
    private val notificationChannel = Channel<Int>(Channel.CONFLATED)
    val notificationReceiver: ReceiveChannel<Int>
        get() = notificationChannel
    private val actor = CoroutineScope(context).actor<Action>(context) {
        for (msg in channel) {
            when (msg) {
                Action.RESET -> value = 0
                Action.INCREASE -> value++
            }
            notificationChannel.send(value)
        }
    }

    suspend fun increment() = actor.send(Action.INCREASE)

    suspend fun reset() = actor.send(Action.RESET)

    enum class Action {
        RESET,
        INCREASE
    }
}