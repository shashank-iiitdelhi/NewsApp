package com.example.newsapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountrySelector(
    selectedCountry: String,
    onCountrySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val countryMap = mapOf(
        "Germany" to "de",
        "United States" to "us",
        "India" to "in",
        "Canada" to "ca",
        "Australia" to "au"
    )
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = countryMap.entries.find { it.value == selectedCountry }?.key ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Country") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                countryMap.forEach { (name, code) ->
                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            onCountrySelected(code)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    selectedScreen: String,
    onTabSelected: (String) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedScreen == "Top",
            onClick = { onTabSelected("Top") },
            label = { Text("Top News") },
            icon = { Icon(Icons.Default.DateRange, contentDescription = null) }
        )
        NavigationBarItem(
            selected = selectedScreen == "Trending",
            onClick = { onTabSelected("Trending") },
            label = { Text("Trending") },
            icon = { Icon(Icons.Rounded.KeyboardArrowUp, contentDescription = null) }
        )
        NavigationBarItem(
            selected = selectedScreen == "Saved",
            onClick = { onTabSelected("Saved") },
            label = { Text("Saved Articles") },
            icon = {Icon(
                painter = painterResource(id = R.drawable.bookmark_24),
                contentDescription = "Saved Articles"
            )
            }
        )
    }
}