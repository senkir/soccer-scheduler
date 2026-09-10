package org.enyeinteractive.soccerconvert

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.enyeinteractive.soccerconvert.locale.AppEnvironment
import org.enyeinteractive.soccerconvert.ui.EmailFooter
import org.enyeinteractive.soccerconvert.ui.ScheduleScreen
import org.enyeinteractive.soccerconvert.ui.viewmodel.ScheduleViewModel
import org.jetbrains.compose.resources.stringResource
import soccerconvertscript.composeapp.generated.resources.Res
import soccerconvertscript.composeapp.generated.resources.language_english

@Composable
fun App() {
    val viewModel: ScheduleViewModel = remember { ScheduleViewModel() }
    var selectedLocaleTag: String? by remember { mutableStateOf(
        null
    ) }
    AppEnvironment(localeTag = selectedLocaleTag) {
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme
                .copy(
                    primary = black1,
                    secondary = black2,
                    primaryContainer = medGreen,
                    secondaryContainer = medGreen2
                )
        ) {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .safeContentPadding()
                    .padding(16.dp),
                bottomBar = {
                    EmailFooter(
                        selectedLocaleTag = selectedLocaleTag,
                        onSelectLocale = { localeTag: String ->
                            selectedLocaleTag = localeTag
                        },
                        onClickEmail = {
                            viewModel.contactEmail()
                        }
                    )
                }
            ) {
                ScheduleScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}
