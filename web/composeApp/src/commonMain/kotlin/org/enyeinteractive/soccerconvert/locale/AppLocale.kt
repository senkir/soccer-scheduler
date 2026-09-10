package org.enyeinteractive.soccerconvert.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.key

expect object LocalAppLocale {
    val current: String
        @Composable get

    @Composable
    infix fun provides(value: String?): ProvidedValue<*>
}

@Composable
fun AppEnvironment(
    localeTag: String?,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalAppLocale provides localeTag
    ) {
        key(localeTag) {
            content()
        }
    }
}

const val ENGLISH_LOCALE_TAG: String = "en"
const val SPANISH_LOCALE_TAG: String = "es"
