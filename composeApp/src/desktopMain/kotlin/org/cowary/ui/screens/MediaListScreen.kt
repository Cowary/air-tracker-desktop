package org.cowary.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import org.cowary.ApiService
import org.openapitools.client.models.MediaDtoRs

class MediaListScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        // Получаем навигатор
        val navigator = LocalNavigator.currentOrThrow

        // Создаем сервис
        val mediaService = remember { ApiService() }

        // Состояния для данных
        var mediaList by remember { mutableStateOf<List<MediaDtoRs>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        // Scope для корутин
        val coroutineScope = rememberCoroutineScope()

        // Загружаем данные при первом отображении экрана
        LaunchedEffect(Unit) {
            loadMediaData(mediaService, { mediaList = it }, { isLoading = false }, { errorMessage = it })
        }

        // Основной контейнер
        Column(modifier = Modifier.fillMaxSize()) {
            // Верхняя панель
            TopAppBar(
                title = { Text("Список медиа") },
                navigationIcon = {
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )

            // Содержимое
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                when {
                    // Показываем индикатор загрузки
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    // Показываем ошибку
                    errorMessage != null -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
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
                                        loadMediaData(mediaService, { mediaList = it }, { isLoading = false }, { errorMessage = it })
                                    }
                                }
                            ) {
                                Text("Повторить")
                            }
                        }
                    }

                    // Показываем таблицу
                    else -> {
                        MediaTableWithLazyScrollbar(mediaList = mediaList)
                    }
                }
            }
        }
    }

    // Альтернативный вариант с LazyColumn и прокруткой
    @Composable
    private fun MediaTableWithLazyScrollbar(mediaList: List<MediaDtoRs>) {
        // State для LazyColumn
        val lazyListState = rememberLazyListState()

        Box(modifier = Modifier.fillMaxSize()) {
            // LazyColumn для таблицы
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize()
            ) {
                // Заголовок таблицы
                item {
                    MediaTableHeader()
                }

                // Строки таблицы
                items(mediaList.size) { index ->
                    val media = mediaList[index]
                    MediaTableRow(media = media, index = index)

                    // Разделитель между строками
                    if (index < mediaList.size - 1) {
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                    }
                }

                // Отступ внизу
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Полоса прокрутки для LazyColumn
            VerticalScrollbar(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight(),
                adapter = rememberScrollbarAdapter(lazyListState)
            )
        }
    }

    // Функция загрузки данных
    private suspend fun loadMediaData(
        service: ApiService,
        onSuccess: (List<MediaDtoRs>) -> Unit,
        onLoadingComplete: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            // Попробуем загрузить реальные данные
            val media = service.fetchAll()
            onSuccess(media)
        } catch (e: Exception) {
            // Если ошибка - используем тестовые данные
            println("Используем тестовые данные: ${e.message}")
//            onSuccess(service.getMockMediaList())
        } finally {
            onLoadingComplete()
        }
    }

    // Компонент таблицы
    @Composable
    private fun MediaTableWithScrollbar(mediaList: List<MediaDtoRs>) {
        // State для отслеживания состояния прокрутки
        val scrollState = rememberScrollState()

        // Box для размещения таблицы и полосы прокрутки
        Box(modifier = Modifier.fillMaxSize()) {
            // Основная таблица с вертикальной прокруткой
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState) // Добавляем вертикальную прокрутку
            ) {
                // Заголовок таблицы
                MediaTableHeader()

                // Список строк
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    mediaList.forEachIndexed { index, media ->
                        MediaTableRow(media = media, index = index)

                        // Разделитель между строками
                        if (index < mediaList.size - 1) {
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                            )
                        }
                    }
                }

                // Добавляем отступ внизу для лучшей прокрутки
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Вертикальная полоса прокрутки
            VerticalScrollbar(
                modifier = Modifier
                    .align(Alignment.CenterEnd) // Выравниваем по правому краю
                    .fillMaxHeight(), // Заполняем всю высоту
                adapter = rememberScrollbarAdapter(scrollState)
            )
        }
    }

    // Заголовок таблицы
    @Composable
    private fun MediaTableHeader() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            HeaderCell("Type", Modifier.weight(1f))
            HeaderCell("Name", Modifier.weight(2f))
            HeaderCell("Status", Modifier.weight(1f))
            HeaderCell("Score", Modifier.weight(1f))
            HeaderCell("Year", Modifier.weight(1f))
            HeaderCell("Date End", Modifier.weight(1f))
        }

        HorizontalDivider(
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }

    // Ячейка заголовка
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

    // Строка таблицы
    @Composable
    private fun MediaTableRow(media: MediaDtoRs, index: Int) {
        val backgroundColor = if (index % 2 == 0) {
            MaterialTheme.colorScheme.surface
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TableCell(media.type.toString(), Modifier.weight(1f))
            TableCell(media.title.toString(), Modifier.weight(2f))
            TableCell(media.status.toString(), Modifier.weight(1f))
            TableCell(media.score.toString(), Modifier.weight(1f))
            TableCell(media.releaseYear.toString(), Modifier.weight(1f))
            TableCell(media.endDate.toString(), Modifier.weight(1f))
        }
    }

    // Ячейка таблицы
    @Composable
    private fun TableCell(text: String, modifier: Modifier = Modifier) {
        Text(
            text = text,
            modifier = modifier.padding(4.dp),
            fontSize = 12.sp,
            maxLines = 2,
            softWrap = true
        )
    }
}