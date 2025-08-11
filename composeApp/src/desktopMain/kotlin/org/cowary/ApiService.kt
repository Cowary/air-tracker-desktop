package org.cowary

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLProtocol
import io.ktor.http.isSuccess
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.openapitools.client.apis.MediaListControllerApi
import org.openapitools.client.infrastructure.ApiClient
import io.ktor.client.plugins.logging.*
import io.ktor.http.hostIsIp
import org.openapitools.client.apis.AnimeControllerApi
import org.openapitools.client.apis.MovieControllerApi
import org.openapitools.client.models.AnimeDtoRq
import org.openapitools.client.models.AnimeDtoRs
import org.openapitools.client.models.AnimeRs
import org.openapitools.client.models.FindMediaRs
import org.openapitools.client.models.Finds
import org.openapitools.client.models.MediaDtoRs
import org.openapitools.client.models.MovieDtoRq
import org.openapitools.client.models.MovieDtoRs
import org.openapitools.client.models.MovieRs
import kotlin.text.get


class ApiService {
    private val client: HttpClient = HttpClient {
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
            install(HttpTimeout) {
            requestTimeoutMillis = 600000
        }
    }


    private val usersApi = MediaListControllerApi("http://localhost:8080", client)
    private val animeApi = AnimeControllerApi("http://localhost:8080", client)

    private val movieApi = MovieControllerApi("http://localhost:8080", client)

    suspend fun fetchData(): String {
//        val result = client.get {
//            url {
//                protocol = URLProtocol.HTTPS
//                host = "dummyjson.com"
//                path("test")
//            }
//        }
        return try {
            val response = usersApi.getMediaList(3)
            response.response.body()
        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
            "Ошибка подключения"
        }
    }

    suspend fun fetchAll(): List<MediaDtoRs> {
        return try {
            val response = usersApi.getMediaList(3)
            response.response.body() as List<MediaDtoRs>
        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
            "Ошибка подключения"
        } as List<MediaDtoRs>
    }

    suspend fun fetchAnime(text: String): List<Finds> {
        return try {
            val response = animeApi.find4(text)
            val rs = response.response.body() as FindMediaRs
            rs.findMedia
        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
            "Ошибка подключения"
        } as List<Finds>
    }

    suspend fun fetchMovie(text: String): List<Finds> {
        return try {
            val response = movieApi.find2(text)
            val rs = response.response.body() as FindMediaRs
            rs.findMedia
        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
            "Ошибка подключения"
        } as List<Finds>
    }

    suspend fun getAnime(text: Int): AnimeRs {
        return try {
            val response = animeApi.getByIntegrationID4(text)
            val rs = response.response.body() as AnimeRs
            rs
        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
            "Ошибка подключения"
        } as AnimeRs
    }

    suspend fun getMovie(text: Int): MovieRs {
        return try {
            val response = movieApi.getByIntegrationID2(text)
            val rs = response.response.body() as MovieRs
            rs
        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
            "Ошибка подключения"
        } as MovieRs
    }

    suspend fun saveAnime(text: AnimeDtoRq): Boolean {
        return try {
            val response = animeApi.postTitle6(text)
            val rs = response.response.body() as AnimeDtoRs
            true
        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
            "Ошибка подключения"
        } as Boolean
    }

    suspend fun saveMovie(text: MovieDtoRq): Boolean {
        return try {
            val response = movieApi.postTitle2(text)
            val rs = response.response.body() as MovieDtoRs
            true
        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
            "Ошибка подключения"
        } as Boolean
    }
}