package org.cowary.screen

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
import org.openapitools.client.models.MangaDtoRq
import org.openapitools.client.models.RanobeRs
import org.openapitools.client.models.RanobeVolumeDtoRq

class EditRanobeScreen(private val integrationId: Long) : AirScreen(), Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val animeService = remember { ApiService() }

        // Состояния для данных
        var ranobeRs by remember { mutableStateOf<RanobeRs?>(null) }
        var animeRq by remember { mutableStateOf<MangaDtoRq?>(null) }
        var isLoading by remember { mutableStateOf(true) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var isSaving by remember { mutableStateOf(false) }
        var saveSuccess by remember { mutableStateOf(false) }

        // Поля без редактирования
        var id by remember { mutableStateOf<Int?>(null) }
        var usrId by remember { mutableStateOf<Int?>(3) }
//        var shikiId by remember { mutableStateOf<Int?>(null) }
        // Поля для редактирования
        var title by remember { mutableStateOf(TextFieldValue("")) }
        var originalTitle by remember { mutableStateOf(TextFieldValue("")) }
        var status by remember { mutableStateOf(TextFieldValue("")) }
        var score by remember { mutableStateOf(TextFieldValue("")) }
        var releaseDate by remember { mutableStateOf(TextFieldValue("")) }
        var endDate by remember { mutableStateOf(TextFieldValue("")) }
        var number by remember { mutableStateOf(TextFieldValue("")) }

        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(integrationId) {
            loadRanobeData(animeService, integrationId, { ranobeRs = it }, { isLoading = false }, { errorMessage = it })
        }

        LaunchedEffect(ranobeRs) {
            ranobeRs?.media?.let { media ->
                title = TextFieldValue(media.title ?: "")
                originalTitle = TextFieldValue(media.originalTitle ?: "")
                status = TextFieldValue(media.status?: "")
                score = TextFieldValue(media.score?.toString() ?: "")
                releaseDate = TextFieldValue(media.releaseDate ?: "")
                endDate = TextFieldValue(media.endDate?: "")
                number = TextFieldValue(media.number?.toString() ?: "")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TopAppBar(
                title = { Text("Редактирование ранобе") },
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
                                loadRanobeData(
                                    animeService,
                                    integrationId,
                                    { ranobeRs = it },
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
                    RanobeEditForm(
                        ranobeRs = ranobeRs,
                        originalTitle = originalTitle,
                        onOriginalTitleChange = { originalTitle = it },
                        title = title,
                        onTitleChange = { title = it },
                        status = status,
                        onStatusChange = { status = it },
                        score = score,
                        onScoreChange = { score = it },
                        releaseDate = releaseDate,
                        onReleaseDateChange = { releaseDate = it },
                        endDate = endDate,
                        onEndDateChange = { endDate = it },
                        number = number,
                        onNumberChange = { number = it },
                        isSaving = isSaving,
                        saveSuccess = saveSuccess,
                        onSaveClick = {
                            coroutineScope.launch {
                                isSaving = true
                                saveSuccess = false

                                val updatedMedia = RanobeVolumeDtoRq(
                                    originalTitle = originalTitle.text,
                                    title = title.text,
                                    status = status.text,
                                    releaseDate = releaseDate.text,
                                    integrationId = integrationId.toInt(),
                                    usrId = usrId?.toLong()
                                        ?: throw IllegalStateException("User ID is missing"),
                                    score = score.text.toIntOrNull(),
                                    endDate = endDate.text,
                                    number = number.text.toInt(),
                                )

                                val success = animeService.saveRanobe(updatedMedia)
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

    // Функция загрузки данных
    suspend fun loadRanobeData(
        service: ApiService,
        id: Long,
        onSuccess: (RanobeRs) -> Unit,
        onLoadingComplete: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val ranobeRs = service.getRanobe(id.toInt())
            onSuccess(ranobeRs)
        } catch (e: Exception) {
            onError(e.message ?: "Неизвестная ошибка")
        } finally {
            onLoadingComplete()
        }
    }

    @Composable
    private fun RanobeEditForm(
        ranobeRs: RanobeRs?,
        originalTitle: TextFieldValue,
        onOriginalTitleChange: (TextFieldValue) -> Unit,
        title: TextFieldValue,
        onTitleChange: (TextFieldValue) -> Unit,
        status: TextFieldValue,
        onStatusChange: (TextFieldValue) -> Unit,
        score: TextFieldValue,
        onScoreChange: (TextFieldValue) -> Unit,
        releaseDate: TextFieldValue,
        onReleaseDateChange: (TextFieldValue) -> Unit,
        endDate: TextFieldValue,
        onEndDateChange: (TextFieldValue) -> Unit,
        number: TextFieldValue,
        onNumberChange: (TextFieldValue) -> Unit,
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

                ranobeRs?.media?.title?.let { title ->
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
                    label = "Номер тома",
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

                DateSelectionField(
                    label = "Дата выхода",
                    selectedDate = releaseDate,
                    onDateSelected = onReleaseDateChange,
                )
                Spacer(modifier = Modifier.height(6.dp))

                DateSelectionField(
                    label = "Дата окончания прочтения:",
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
                ranobeRs?.posterUrl?.let { posterUrl ->
                    if (posterUrl.isNotEmpty()) {
                        println("Получен URL постера: $posterUrl")
                        println("Попытка загрузить изображение...")

                        AsyncImage(
                            model = ranobeRs.posterUrl,
                            contentDescription = "Постер аниме",
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