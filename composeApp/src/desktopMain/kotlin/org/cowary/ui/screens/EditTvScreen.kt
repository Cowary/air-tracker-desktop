package org.cowary.ui.screens

import air_tracker_desktop.composeapp.generated.resources.Res
import air_tracker_desktop.composeapp.generated.resources.compose_multiplatform
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import org.cowary.ApiService
import org.jetbrains.compose.resources.painterResource
import org.openapitools.client.models.TvRs
import org.openapitools.client.models.TvSeasonDtoRq


class EditTvScreen(private val integrationId: Long) : AirScreen(), Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val service = remember { ApiService() }

        // Состояния для данных
        var movieRs by remember { mutableStateOf<TvRs?>(null) }
        var isLoading by remember { mutableStateOf(true) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var isSaving by remember { mutableStateOf(false) }
        var saveSuccess by remember { mutableStateOf(false) }

        // Поля без редактирования
        var id by remember { mutableStateOf<Int?>(null) }
        var usrId by remember { mutableStateOf<Int?>(null) }
//        var shikiId by remember { mutableStateOf<Int?>(null) }
        // Поля для редактирования
        var title by remember { mutableStateOf(TextFieldValue("")) }
        var originalTitle by remember { mutableStateOf(TextFieldValue("")) }
        var status by remember { mutableStateOf(TextFieldValue("")) }
        var score by remember { mutableStateOf(TextFieldValue("")) }
//        var duration by remember { mutableStateOf(TextFieldValue("")) }
//        var releaseDate by remember { mutableStateOf(TextFieldValue("")) }
        var endDate by remember { mutableStateOf(TextFieldValue("")) }
        var number by remember { mutableStateOf(TextFieldValue("")) }
//        var episodes by remember { mutableStateOf(TextFieldValue("")) }
//        var episodesEnd by remember { mutableStateOf(TextFieldValue("")) }
        var releaseYear by remember { mutableStateOf(TextFieldValue("")) }

        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(integrationId) {
            loadMovieData(service, integrationId, { movieRs = it }, { isLoading = false }, { errorMessage = it })
        }

        LaunchedEffect(movieRs) {
            movieRs?.media?.let { media ->
                title = TextFieldValue(media.title ?: "")
                originalTitle = TextFieldValue(media.originalTitle ?: "")
                status = TextFieldValue(media.status ?: "")
                score = TextFieldValue(media.score?.toString() ?: "")
//                endDate = TextFieldValue(media.endDate ?: "")
                number = TextFieldValue(media.number?.toString() ?: "")

//                episodes = TextFieldValue(media.episodes ?: "")
//                episodesEnd = TextFieldValue(media.episodesEnd ?: "")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TopAppBar(
                title = { Text("Редактирование сериала") },
                navigationIcon = {
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )

            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                errorMessage != null -> {
                    Text(
                        text = "Ошибка: $errorMessage",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            isLoading = true
                            errorMessage = null
                            coroutineScope.launch {
                                loadMovieData(
                                    service,
                                    integrationId,
                                    { movieRs = it },
                                    { isLoading = false },
                                    { errorMessage = it })
                            }
                        }
                    ) {
                        Text("Повторить")
                    }
                }

                // Основная форма
                else -> {
                    TvEditForm(
                        tvRs = movieRs,
                        originalTitle = originalTitle,
                        onOriginalTitleChange = { originalTitle = it },
                        title = title,
                        onTitleChange = { title = it },
                        status = status,
                        onStatusChange = { status = it },
                        score = score,
                        onScoreChange = { score = it },
//                        releaseDate = releaseDate,
//                        onReleaseDateChange = { releaseDate = it },
                        endDate = endDate,
                        onEndDateChange = { endDate = it },
                        number = number,
                        onNumberChange = { number = it },
//                        episodes = episodes,
//                        onEpisodeChange = { episodes = it },
//                        episodesEnd = episodesEnd,
//                        onEpisodeEndChange = { episodesEnd = it },
                        releaseYear = releaseYear,
                        onReleaseYearChange = { releaseYear = it },
                        isSaving = isSaving,
                        saveSuccess = saveSuccess,
                        onSaveClick = {
                            coroutineScope.launch {
                                isSaving = true
                                saveSuccess = false

                                val updatedMedia = TvSeasonDtoRq(
                                    title = title.text,
                                    status = status.text,
                                    score = score.text.toInt(),
                                    releaseYear = releaseYear.text.toInt(),
                                    originalTitle = originalTitle.text,
                                    endDate = endDate.text,
                                    integrationId = integrationId.toInt(),
                                    number = number.text.toInt(),
                                )

                                val success = service.saveTv(updatedMedia)
                                isSaving = false
                                saveSuccess = success

                                if (success) {
//                            navigator.popUntilRoot()
                                    // Можно показать уведомление об успехе
                                    // или автоматически вернуться назад через некоторое время
                                }
                            }
                        },
                        onBackClick = { navigator.pop() }
                    )
                }
            }
        }
    }

    suspend fun loadMovieData(
        service: ApiService,
        id: Long,
        onSuccess: (TvRs) -> Unit,
        onLoadingComplete: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val tvRs = service.getTv(id.toInt())
            onSuccess(tvRs)
        } catch (e: Exception) {
            onError(e.message ?: "Неизвестная ошибка")
        } finally {
            onLoadingComplete()
        }
    }

    @Composable
    private fun TvEditForm(
        tvRs: TvRs?,
        originalTitle: TextFieldValue,
        onOriginalTitleChange: (TextFieldValue) -> Unit,
        title: TextFieldValue,
        onTitleChange: (TextFieldValue) -> Unit,
        status: TextFieldValue,
        onStatusChange: (TextFieldValue) -> Unit,
        score: TextFieldValue,
        onScoreChange: (TextFieldValue) -> Unit,
//        releaseDate: TextFieldValue,
//        onReleaseDateChange: (TextFieldValue) -> Unit,
        releaseYear: TextFieldValue,
        onReleaseYearChange: (TextFieldValue) -> Unit,
        endDate: TextFieldValue,
        onEndDateChange: (TextFieldValue) -> Unit,
        number: TextFieldValue,
        onNumberChange: (TextFieldValue) -> Unit,
//        episodes: TextFieldValue,
//        onEpisodeChange: (TextFieldValue) -> Unit,
//        episodesEnd: TextFieldValue,
//        onEpisodeEndChange: (TextFieldValue) -> Unit,
        isSaving: Boolean,
        saveSuccess: Boolean,
        onSaveClick: () -> Unit,
        onBackClick: () -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                tvRs?.media?.title?.let { title ->
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text("Название") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = originalTitle,
                    onValueChange = onOriginalTitleChange,
                    label = { Text("Оригинальное название") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))

                IntegerOnlyTextField(
                    label = "Номер сезона",
                    value = number.text,
                    onValueChange = onNumberChange,
                    modifier = Modifier.fillMaxWidth(),
                    maxValue = 100
                )
                Spacer(modifier = Modifier.height(6.dp))

                StatusDropdown(
                    status = status.text,
                    onStatusChange = onStatusChange,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))

                IntegerOnlyTextField(
                    label = "Оценка",
                    value = score.text,
                    onValueChange = onScoreChange,
                    modifier = Modifier.fillMaxWidth(),
                    maxValue = 10
                )
                Spacer(modifier = Modifier.height(6.dp))

                IntegerOnlyTextField(
                    label = "Год выхода",
                    value = releaseYear.text,
                    onValueChange = onReleaseYearChange,
                    modifier = Modifier.fillMaxWidth(),
                    maxValue = 10000
                )
                Spacer(modifier = Modifier.height(6.dp))

