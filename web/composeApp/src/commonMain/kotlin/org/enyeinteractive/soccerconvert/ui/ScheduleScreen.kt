package org.enyeinteractive.soccerconvert.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import org.enyeinteractive.soccerconvert.black2
import org.enyeinteractive.soccerconvert.locale.ENGLISH_LOCALE_TAG
import org.enyeinteractive.soccerconvert.locale.SPANISH_LOCALE_TAG
import org.enyeinteractive.soccerconvert.medGreen2
import org.enyeinteractive.soccerconvert.offwhite1
import org.enyeinteractive.soccerconvert.ui.data.Location
import org.enyeinteractive.soccerconvert.ui.data.ScheduleMetadata
import org.enyeinteractive.soccerconvert.ui.data.ScheduledEvent
import org.enyeinteractive.soccerconvert.ui.viewmodel.ScheduleError
import org.enyeinteractive.soccerconvert.ui.viewmodel.ScheduleUiState
import org.enyeinteractive.soccerconvert.ui.viewmodel.ScheduleViewModel
import org.enyeinteractive.soccerconvert.white1
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import soccerconvertscript.composeapp.generated.resources.Res
import soccerconvertscript.composeapp.generated.resources.add_to_calendar_cd
import soccerconvertscript.composeapp.generated.resources.contact
import soccerconvertscript.composeapp.generated.resources.contact_prompt
import soccerconvertscript.composeapp.generated.resources.download_calendar
import soccerconvertscript.composeapp.generated.resources.download_calendar_cd
import soccerconvertscript.composeapp.generated.resources.error_calendar_export
import soccerconvertscript.composeapp.generated.resources.error_empty_csv
import soccerconvertscript.composeapp.generated.resources.error_empty_events
import soccerconvertscript.composeapp.generated.resources.error_parse
import soccerconvertscript.composeapp.generated.resources.error_url_paste
import soccerconvertscript.composeapp.generated.resources.game_schedule
import soccerconvertscript.composeapp.generated.resources.gender_boys
import soccerconvertscript.composeapp.generated.resources.gender_girls
import soccerconvertscript.composeapp.generated.resources.group_grade_gender
import soccerconvertscript.composeapp.generated.resources.ic_calendar_add
import soccerconvertscript.composeapp.generated.resources.ic_calendar_add_2
import soccerconvertscript.composeapp.generated.resources.ic_open_window
import soccerconvertscript.composeapp.generated.resources.ic_paste
import soccerconvertscript.composeapp.generated.resources.ic_select_all
import soccerconvertscript.composeapp.generated.resources.instruction_open_cd
import soccerconvertscript.composeapp.generated.resources.instruction_open_subtitle
import soccerconvertscript.composeapp.generated.resources.instruction_paste_cd
import soccerconvertscript.composeapp.generated.resources.instruction_paste_subtitle
import soccerconvertscript.composeapp.generated.resources.instruction_select_all_cd
import soccerconvertscript.composeapp.generated.resources.instruction_select_all_subtitle
import soccerconvertscript.composeapp.generated.resources.label_club
import soccerconvertscript.composeapp.generated.resources.label_coach
import soccerconvertscript.composeapp.generated.resources.label_games
import soccerconvertscript.composeapp.generated.resources.label_group
import soccerconvertscript.composeapp.generated.resources.label_season
import soccerconvertscript.composeapp.generated.resources.language_default
import soccerconvertscript.composeapp.generated.resources.language_english
import soccerconvertscript.composeapp.generated.resources.language_settings
import soccerconvertscript.composeapp.generated.resources.language_spanish
import soccerconvertscript.composeapp.generated.resources.next_step_bulk
import soccerconvertscript.composeapp.generated.resources.next_step_import
import soccerconvertscript.composeapp.generated.resources.next_step_individual
import soccerconvertscript.composeapp.generated.resources.opponent_tbd
import soccerconvertscript.composeapp.generated.resources.page_source_label
import soccerconvertscript.composeapp.generated.resources.page_source_placeholder
import soccerconvertscript.composeapp.generated.resources.submit
import soccerconvertscript.composeapp.generated.resources.success_title
import soccerconvertscript.composeapp.generated.resources.vs_opponent
import soccerconvertscript.composeapp.generated.resources.welcome_title

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel
) {
    val textState = rememberTextFieldState("")
    val uiState: ScheduleUiState by viewModel.uiState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (uiState.events.isEmpty()) {
            Column(
                Modifier.background(
                    color = offwhite1
                ).padding(4.dp)
            ) {
                SetupProcessGraphic()
                TextField(
                    state = textState,
                    colors =
                        TextFieldDefaults.colors().copy(
                            focusedTextColor = black2,
                            unfocusedTextColor = black2,
                            unfocusedContainerColor = white1,
                            focusedContainerColor = white1
                        ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 160.dp),
                    label = { Text(text = stringResource(Res.string.page_source_label)) },
                    placeholder = { Text(text = stringResource(Res.string.page_source_placeholder)) },
                    lineLimits = TextFieldLineLimits.MultiLine(
                        minHeightInLines = 8,
                        maxHeightInLines = 16
                    )
                )
                Button(
                    onClick = { viewModel.onSubmit(textState.text.toString()) },
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(text = stringResource(Res.string.submit))
                }
            }
        }
        val error: ScheduleError? = uiState.error
        if (error != null) {
            Text(
                text = resolveErrorMessage(error),
                color = MaterialTheme.colorScheme.error
            )
        }

        if (uiState.events.isNotEmpty()) {

            if (uiState.scheduleMetadata != null) {

                NextStepsGraphic()

                Card {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(
                            100.dp,
                            Alignment.CenterHorizontally
                        ),
                        modifier = Modifier.wrapContentWidth().padding(10.dp)
                    ) {


                        HeaderContent(
                            scheduleMetadata = uiState.scheduleMetadata!!,
                            count = uiState.events.size
                        )

                        Button(
                            onClick = {
                                viewModel.downloadCalendarFiles()
                            },
                            modifier = Modifier.padding(4.dp).widthIn(300.dp)
                        ) {
                            Icon(
                                painterResource(Res.drawable.ic_calendar_add_2),
                                modifier = Modifier.size(38.dp),
                                contentDescription = stringResource(Res.string.download_calendar_cd)
                            )
                            Text(stringResource(Res.string.download_calendar))
                        }
                    }
                }
            }

            Text(
                stringResource(Res.string.game_schedule),
                style = MaterialTheme.typography.titleLarge
            )
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 300.dp),
                modifier = Modifier.heightIn(min = 300.dp, max = 1500.dp)
            ) {
                uiState.events.forEach { event: ScheduledEvent ->
                    item {
                        ScheduledEventCard(
                            event = event,
                            modifier = Modifier.padding(10.dp),
                            onAddToCalendar = { viewModel.addEventToCalendar(event) }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun EmailFooter(
    selectedLocaleTag: String?,
    onSelectLocale: (String) -> Unit,
    onClickEmail: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(top = 100.dp, start = 10.dp, end = 10.dp, bottom = 10.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        LanguageSettingsButton(
            selectedLocaleTag = selectedLocaleTag,
            onSelectLocale = onSelectLocale
        )
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                stringResource(Res.string.contact_prompt)
            )
            Button(
                onClick = onClickEmail
            ) {
                Text(stringResource(Res.string.contact))
            }
        }
    }
}

@Composable
private fun LanguageSettingsButton(
    selectedLocaleTag: String?,
    onSelectLocale: (String) -> Unit
) {
    var isExpanded: Boolean by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            stringResource(Res.string.language_settings),
            modifier = Modifier.padding(4.dp)
        )
        OutlinedButton(
            onClick = { isExpanded = !isExpanded }
        ) {
            Text(selectedLocaleTag ?: stringResource(Res.string.language_default))
        }

        DropdownMenu(
            isExpanded,
            onDismissRequest = {
                isExpanded = false
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(4.dp)
            ) {
                TextButton(
                    onClick = {
                        onSelectLocale(ENGLISH_LOCALE_TAG)
                        isExpanded = false
                    }
                ) {
                    Text(stringResource(Res.string.language_english))
                }
                TextButton(
                    onClick = {
                        onSelectLocale(SPANISH_LOCALE_TAG)
                        isExpanded = false
                    }
                ) {
                    Text(stringResource(Res.string.language_spanish))
                }
            }
        }
    }
}

