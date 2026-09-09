package org.enyeinteractive.soccerconvert

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import org.w3c.dom.HTMLElement

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val viewportContainer: HTMLElement = document.body ?: return
    ComposeViewport(viewportContainer) {
        App()
    }
}
