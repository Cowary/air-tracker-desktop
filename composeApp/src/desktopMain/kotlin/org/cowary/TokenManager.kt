package org.cowary

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object TokenManager {
    private var token: String? = null
    private var username = "ruderu";
    private var password = "123";

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    println("[Ktor] $message")
                }
            }
        }
    }

    suspend fun getToken(username: String, password: String): String {
        if (token == null) {
            token = fetchToken(username, password)
        }
        return token!!
    }

    suspend fun getToken(): String {
        if (token == null) {
            token = fetchToken(username, password)
        }
        return token!!
    }

    private suspend fun fetchToken(username: String, password: String): String {
        val response: TokenResponse = client.post("http://localhost:8080/api/auth/sign-in") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("username" to username, "password" to password))
        }.body()
        return response.token
    }

    @Serializable
    data class TokenResponse(val token: String)

    fun clearToken() {
        token = null
    }
}