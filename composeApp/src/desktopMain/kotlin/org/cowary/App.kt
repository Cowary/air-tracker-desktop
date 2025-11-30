package org.cowary

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.cowary.ui.screens.HomeScreen

@Composable
fun App() {
    MaterialTheme {
        // Surface - базовый контейнер с фоном
        Surface(
            modifier = androidx.compose.ui.Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // Navigator - КОНТЕЙНЕР навигации Voyager
            // Это как NavHost в предыдущих примерах, но проще
            Navigator(HomeScreen()) { navigator ->
                // SlideTransition - анимация переходов (слайд вправо/влево)
                SlideTransition(navigator)

                // Альтернативы:
                // FadeTransition(navigator) - плавное появление
                // ScaleTransition(navigator) - масштабирование
            }
        }
    }
}

//@Composable
//@Preview
//fun App() {
//    var apiResponse by remember { mutableStateOf("Waiting...") }
//    val scope = rememberCoroutineScope()
//    var res by remember { mutableStateOf("Waiting...") }
//
//    MaterialTheme {
//        var showContent by remember { mutableStateOf(false) }
//        Column(
//            modifier = Modifier
//                .safeContentPadding()
//                .fillMaxSize(),
//            horizontalAlignment = Alignment.CenterHorizontally,
//        ) {
//            Button(onClick = { showContent = !showContent }) {
//                Text("Click me!")
//            }
//            AnimatedVisibility(showContent) {
//                val greeting = remember { Greeting().greet() }
//                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
//                    Image(painterResource(Res.drawable.compose_multiplatform), null)
//                    Text("Compose: $greeting")
//                }
//            }
//            Button(onClick = {
//                scope.launch {
//                    apiResponse = ApiService().fetchData()
//                    var r = ApiService().fetchAll() as List<Media>
//                    res = r.get(0).toString()
//                }
//            }) {
////                Text("Touch me!")
//                Text(res.toString())
//            }
//
//
//        }
//    }
//}