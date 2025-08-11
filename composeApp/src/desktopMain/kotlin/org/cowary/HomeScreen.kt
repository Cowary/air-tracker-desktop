package org.cowary

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen // ВАЖНО: Voyager Screen
import cafe.adriel.voyager.navigator.LocalNavigator // Для навигации
import cafe.adriel.voyager.navigator.currentOrThrow // Для получения навигатора

// HomeScreen НАСЛЕДУЕТСЯ от Screen - это как implements в Java
// В Java это было бы: public class HomeScreen implements Screen
class HomeScreen : Screen {

    // Content() - метод, который описывает интерфейс экрана
    // @Composable - как аннотация @Composable в предыдущих примерах
    @Composable
    override fun Content() {
        // LocalNavigator - получаем навигатор (как Context в Android)
        // currentOrThrow - получаем навигатор или выбрасываем ошибку если нет
        val navigator = LocalNavigator.currentOrThrow

        // Column - вертикальный контейнер (как VBox)
        Column(
            modifier = Modifier
                .fillMaxSize()   // Заполняет весь экран
                .padding(16.dp), // Отступы
            horizontalAlignment = Alignment.CenterHorizontally, // Центрируем горизонтально
            verticalArrangement = Arrangement.Center           // Центрируем вертикально
        ) {
            // Заголовок
            Text(
                text = "Добро пожаловать!",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Кнопка перехода на профиль
            Button(
                onClick = {
                    // navigator.push() - переход на другой экран
                    // ProfileScreen() - создаем новый экземпляр экрана
                    navigator.push(ProfileScreen())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Перейти в профиль")
            }

            // Кнопка "назад" (если есть куда возвращаться)
            Button(
                onClick = {
                    // navigator.pop() - возврат на предыдущий экран
                    navigator.pop()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Назад (если есть)")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navigator.push(MediaListScreen())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Список медиа")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navigator.push(SearchAnimeScreen("anime"))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Поиск аниме")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navigator.push(SearchAnimeScreen("film"))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Поиск фильмов")
            }

        }


    }
}