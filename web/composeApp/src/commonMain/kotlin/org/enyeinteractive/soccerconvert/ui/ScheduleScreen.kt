package org.enyeinteractive.soccerconvert.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.enyeinteractive.soccerconvert.medGreen2
import org.enyeinteractive.soccerconvert.offwhite1
import org.enyeinteractive.soccerconvert.ui.data.Location
import org.enyeinteractive.soccerconvert.ui.data.ScheduleMetadata
import org.enyeinteractive.soccerconvert.ui.data.ScheduledEvent
import org.enyeinteractive.soccerconvert.ui.viewmodel.ScheduleUiState
import org.enyeinteractive.soccerconvert.ui.viewmodel.ScheduleViewModel

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
            TextField(
                state = textState,
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
        val errorMessage: String? = uiState.errorMessage
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (uiState.events.isNotEmpty()) {

            Text(
                "Success!!",
                style = MaterialTheme.typography.bodyMediumEmphasized
            )
            Card(
                Modifier.padding(10.dp).fillMaxWidth(0.7f)
            ) {
                if (uiState.scheduleMetadata != null) {
                    HeaderContent(
                        scheduleMetadata = uiState.scheduleMetadata!!,
                        count = uiState.events.size
                    )
                }
            }

            Text(
                "Game Schedule",
                style = MaterialTheme.typography.titleLarge
            )
            LazyRow {
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
            val calendarCsv: String = uiState.calendarCsv
            Button(
                onClick = { viewModel.downloadCalendarFiles() },
                enabled = calendarCsv.isNotEmpty()
            ) {
                Text("Download CSV")
            }
            if (calendarCsv.isNotEmpty()) {
                Text(
                    text = "Google — Import the CSV (or ICS). Apple — open the downloaded .ics.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = "Calendar CSV",
                    style = MaterialTheme.typography.titleLarge
                )
                TextField(
                    value = calendarCsv,
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 160.dp),
                    readOnly = true,
                    minLines = 8,
                    maxLines = 16
                )
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
            Text("Season:")
            Text(
                scheduleMetadata.season,
                style = MaterialTheme.typography.bodyLargeEmphasized
            )
        }
        Row {
            Text("Coach:")
            Text(scheduleMetadata.coach)
        }
        Row {
            Text("Club:")
            Text(scheduleMetadata.club)
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
            Text(
                text = event.date,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = event.time,
                style = MaterialTheme.typography.bodyMediumEmphasized
            )
            if (event.opponentKey != BYE_MARKER) {
                Text(
                    text = "VS. ${event.opponent?.name ?: "TBD"}",
                    style = MaterialTheme.typography.bodyMediumEmphasized
                )
                Button(onClick = onAddToCalendar) {
                    Text("Add to calendar")
                }
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

private const val BYE_MARKER: String = "(BYE)"

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
