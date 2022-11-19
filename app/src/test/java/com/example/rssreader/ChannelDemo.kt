@file:OptIn(DelicateCoroutinesApi::class)

package com.example.rssreader

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

suspend fun main() {
    println("start RENDEZVOUS channel demo")
    executeUnbufferedChannelDemo()
    println("end RENDEZVOUS channel demo")

    println("==============================================")

    println("start UNLIMITED channel demo")
    executeUnlimitedChannelDemo()
    println("end UNLIMITED channel demo")

    println("==============================================")

    println("start BUFFERED channel demo")
    executeBufferedChannelDemo()
    println("end BUFFERED channel demo")

    println("==============================================")

    println("start CONFLATED channel demo")
    executeConflatedChannelDemo()
    println("end CONFLATED channel demo")
}

suspend fun executeUnbufferedChannelDemo() {
    // RENDEZVOUS Channel
    val channel = Channel<Int>()
    val job = GlobalScope.launch {
        repeat(10) {
            // 리시버가 데이터를 처리하기전까지 일시 중단된다
            channel.send(it)
            println("sent $it")
        }
    }
    channel.receive()
    channel.receive()
    job.cancelAndJoin()
}

suspend fun executeUnlimitedChannelDemo() {
    val channel = Channel<Int>(Channel.UNLIMITED)
    val job = GlobalScope.launch {
        repeat(5) {
            // 리시버 처리 속도와 무관하게 무제한으로 전송한다 (중단되지 않는다)
            channel.send(it)
            println("sent $it")
        }
    }
    job.join()
    println("received ${channel.receive()}")
}

suspend fun executeBufferedChannelDemo() {
    val channel = Channel<Int>(4)
    val job = GlobalScope.launch {
        repeat(5) {
            // 버퍼가 가득 차면 일시 중단된다
            channel.send(it)
            println("sent $it")
        }
    }
    delay(500)
    println("taking one")
    channel.receive()
    job.join()
}

suspend fun executeConflatedChannelDemo() {
    // channel receiver가 buffer를 처리하기전에 channel로 데이터를 전송하면 buffer를 덮어쓰기 하여 데이터가 유실될 수 있음
    val channel = Channel<Int>(Channel.CONFLATED)
    GlobalScope.launch {
        repeat(5) {
            // 중단되지 않는다
            channel.send(it)
            println("sent $it")
        }
    }
    delay(500)
    val ele = channel.receive()
    println("received $$ele")
}
