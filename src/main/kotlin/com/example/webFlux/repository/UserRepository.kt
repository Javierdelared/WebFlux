package com.example.webFlux.repository

import com.example.webFlux.dto.User
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserRepository : CoroutineCrudRepository<User, Long> {

    suspend fun findAllByName(name: String?): List<User>
}