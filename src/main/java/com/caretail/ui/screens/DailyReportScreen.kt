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
import com.caretail.data.BookingStatus
import com.caretail.data.Role
import com.caretail.viewmodel.AuthViewModel
import com.caretail.viewmodel.CareTailViewModel

@Composable
fun DailyReportScreen(
    nav: NavController,
    authViewModel: AuthViewModel = viewModel(),
    careTailViewModel: CareTailViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val bookings by careTailViewModel.bookings.collectAsState()
    val reports by careTailViewModel.reports.collectAsState()
    
    var selectedBookingId by remember { mutableStateOf<String?>(null) }
    var meals by remember { mutableStateOf("") }
    var walks by remember { mutableStateOf("") }
    var playTime by remember { mutableStateOf("") }
    var bathroomBreaks by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf("Happy") }
    var notes by remember { mutableStateOf("") }
    var showMessage by remember { mutableStateOf<String?>(null) }
    
    val isSitter = currentUser?.getRoleEnum() == Role.SITTER
    
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            if (isSitter) {
                careTailViewModel.loadSitterBookings(user.id)
            } else {
                careTailViewModel.loadOwnerBookings(user.id)
            }
        }
    }
    
    LaunchedEffect(selectedBookingId) {
        selectedBookingId?.let { bookingId ->
            careTailViewModel.loadReports(bookingId)
        }
    }
    
    val activeBookings = bookings.filter {
        it.getStatusEnum() == BookingStatus.IN_PROGRESS || it.getStatusEnum() == BookingStatus.COMPLETED
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Reports") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                if (activeBookings.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text("No active bookings to report on")
                    }
                } else {
                    Text("Select Booking:", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    
                    activeBookings.forEach { booking ->
                        FilterChip(
                            selected = selectedBookingId == booking.id,
                            onClick = { selectedBookingId = booking.id },
                            label = { Text("Booking ${booking.id.take(8)} - ${booking.status}") },
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
                
                selectedBookingId?.let { bookingId ->
                    val selectedBooking = bookings.find { it.id == bookingId }
                    
                    Divider(Modifier.padding(vertical = 16.dp))
                    
                    // Sitter can submit reports
                    if (isSitter) {
                        Text("Submit Daily Report", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(12.dp))
                        
                        OutlinedTextField(
                            value = meals,
                            onValueChange = { meals = it },
                            label = { Text("Meals Given") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = walks,
                            onValueChange = { walks = it },
                            label = { Text("Walks Completed") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = playTime,
                            onValueChange = { playTime = it },
                            label = { Text("Play Time (minutes)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = bathroomBreaks,
                            onValueChange = { bathroomBreaks = it },
                            label = { Text("Bathroom Breaks") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = mood,
                            onValueChange = { mood = it },
                            label = { Text("Pet's Mood") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                        
                        showMessage?.let { message ->
                            Spacer(Modifier.height(8.dp))
                            Text(
                                message,
                                color = if (message.contains("Success")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }
                        
                        Spacer(Modifier.height(16.dp))
                        
                        Button(
                            onClick = {
                                selectedBooking?.let { booking ->
                                    currentUser?.let { user ->
                                        careTailViewModel.submitDailyReport(
                                            bookingId = bookingId,
                                            petId = booking.petId,
                                            sitterId = user.id,
                                            mealsTaken = meals.toIntOrNull() ?: 0,
                                            walksCompleted = walks.toIntOrNull() ?: 0,
                                            playTimeMinutes = playTime.toIntOrNull() ?: 0,
                                            bathroomBreaks = bathroomBreaks.toIntOrNull() ?: 0,
                                            mood = mood,
                                            healthStatus = "Normal",
                                            notes = notes,
                                            photoUrls = emptyList(),
                                            location = ""
                                        ) { success, message ->
                                            showMessage = message
                                            if (success) {
                                                meals = ""
                                                walks = ""
                                                playTime = ""
                                                bathroomBreaks = ""
                                                notes = ""
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Submit Report")
                        }
                        
                        Divider(Modifier.padding(vertical = 16.dp))
                    }
                    
                    // Both can view reports
                    Text("Daily Reports History", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                }
            }
            
            selectedBookingId?.let {
                if (reports.isEmpty()) {
                    item {
                        Text("No reports submitted yet")
                    }
                } else {
                    items(reports.size) { i ->
                        val report = reports[i]
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(
                                    "Report #${i + 1}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(4.dp))
                                Text("Meals: ${report.mealsTaken}, Walks: ${report.walksCompleted}")
                                Text("Play Time: ${report.playTimeMinutes} min")
                                Text("Bathroom: ${report.bathroomBreaks} times")
                                Text("Mood: ${report.mood}")
                                if (report.notes.isNotBlank()) {
                                    Spacer(Modifier.height(4.dp))
                                    Text("Notes: ${report.notes}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
