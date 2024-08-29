package com.example.webFlux.controller

import com.example.webFlux.dto.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class BadUserController {

    @GetMapping("/bad-users/test1")
    @ResponseBody
    @Transactional
    fun getUser1(): User { // 4s
        // Classic sequential programing
        val name = getName()
        val mail = getMail()
        println("Sending response")
        return User(1, name, mail)
    }

    @GetMapping("/bad-users/test2")
    @ResponseBody
    @Transactional
    fun getUser2(): User { // 4s
        return runBlocking {
            // Using async wrong
            // It only uses one thread and the methods we call are blocking
            val name = async { getName() }
            val mail = async { getMail() }
            println("Sending response")
            User(1, name.await(), mail.await())
        }
    }

    @GetMapping("/bad-users/test3")
    @ResponseBody
    @Transactional
    fun getUser3(): User { // 2s
        return runBlocking(Dispatchers.IO) {
            // Using async a little better
            // It uses multiple threads in parallel, but if there are too many requests, the thread pool might exhaust
            // It can produce problems if there are concurrent calls to the database
            val name = async { getName() }
            val mail = async { getMail() }
            println("Sending response")
            User(1, name.await(), mail.await())
        }
    }

    private fun getName(): String {
        Thread.sleep(2000)
        println("name found")
        return "name1"
    }

    private fun getMail(): String {
        Thread.sleep(2000)
        println("mail found")
        return "mail1"
    }

}