//                IntegerOnlyTextField(
//                    label = "Кол-во эпизодов",
//                    value = episodes.text,
//                    onValueChange = onEpisodeChange,
//                    modifier = Modifier.fillMaxWidth(),
//                    maxValue = 10000
//                )
//                Spacer(modifier = Modifier.height(6.dp))
//
//                IntegerOnlyTextField(
//                    label = "Кол-во эпизодов просмотрено",
//                    value = episodesEnd.text,
//                    onValueChange = onEpisodeEndChange,
//                    modifier = Modifier.fillMaxWidth(),
//                    maxValue = 10000
//                )
//                Spacer(modifier = Modifier.height(6.dp))

                DateSelectionField(
                    label = "Дата окончания просмотра:",
                    selectedDate = endDate,
                    onDateSelected = onEndDateChange
                )
                Spacer(modifier = Modifier.height(6.dp))

                if (saveSuccess) {
                    Text(
                        text = "Данные успешно сохранены!",
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onBackClick,
                        enabled = !isSaving,
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Text("Назад")
                    }

                    Button(
                        onClick = onSaveClick,
                        enabled = !isSaving,
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    ) {
                        if (isSaving) {
                            Text("Сохранение...")
                        } else {
                            Text("Сохранить")
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                tvRs?.posterUrl?.let { posterUrl ->
                    if (posterUrl.isNotEmpty()) {
                        println("Получен URL постера: $posterUrl")
                        println("Попытка загрузить изображение...")

                        AsyncImage(
                            model = tvRs.posterUrl,
                            contentDescription = "Постер сериала",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                                .align(Alignment.CenterHorizontally),
                            error = painterResource(Res.drawable.compose_multiplatform).also {
                                println("Отображено изображение ошибки")
                            },
                            placeholder = painterResource(Res.drawable.compose_multiplatform).also {
                                println("Отображено заполнительное изображение")
                            },
                            onSuccess = {
                                println("Изображение успешно загружено:")
                            },
                            onError = { exception ->
                                println("Ошибка загрузки изображения: $exception")
                                exception.toString()
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    } else {
                        println("URL пустой или null")
                    }
                }
            }
        }
    }
}