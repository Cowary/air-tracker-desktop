package org.cowary

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.openapitools.client.apis.*
import org.openapitools.client.models.*


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
    private val tvApi = TvControllerApi("http://localhost:8080", client)
    private val gameApi = GameControllerApi("http://localhost:8080", client)
    private val bookApi = BookControllerApi("http://localhost:8080", client)
    private val ranobeApi = RanobeControllerApi("http://localhost:8080", client)
    private val mangaApi = MangaControllerApi("http://localhost:8080", client)

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

    suspend fun fetchTv(text: String): List<Finds> {
        return try {
            val response = tvApi.find(text)
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

    suspend fun getTv(text: Int): TvRs {
        return try {
            val response = tvApi.getByIntegrationID(text)
            val rs = response.response.body<TvRs>() as TvRs
            rs
        } catch (e: Exception) {
            println("Error: ${e.message}")
        } as TvRs
    }

    suspend fun getRanobe(text: Int): RanobeRs {
        return try {
            val response = ranobeApi.getByIntegrationID1(text)
            val rs = response.response.body<RanobeRs>() as RanobeRs
            rs
        } catch (e: Exception) {
            println("Error: ${e.message}")
        } as RanobeRs
    }

    suspend fun getManga(text: Int): MangaRs {
        return try {
            val response = mangaApi.getByIntegrationID3(text)
            val rs = response.response.body<MangaRs>() as MangaRs
            rs
        } catch (e: Exception) {
            println("Error: ${e.message}")
        } as MangaRs
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

    suspend fun saveTv(text: TvSeasonDtoRq): Boolean {
        return try {
            val response = tvApi.postTitle(text)
            val rs = response.response.body() as TvDtoRs
            true
        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
            "Ошибка подключения"
        } as Boolean
    }

    suspend fun saveRanobe(text: RanobeVolumeDtoRq): Boolean {
        return try {
            val response = ranobeApi.postTitle1(text)
            val rs = response.response.body() as RanobeVolumeDtoRs
            true
        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
            "Ошибка подключения"
        } as Boolean
    }

    suspend fun saveManga(text: MangaDtoRq): Boolean {
        return try {
            val response = mangaApi.postTitle3(text)
            val rs = response.response.body() as MangaDtoRq
            true
        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
            "Ошибка подключения"
        } as Boolean
    }

    suspend fun saveGame(text: GameDtoRq): Boolean {
        return try {
            val response = gameApi.postTitle4(text)
            val rs = response.response.body() as GameDtoRs
            true
        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
            "Ошибка подключения"
        } as Boolean
    }

//    suspend fun saveBook(text: Book): Boolean {
//        return try {
//            val response = ranobeApi.postTitle1(text)
//            val rs = response.response.body() as RanobeVolumeDtoRs
//            true
//        } catch (e: Exception) {
//            println("Ошибка: ${e.message}")
//            "Ошибка подключения"
//        } as Boolean
//    }
}