package com.example.webFlux

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.lang.Thread.sleep
import java.time.Instant
import kotlin.concurrent.thread
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Ignore

class CoroutinesTest {

    @Test
    @Ignore
    fun manyThreads() {
        val start = Instant.now().toEpochMilli()
        (1..100_000).map {
            thread {
                sleep(2000) // This functions blocks the thread
                println(it)
            }
        }
        val millis = Instant.now().toEpochMilli() - start
        println(String.format("Threads ready in [%d] milliseconds", millis))
    }

    @Test
    fun manyCoroutines() {
        val start = Instant.now().toEpochMilli()
        runBlocking(Dispatchers.IO) {
            // The parent coroutine scope makes sure that the child scopes are completed before proceeding
            (1..100_000).map {
                launch {
                    delay(2000) // This is a suspend function, it can be paused and resumed at a later time
                    println(it)
                }
            }
        }
        val millis = Instant.now().toEpochMilli() - start
        println(String.format("Coroutines ready in [%d] milliseconds", millis))
    }

    @Test
    fun async() {
        val start = Instant.now().toEpochMilli()
        runBlocking(Dispatchers.IO) {
            val asyncResult = async {
                delay(2000)
                "."
            }
            println(asyncResult.await())
        }
        val millis = Instant.now().toEpochMilli() - start
        println(String.format("Async function executed in [%d] milliseconds", millis))
    }

    @Test
    fun emptyContext() {
        val start = Instant.now().toEpochMilli()
        runBlocking(context = EmptyCoroutineContext) {
            launch {// The coroutine context is inherited by default
                delay(2000)
                println(Thread.currentThread().name)
            }
            launch {
                delay(2000)
                println(Thread.currentThread().name)
            }
        }
        val millis = Instant.now().toEpochMilli() - start
        println(String.format("Delay function executed in [%d] milliseconds", millis))
    }

    @Test
    fun dispatchersContext() {
        val start = Instant.now().toEpochMilli()
        runBlocking(Dispatchers.IO) {
            // The Dispatchers coroutine context allows to execute blocking functions in parallel
            launch {
                sleep(2000) // Even though the sleep method is blocking, it's executed in parallel
                println(Thread.currentThread().name)
            }
            launch {
                sleep(2000)
                println(Thread.currentThread().name)
            }
        }
        val millis = Instant.now().toEpochMilli() - start
        println(String.format("Sleep function executed in [%d] milliseconds", millis))
    }
}