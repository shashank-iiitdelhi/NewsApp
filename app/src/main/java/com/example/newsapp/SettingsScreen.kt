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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {

    Column(modifier = Modifier.padding(16.dp)) {
        val redColor = Color(0xFF8B0000)
        Text(text = "Settings", style = MaterialTheme.typography.headlineMedium)

        SettingsOption(
            title = "Enable Dark Mode",
            isEnabled = isDarkMode,
            onCheckedChange = onDarkModeToggle,
            color = redColor
        )



        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = redColor, contentColor = Color.White)) {
            Text(text = "Close Settings")

        }
    }
}

@Composable
fun SettingsOption(
    title: String,
    isEnabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    color: Color
) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(text = title, modifier = Modifier.weight(1f))
        val redTrackColor = Color(0xFFFFA07A)
        Switch(
            checked = isEnabled,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = color, // Change the switch thumb color to red
//                uncheckedThumbColor = color.copy(alpha = 0.5f), // Change the switch thumb color when unchecked
                checkedTrackColor = redTrackColor, // Change the track color when checked
//                uncheckedTrackColor = color.copy(alpha = 0.1f) // Change the track color when unchecked
            )
        )
    }
}
