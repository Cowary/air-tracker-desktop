package org.cowary.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import org.cowary.ApiService
import org.openapitools.client.models.Finds

class SearchAnimeScreen(private val type: String) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val animeService = remember { ApiService() }

        var query by remember { mutableStateOf(TextFieldValue("")) }
        var results by remember { mutableStateOf<List<Finds>>(emptyList()) }
        var isLoading by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        val coroutineScope = rememberCoroutineScope()

        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Поиск аниме") },
                navigationIcon = {
                    IconButton(onClick = {
                        navigator.pop()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )


            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                TextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Введите название аниме") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (query.text.isNotBlank()) {
                            coroutineScope.launch {
                                isLoading = true
                                errorMessage = null
                                results = when (type) {
                                    "anime" -> animeService.fetchAnime(query.text)
                                    "film" -> animeService.fetchMovie(query.text)
                                    "tv" -> animeService.fetchTv(query.text)
                                    "manga" -> animeService.fetchManga(query.text)
                                    "ranobe" -> animeService.fetchRanobe(query.text)
                                    else -> throw IllegalArgumentException("Unknown type: $type")
                                }
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Поиск")
                }

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else if (errorMessage != null) {
                    Text(
                        text = "Ошибка: $errorMessage",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                } else if (results.isEmpty()) {
                    Text(
                        text = "Ничего не найдено",
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    MediaTable(results) { anime ->
                        when (type) {
                            "anime" -> navigator.push(EditAnimeScreen(anime))
                            "film" -> navigator.push(EditMovieScreen(anime))
                            "tv" -> navigator.push(EditTvScreen(anime))
                            "manga" -> navigator.push(EditMangaScreen(anime))
                            "ranobe" -> navigator.push(EditRanobeScreen(anime))
                            else -> throw IllegalArgumentException("Unknown type: $type")
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun MediaTable(
        results: List<Finds>,
        onEditClick: (Long) -> Unit
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                TableHeader()
            }

            items(results.size) { index ->
                val anime = results[index]
                TableRow(anime) { onEditClick(anime.integrationId?.toLong()!!) }

                if (index < results.lastIndex) {
                    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                }
            }
        }
    }

    @Composable
    private fun TableHeader() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            HeaderCell("NameEn", Modifier.weight(2f))
            HeaderCell("NameRu", Modifier.weight(3f))
            HeaderCell("Year", Modifier.weight(1f))
            HeaderCell("Score", Modifier.weight(1f))
            HeaderCell("Episodes", Modifier.weight(1f))
            HeaderCell("IntegrationId", Modifier.weight(1f))
            HeaderCell("Действие", Modifier.weight(1f))
        }

        HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.onSurface)
    }

    @Composable
    private fun HeaderCell(text: String, modifier: Modifier = Modifier) {
        Text(
            text = text,
            modifier = modifier.padding(4.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }

    @Composable
    private fun TableRow(
        anime: Finds,
        onEditClick: () -> Unit
    ) {
        val backgroundColor = if (anime.episodes?.rem(2) == 0) {
            MaterialTheme.colorScheme.surface
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .clickable(onClick = onEditClick)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TableCell(anime.nameEn.toString(), Modifier.weight(2f))
            TableCell(anime.nameRu.toString(), Modifier.weight(3f))
            TableCell(anime.year.toString(), Modifier.weight(1f))
            TableCell(anime.score.toString(), Modifier.weight(1f))
            TableCell(anime.episodes.toString(), Modifier.weight(1f))
            TableCell(anime.integrationId.toString(), Modifier.weight(1f))
            TableCell(
                modifier = Modifier.weight(1f),
                text = "Добавить кнопка",
                content = {
                    Button(
                        onClick = onEditClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Добавить")
                    }
                }
            )
        }
    }

    @Composable
    private fun TableCell(
        text: String,
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit = { Text(text) }
    ) {
        Box(modifier = modifier.padding(4.dp)) {
            content()
        }
    }
}