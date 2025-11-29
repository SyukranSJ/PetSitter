package com.caretail.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.caretail.data.Repo
import com.caretail.data.Role

@Composable
fun OwnerHomeScreen(nav: NavController) {
    val currentUser = Repo.users.lastOrNull { it.role == Role.OWNER } ?: Repo.ownerSarah
    var selectedTab by remember { mutableStateOf("home") }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == "home",
                    onClick = {
                        selectedTab = "home"
                        nav.navigate("owner_home")
                    },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedTab == "booking",
                    onClick = {
                        selectedTab = "booking"
                        nav.navigate("booking")
                    },
                    icon = { Icon(Icons.Filled.DateRange, contentDescription = "Booking") },
                    label = { Text("Booking") }
                )
                NavigationBarItem(
                    selected = selectedTab == "report",
                    onClick = {
                        selectedTab = "report"
                        nav.navigate("daily_report")
                    },
                    // ✅ FINAL: Using List icon (always available)
                    icon = { Icon(Icons.Filled.List, contentDescription = "Reports") },
                    label = { Text("Reports") }
                )
                NavigationBarItem(
                    selected = selectedTab == "profile",
                    onClick = {
                        selectedTab = "profile"
                        nav.navigate("profile")
                    },
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                    label = { Text("Profile") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Welcome, ${currentUser.name} 👋",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "🏠 Pet Owner",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                TextButton(
                    onClick = {
                        nav.navigate("landing") {
                            popUpTo("landing") { inclusive = true }
                        }
                    }
                ) {
                    Text("Logout", color = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { nav.navigate("booking") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Book a Sitter")
            }

            Spacer(Modifier.height(16.dp))

            Text("My Care Sessions", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            LazyColumn {
                items(Repo.bookings.size) { i ->
                    val b = Repo.bookings[i]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text("${b.pet.name} with ${b.sitter.name}", fontWeight = FontWeight.SemiBold)
                            Text("Days: ${b.days} • Status: ${b.status}")
                            TextButton(onClick = { nav.navigate("daily_report") }) {
                                Text("View Daily Report")
                            }
                        }
                    }
                }
            }
        }
    }
}





