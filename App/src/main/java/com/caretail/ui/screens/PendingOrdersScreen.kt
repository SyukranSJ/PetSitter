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
fun PendingOrdersScreen(
    nav: NavController,
    authViewModel: AuthViewModel = viewModel(),
    careTailViewModel: CareTailViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val bookingsState by careTailViewModel.bookingsState.collectAsState()
    var showMessage by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            careTailViewModel.loadSitterBookings(user.id)
        }
    }
    
    val pendingBookings = careTailViewModel.getPendingBookings()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pending Orders") },
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
                    showMessage?.let { message ->
                        Text(
                            message,
                            color = if (message.contains("Success") || message.contains("accepted") || message.contains("rejected")) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    
                    if (pendingBookings.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No pending orders", style = MaterialTheme.typography.titleMedium)
                        }
                    } else {
                        LazyColumn {
                            items(pendingBookings.size) { i ->
                                val booking = pendingBookings[i]
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Column(Modifier.padding(16.dp)) {
                                        Text(
                                            "Booking Request",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        
                                        Text("Booking ID: ${booking.id.take(8)}")
                                        Text("Days: ${booking.totalDays}")
                                        Text("Price: $${booking.totalPrice}")
                                        Text("Service: ${if (booking.isOnSite) "On-site" else "Off-site"}")
                                        
                                        if (booking.specialInstructions.isNotBlank()) {
                                            Spacer(Modifier.height(4.dp))
                                            Text(
                                                "Instructions: ${booking.specialInstructions}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        
                                        Spacer(Modifier.height(12.dp))
                                        
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = {
                                                    currentUser?.let { user ->
                                                        careTailViewModel.acceptBooking(booking.id, user.id) { success, message ->
                                                            showMessage = message
                                                        }
                                                    }
                                                },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Accept")
                                            }
                                            
                                            OutlinedButton(
                                                onClick = {
                                                    currentUser?.let { user ->
                                                        careTailViewModel.rejectBooking(booking.id, user.id) { success, message ->
                                                            showMessage = message
                                                        }
                                                    }
                                                },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Reject")
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
