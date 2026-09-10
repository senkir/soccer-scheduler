package org.enyeinteractive.soccerconvert.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.intl.Locale
import kotlinx.browser.window
import org.w3c.dom.events.Event

actual object LocalAppLocale {
    private val localAppLocale = staticCompositionLocalOf { Locale.current }

    actual val current: String
        @Composable
        get() = localAppLocale.current.toString()

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        updateCustomLocale(value?.replace('_', '-'))
        return localAppLocale.provides(Locale.current)
    }
}

private fun updateCustomLocale(value: String?) {
    val currentValue: String? = window.asDynamic().__customLocale as String?
    if (currentValue == value) {
        return
    }
    window.asDynamic().__customLocale = value
    window.dispatchEvent(Event(LANGUAGE_CHANGE_EVENT))
}

private const val LANGUAGE_CHANGE_EVENT: String = "languagechange"
