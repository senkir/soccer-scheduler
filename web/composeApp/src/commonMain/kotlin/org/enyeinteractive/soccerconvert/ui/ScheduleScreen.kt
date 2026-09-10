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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import org.enyeinteractive.soccerconvert.black2
import org.enyeinteractive.soccerconvert.medGreen2
import org.enyeinteractive.soccerconvert.offwhite1
import org.enyeinteractive.soccerconvert.ui.data.Location
import org.enyeinteractive.soccerconvert.ui.data.ScheduleMetadata
import org.enyeinteractive.soccerconvert.ui.data.ScheduledEvent
import org.enyeinteractive.soccerconvert.ui.viewmodel.ScheduleUiState
import org.enyeinteractive.soccerconvert.ui.viewmodel.ScheduleViewModel
import org.enyeinteractive.soccerconvert.white1
import org.jetbrains.compose.resources.painterResource
import soccerconvertscript.composeapp.generated.resources.Res
import soccerconvertscript.composeapp.generated.resources.ic_calendar_add
import soccerconvertscript.composeapp.generated.resources.ic_calendar_add_2
import soccerconvertscript.composeapp.generated.resources.ic_open_window
import soccerconvertscript.composeapp.generated.resources.ic_paste
import soccerconvertscript.composeapp.generated.resources.ic_select_all

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
                    label = { Text(text = "Page source") },
                    placeholder = { Text(text = "Paste page source, not a URL") },
                    lineLimits = TextFieldLineLimits.MultiLine(
                        minHeightInLines = 8,
                        maxHeightInLines = 16
                    )
                )
                Button(
                    onClick = { viewModel.onSubmit(textState.text.toString()) },
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(text = "Submit")
                }
            }
        }
        val errorMessage: String? = uiState.errorMessage
        if (errorMessage != null) {
            Text(
                text = errorMessage,
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
                                contentDescription = "Download Calendar"
                            )
                            Text("Download Calendar.")
                        }
                    }
                }
            }

            Text(
                "Game Schedule",
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
//    EmailFooter({
//        viewModel.contactEmail()
//    })
}

@Composable
fun EmailFooter(
    onClickEmail: () -> Unit
) {
    Column(
        Modifier.padding(top = 100.dp, start = 10.dp, end = 10.dp, bottom = 10.dp)
            .fillMaxWidth()
    ) {
        Text(
            "For issues with scheduler please use this contact link.",
            modifier = Modifier.align(Alignment.End)
        )
        Button(
            onClick = onClickEmail,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Contact")
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
            Text("Coach:")
            Text(scheduleMetadata.coach)
        }

        Row {
            Text("Club:")
            Text(scheduleMetadata.club)
        }

        Row {
            Text("Season:")
            Text(
                scheduleMetadata.season,
                style = MaterialTheme.typography.bodyLargeEmphasized
            )
        }
        Row {
            Text("Group:")
            Text(scheduleMetadata.group)
        }
        Row {
            Text("Games:")
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
                Text(
                    text = "VS. ${event.opponent?.name ?: "TBD"}",
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
                contentDescription = "Add to Calendar.",
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

@Composable
fun NextStepsGraphic() {

    Column(
        Modifier.background(color = offwhite1).padding(4.dp)
    ) {
        Text(
            text = "Success!",
            style = typography.displaySmallEmphasized
        )
        Text(
            text = "1. Click individual games to add them to your calendar of choice."
        )
        Text(
            text = "2. Bulk add the whole schedule of games. "
        )
        Text(
            text = "Google — Import the CSV (or ICS). Apple — open the downloaded .ics."
        )
    }
}


@Composable
fun SetupProcessGraphic() {

    Text(
        text = "Welcome to the Soccer calendar helper.",
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
            contentDescription = "Open",
            subtitle = "Open the team schedule in another tab."
        )
        InstructionalIcon(
            allIcon,
            contentDescription = "Select All",
            subtitle = "Select all, copy."
        )
        InstructionalIcon(
            pasteIcon,
            contentDescription = "Paste",
            subtitle = "Paste into the field below."
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
