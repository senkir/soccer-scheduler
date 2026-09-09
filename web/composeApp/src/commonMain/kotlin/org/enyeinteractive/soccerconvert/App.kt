package org.enyeinteractive.soccerconvert

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun App() {
    MaterialTheme {
        var isContentVisible: Boolean by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = "Soccer Convert Script")
            Button(onClick = { isContentVisible = !isContentVisible }) {
                Text(text = "Click me")
            }
            if (isContentVisible) {
                Text(text = "Compose Multiplatform is running in the browser.")
            }
        }
    }
}
