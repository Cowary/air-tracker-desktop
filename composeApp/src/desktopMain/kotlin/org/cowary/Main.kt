package org.cowary

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import java.awt.Toolkit

fun main() = application {
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val windowWidth: Dp = (screenSize.width * 0.8f).dp
    val windowHeight: Dp = (screenSize.height * 0.8f).dp

    val windowState = rememberWindowState(
        placement = WindowPlacement.Floating,
        position = WindowPosition.Aligned(Alignment.Center),
        size = DpSize(windowWidth, windowHeight)
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "air-tracker-desktop",
        state = windowState,

    ) {
        App()
    }
}