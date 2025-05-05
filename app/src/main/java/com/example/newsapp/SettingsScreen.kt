package com.example.newsapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Settings", style = MaterialTheme.typography.headlineMedium)

        SettingsOption(
            title = "Enable Dark Mode",
            isEnabled = isDarkMode,
            onCheckedChange = onDarkModeToggle
        )

        SettingsOption(
            title = "Language",
            isEnabled = false,
            onCheckedChange = { /* handle language change */ }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onDismiss) {
            Text(text = "Close Settings")
        }
    }
}

@Composable
fun SettingsOption(
    title: String,
    isEnabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(text = title, modifier = Modifier.weight(1f))
        Switch(
            checked = isEnabled,
            onCheckedChange = onCheckedChange
        )
    }
}
