package com.example.newsapp

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

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

    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }

        ) {
            val textFieldModifier = Modifier
                .menuAnchor()
                .fillMaxWidth(0.9f)
                .border(
                    1.dp,
                    Color(0xFF8B0000),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(40.dp)
                )
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(40.dp))
            TextField(
                value = countryMap.entries.find { it.value == selectedCountry }?.key ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Country") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = textFieldModifier
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier
                .fillMaxWidth(0.9f)) {
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
            icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
            colors = NavigationBarItemColors(
                selectedIconColor = Color(0xFFFFFFFF), // Dark red for selected icon
                unselectedIconColor = Color.Gray, // Gray for unselected icon
                selectedTextColor = Color(0xFF8B0000), // Dark red for selected text
                unselectedTextColor = Color.Gray, // Gray for unselected text
                selectedIndicatorColor = Color(0xFF8B0000), // Dark red indicator when selected
                disabledIconColor = Color.LightGray, // Light gray for disabled icon
                disabledTextColor = Color.LightGray // Light gray for disabled text
            )
        )

        NavigationBarItem(
            selected = selectedScreen == "Trending",
            onClick = { onTabSelected("Trending") },
            label = { Text("Trending") },
            icon = { Icon(painter = painterResource(id = R.drawable.trending_up_24px), contentDescription = null) },
            colors = NavigationBarItemColors(
                selectedIconColor = Color(0xFFFFFFFF), // Dark red for selected icon
                unselectedIconColor = Color.Gray, // Gray for unselected icon
                selectedTextColor = Color(0xFF8B0000), // Dark red for selected text
                unselectedTextColor = Color.Gray, // Gray for unselected text
                selectedIndicatorColor = Color(0xFF8B0000), // Dark red indicator when selected
                disabledIconColor = Color.LightGray, // Light gray for disabled icon
                disabledTextColor = Color.LightGray // Light gray for disabled text
            )
        )

        NavigationBarItem(
            selected = selectedScreen == "Saved",
            onClick = { onTabSelected("Saved") },
            label = { Text("Saved Articles") },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.bookmark_24),
                    contentDescription = "Saved Articles"
                )
            },
            colors = NavigationBarItemColors(
                selectedIconColor = Color(0xFFFFFFFF), // Dark red for selected icon
                unselectedIconColor = Color.Gray, // Gray for unselected icon
                selectedTextColor = Color(0xFF8B0000), // Dark red for selected text
                unselectedTextColor = Color.Gray, // Gray for unselected text
                selectedIndicatorColor = Color(0xFF8B0000), // Dark red indicator when selected
                disabledIconColor = Color.LightGray, // Light gray for disabled icon
                disabledTextColor = Color.LightGray // Light gray for disabled text
            )
        )
    }
}