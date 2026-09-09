package org.enyeinteractive.soccerconvert

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.enyeinteractive.soccerconvert.ui.ScheduleScreen
import org.enyeinteractive.soccerconvert.ui.viewmodel.ScheduleViewModel

@Composable
fun App() {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme
            .copy(
                primary = black1,
                secondary = black2,
                primaryContainer = medGreen,
                secondaryContainer = medGreen2 //TODO: change
            )
    ) {
        val viewModel: ScheduleViewModel = ScheduleViewModel()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Text(text = "Soccer Convert Script")
            Text(text = "Welcome to the Soccer calendar helper. Paste the season page source to scan.")
            Text(text = "1. Open the team schedule in another tab.")
            Text(text = "2. View page source: Cmd+Option+U (Mac) or Ctrl+U (Windows/Linux).")
            Text(text = "3. Select all, copy, and paste into the field below.")
            Text(text = "4. Click Submit.")
            ScheduleScreen(viewModel = viewModel)
        }
    }
}
