package org.cowary

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen // ВАЖНО: Voyager Screen
import cafe.adriel.voyager.navigator.LocalNavigator // Для навигации
import cafe.adriel.voyager.navigator.currentOrThrow // Для получения навигатора

// ProfileScreen тоже НАСЛЕДУЕТСЯ от Screen
class ProfileScreen : Screen {

    // Content() - метод, который описывает интерфейс
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        // Получаем навигатор
        val navigator = LocalNavigator.currentOrThrow

        // Вертикальный контейнер для всего экрана
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Верхняя панель (как ActionBar в Android)
            TopAppBar(
                title = { Text("Профиль") },
                navigationIcon = {
                    // Кнопка "назад"
                    IconButton(onClick = {
                        // Возвращаемся назад
                        navigator.pop()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )

            // Содержимое под верхней панелью
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Иконка профиля
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Аватар пользователя",
                    modifier = Modifier.size(120.dp)
                )

                // Пустое пространство
                Spacer(modifier = Modifier.height(16.dp))

                // Имя пользователя
                Text(
                    text = "Иван Иванов",
                    style = MaterialTheme.typography.headlineSmall
                )

                // Email
                Text(
                    text = "ivan@example.com",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                // Пустое пространство
                Spacer(modifier = Modifier.height(32.dp))

                // Кнопка "Назад"
                Button(
                    onClick = { navigator.pop() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Назад на главную")
                }
            }
        }
    }
}