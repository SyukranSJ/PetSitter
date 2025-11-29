package com.caretail.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.caretail.data.*
import com.caretail.viewmodel.AuthViewModel
import com.caretail.viewmodel.CareTailViewModel
import com.caretail.viewmodel.DataState

@Composable
fun OwnerHomeScreen(
    nav: NavController,
    authViewModel: AuthViewModel = viewModel(),
    careTailViewModel: CareTailViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val bookings by careTailViewModel.bookings.collectAsState()
    val bookingsState by careTailViewModel.bookingsState.collectAsState()
    var selectedTab by remember { mutableStateOf("home") }
    
    // Load bookings when user is available
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            careTailViewModel.loadOwnerBookings(user.id)
        }
    }

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
                    icon = { Icon(Icons.Filled.List, contentDescription = "Reports") },
                    label = { Text("Reports") }
                )
                NavigationBarItem(
                    selected = selectedTab == "profile",
                    onClick = {
                        selectedTab = "profile"
                        nav.navigate("owner_profile")
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
                        text = "Welcome, ${currentUser?.name ?: "Owner"} 👋",
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
                        authViewModel.logout()
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

            Button(
                onClick = { nav.navigate("owner_pet_list") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("My Pets")
            }


            Spacer(Modifier.height(16.dp))

            Text("My Care Sessions", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            // Show loading/error states
            when (bookingsState) {
                is DataState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is DataState.Error -> {
                    Text(
                        "Error loading bookings: ${(bookingsState as DataState.Error).message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> {
                    if (bookings.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "No bookings yet",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Book a sitter to get started!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        LazyColumn {
                            items(bookings.size) { i ->
                                val booking = bookings[i]
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                                ) {
                                    Column(Modifier.padding(12.dp)) {
                                        Text(
                                            "Booking #${booking.id.take(8)}",
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text("Days: ${booking.totalDays} • Status: ${booking.status}")
                                        Text("Total: $${booking.totalPrice}")
                                        
                                        if (booking.getStatusEnum() == BookingStatus.IN_PROGRESS ||
                                            booking.getStatusEnum() == BookingStatus.COMPLETED) {
                                            TextButton(onClick = { nav.navigate("daily_report") }) {
                                                Text("View Daily Reports")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

