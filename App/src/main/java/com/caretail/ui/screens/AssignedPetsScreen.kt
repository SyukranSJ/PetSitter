package com.caretail.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.caretail.data.Repo
import com.caretail.data.Role
import com.caretail.ui.components.BottomNavBarSitter

@Composable
fun AssignedPetsScreen(nav: NavController) {
    val currentUser = Repo.users.lastOrNull { it.role == Role.SITTER } ?: Repo.sitterAisha
    val currentRoute = nav.currentBackStackEntryAsState().value?.destination?.route ?: ""

    Scaffold(
        bottomBar = { BottomNavBarSitter(nav = nav, currentRoute = currentRoute) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "🐾 Assigned Pets",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Here are the pets currently assigned to you, ${currentUser.name}.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            // List of assigned pets (from Repo.bookings)
            val assignedPets = Repo.bookings.filter { it.sitter == currentUser }

            if (assignedPets.isEmpty()) {
                Text("No pets assigned right now.", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(assignedPets) { booking ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    booking.pet.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text("Type: ${booking.pet.type}")
                                Text("Owner: ${Repo.ownerSarah.name}")
                                Text("Days: ${booking.days}")
                                Text("Status: ${booking.status}")
                                Spacer(Modifier.height(8.dp))
                                Button(
                                    onClick = { /* in future: go to details or daily report */ },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("View Details")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
