package org.enyeinteractive.soccerconvert

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.enyeinteractive.soccerconvert.ui.EmailFooter
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
        Scaffold(
            modifier = Modifier
                    .fillMaxSize()
                    .safeContentPadding()
                    .padding(16.dp),
//            horizontalAlignment = Alignment.Start,
//            verticalArrangement = Arrangement.Top
            bottomBar = {
                EmailFooter {
                    viewModel.contactEmail()
                }
            }
        ) {
            ScheduleScreen(
                viewModel = viewModel
            )
        }
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .safeContentPadding()
//                .padding(16.dp),
//            horizontalAlignment = Alignment.Start,
//            verticalArrangement = Arrangement.Top
//        ) {
//            ScheduleScreen(viewModel = viewModel)
//        }
    }
}