@Composable
fun HeaderContent(
    scheduleMetadata: ScheduleMetadata,
    count: Int
) {
    Column(
        Modifier.padding(4.dp)
    ) {
        Row {
            Text(stringResource(Res.string.label_coach))
            Text(scheduleMetadata.coach)
        }
        Row {
            Text(stringResource(Res.string.label_club))
            Text(scheduleMetadata.club)
        }
        Row {
            Text(stringResource(Res.string.label_season))
            Text(
                scheduleMetadata.season,
                style = MaterialTheme.typography.bodyLargeEmphasized
            )
        }
        Row {
            Text(stringResource(Res.string.label_group))
            Text(formatGroupLabel(scheduleMetadata.group))
        }
        Row {
            Text(stringResource(Res.string.label_games))
            Text(count.toString())
        }
    }
}

@Composable
fun ScheduledEventCard(
    event: ScheduledEvent,
    modifier: Modifier = Modifier,
    onAddToCalendar: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors().copy(
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            EventHeader(event, onAddToCalendar)

            if (event.opponentKey != BYE_MARKER) {
                val opponentName: String = event.opponent?.name
                    ?: stringResource(Res.string.opponent_tbd)
                Text(
                    text = stringResource(Res.string.vs_opponent, opponentName),
                    style = MaterialTheme.typography.bodyMediumEmphasized
                )
            }
        }
        if (event.location != null) {
            LocationCard(
                event.location,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Composable
fun EventHeader(
    event: ScheduledEvent,
    onAddToCalendar: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth().padding(4.dp)
    ) {
        IconButton(
            onClick = onAddToCalendar,
            modifier = Modifier.padding(4.dp)
        ) {
            Icon(
                painterResource(Res.drawable.ic_calendar_add),
                contentDescription = stringResource(Res.string.add_to_calendar_cd),
                modifier = Modifier.size(28.dp)
            )
        }

        Column {

            Text(
                text = event.date,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = event.time,
                style = MaterialTheme.typography.bodyMediumEmphasized
            )

        }
    }
}

private const val BYE_MARKER: String = "(BYE)"
private const val GENDER_BOYS_CODE: String = "B"
private const val GENDER_GIRLS_CODE: String = "G"
private const val GRADE_NUMBER_GROUP: Int = 1
private const val GENDER_CODE_GROUP: Int = 2
private val GROUP_CODE_REGEX: Regex = Regex("^G(\\d+)([BG])$")

@Composable
private fun resolveErrorMessage(error: ScheduleError): String {
    return when (error) {
        ScheduleError.UrlPaste -> stringResource(Res.string.error_url_paste)
        ScheduleError.ParseFailed -> stringResource(Res.string.error_parse)
        ScheduleError.EmptyEvents -> stringResource(Res.string.error_empty_events)
        ScheduleError.EmptyCsv -> stringResource(Res.string.error_empty_csv)
        ScheduleError.CalendarExportFailed -> stringResource(Res.string.error_calendar_export)
        is ScheduleError.Conversion -> error.details.joinToString("\n")
    }
}

@Composable
private fun formatGroupLabel(groupCode: String): String {
    val match: MatchResult = GROUP_CODE_REGEX.find(groupCode.trim().uppercase())
        ?: return groupCode
    val gradeNumber: String = match.groupValues[GRADE_NUMBER_GROUP]
    val genderCode: String = match.groupValues[GENDER_CODE_GROUP]
    val genderLabel: String = when (genderCode) {
        GENDER_BOYS_CODE -> stringResource(Res.string.gender_boys)
        GENDER_GIRLS_CODE -> stringResource(Res.string.gender_girls)
        else -> return groupCode
    }
    return stringResource(Res.string.group_grade_gender, gradeNumber, genderLabel)
}

@Composable
fun NextStepsGraphic() {

    Column(
        Modifier.background(color = offwhite1).padding(4.dp)
    ) {
        Text(
            text = stringResource(Res.string.success_title),
            style = typography.displaySmallEmphasized
        )
        Text(
            text = stringResource(Res.string.next_step_individual)
        )
        Text(
            text = stringResource(Res.string.next_step_bulk)
        )
        Text(
            text = stringResource(Res.string.next_step_import)
        )
    }
}


@Composable
fun SetupProcessGraphic() {

    Text(
        text = stringResource(Res.string.welcome_title),
        modifier = Modifier.padding(4.dp),
        style = typography.displaySmallEmphasized
    )
    Row(
        modifier = Modifier.fillMaxWidth(0.8f).padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val pasteIcon = painterResource(Res.drawable.ic_paste)
        val allIcon = painterResource(Res.drawable.ic_select_all)
        val openIcon = painterResource(Res.drawable.ic_open_window)

        InstructionalIcon(
            openIcon,
            contentDescription = stringResource(Res.string.instruction_open_cd),
            subtitle = stringResource(Res.string.instruction_open_subtitle)
        )
        InstructionalIcon(
            allIcon,
            contentDescription = stringResource(Res.string.instruction_select_all_cd),
            subtitle = stringResource(Res.string.instruction_select_all_subtitle)
        )
        InstructionalIcon(
            pasteIcon,
            contentDescription = stringResource(Res.string.instruction_paste_cd),
            subtitle = stringResource(Res.string.instruction_paste_subtitle)
        )

    }
}

@Composable
fun InstructionalIcon(
    image: Painter,
    contentDescription: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            image,
            contentDescription = contentDescription,
            modifier = modifier.size(80.dp)
        )
        Text(
            subtitle,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
fun LocationCard(
    location: Location,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors().copy(
            contentColor = offwhite1,
            containerColor = medGreen2
        )
    ) {
        Column(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = location.name,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = location.address,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
