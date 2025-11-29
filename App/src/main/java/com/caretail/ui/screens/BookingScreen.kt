package com.caretail.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.caretail.ui.components.BottomNavBarOwner

@Composable
fun BookingScreen(nav: NavController) {
    val currentRoute = nav.currentBackStackEntryAsState().value?.destination?.route ?: ""
    var pet by remember { mutableStateOf("Buddy") }
    var days by remember { mutableStateOf("3") }
    var notes by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = { BottomNavBarOwner(nav = nav, currentRoute = currentRoute) }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // 🔹 Back Arrow
            IconButton(onClick = { nav.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }

            Spacer(Modifier.height(8.dp))

            Text("Booking Form", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = pet,
                onValueChange = { pet = it },
                label = { Text("Pet Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = days,
                onValueChange = { days = it },
                label = { Text("Days") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { nav.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit (demo)")
            }
        }
    }
}

