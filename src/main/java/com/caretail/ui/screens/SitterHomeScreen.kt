package com.caretail.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.caretail.data.BookingStatus
import com.caretail.ui.components.BottomNavBarSitter
import com.caretail.viewmodel.AuthViewModel
import com.caretail.viewmodel.CareTailViewModel
import com.caretail.viewmodel.DataState

@Composable
fun SitterHomeScreen(
    nav: NavController,
    authViewModel: AuthViewModel = viewModel(),
    careTailViewModel: CareTailViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val bookings by careTailViewModel.bookings.collectAsState()
    val bookingsState by careTailViewModel.bookingsState.collectAsState()
    val currentRoute = nav.currentBackStackEntryAsState().value?.destination?.route ?: ""

    // Load sitter bookings when user is available
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            careTailViewModel.loadSitterBookings(user.id)
        }
    }
    
    // Calculate statistics
    val activeBookings = bookings.filter { 
        it.getStatusEnum() == BookingStatus.CONFIRMED || 
        it.getStatusEnum() == BookingStatus.IN_PROGRESS 
    }
    val pendingBookings = bookings.filter { 
        it.getStatusEnum() == BookingStatus.PENDING 
    }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Welcome, ${currentUser?.name ?: "Sitter"} 👋",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "🐾 Pet Sitter",
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
            Text("Dashboard", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

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
                    Text("Here's a quick look at your sitter activity:")
                    Spacer(Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                "🦴 Active Assignments: ${activeBookings.size}",
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "⏳ Pending Orders: ${pendingBookings.size}",
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "✅ Total Bookings: ${bookings.size}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    
                    if (pendingBookings.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { nav.navigate("pending_orders") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("View Pending Orders (${pendingBookings.size})")
                        }
                    }
                    
                    if (activeBookings.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { nav.navigate("assigned_pets") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("View Assigned Pets (${activeBookings.size})")
                        }
                    }
                }
            }
        }
    }
}
