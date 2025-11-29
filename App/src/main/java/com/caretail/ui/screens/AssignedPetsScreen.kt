package com.caretail.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.caretail.viewmodel.AuthViewModel
import com.caretail.viewmodel.CareTailViewModel
import com.caretail.viewmodel.DataState

@Composable
fun AssignedPetsScreen(
    nav: NavController,
    authViewModel: AuthViewModel = viewModel(),
    careTailViewModel: CareTailViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val bookingsState by careTailViewModel.bookingsState.collectAsState()

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            careTailViewModel.loadSitterBookings(user.id)
        }
    }

    val activeBookings = careTailViewModel.getActiveBookings()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Assigned Pets") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when (bookingsState) {
                is DataState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is DataState.Error -> {
                    Text(
                        "Error: ${(bookingsState as DataState.Error).message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> {
                    if (activeBookings.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No active assignments", style = MaterialTheme.typography.titleMedium)
                        }
                    } else {
                        LazyColumn {
                            items(activeBookings.size) { i ->
                                val booking = activeBookings[i]
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Column(Modifier.padding(16.dp)) {
                                        Text(
                                            "Active Assignment",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(Modifier.height(8.dp))

                                        Text("Booking ID: ${booking.id.take(8)}")
                                        Text("Pet ID: ${booking.petId.take(8)}")
                                        Text("Days: ${booking.totalDays}")
                                        Text("Status: ${booking.status}")

                                        Spacer(Modifier.height(12.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = { nav.navigate("daily_report") },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Submit Report")
                                            }

                                            OutlinedButton(
                                                onClick = { nav.navigate("checklist") },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Checklist")
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
