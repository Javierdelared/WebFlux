package com.example.webFlux.controller

import com.example.webFlux.dto.User
import com.example.webFlux.repository.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class UserController(val userRepository: UserRepository) {

    private val logger = KotlinLogging.logger {}

    private var counter = 0L

    @PostMapping("/users")
    @ResponseBody
    @Transactional
    suspend fun saveUser(@RequestBody user: User): User { // 4s
        return runBlocking {
            user.id = counter++
            val savedUser = async {
                logger.info { "Saving user" }
                delay(2000)
                userRepository.save(user)
            }
            val foundUser = async {
                logger.info { "Finding known user" }
                delay(5000)
                val users = userRepository.findAllByName(user.name)
                if (users.isEmpty()) {
                    getTestUser()
                } else {
                    users[0]
                }
            }
            if (0L == savedUser.await().id || 0L == foundUser.await().id) {
                logger.info { "First!" }
                // It logs after 2 seconds, when the coroutine `savedUser` have finished if it's the first user in the database
                // It logs after 5 seconds, when the coroutine `foundUser` have finished if it has the same name as the first user in the database
            }
            savedUser.await()// The response is only sent after all the coroutines have finished
        }
    }

    @GetMapping("/users/test")
    @ResponseBody
    @Transactional
    suspend fun getTestUser(): User { // 4s
        return runBlocking {
            // Using async the correct way, with suspend functions
            // It uses a single thread, but the thread jumps from one function to the other when it's suspended
            val name = async { getName() }
            val mail = async { getMail() }
            logger.info { "Sending response" }
            User(1, name.await(), mail.await())
        }
    }

    private suspend fun getName(): String {
        logger.info { "Finding name" }
        delay(2000) // We can use the suspend function `delay` because the parent functions are also suspend functions
        return "name1"
    }

    private suspend fun getMail(): String {
        logger.info { "Finding mail" }
        delay(2000)
        return "mail1"
    }

}