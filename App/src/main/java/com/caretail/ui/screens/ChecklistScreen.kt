package com.caretail.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.caretail.data.BookingStatus
import com.caretail.viewmodel.AuthViewModel
import com.caretail.viewmodel.CareTailViewModel

@Composable
fun ChecklistScreen(
    nav: NavController,
    authViewModel: AuthViewModel = viewModel(),
    careTailViewModel: CareTailViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val bookings by careTailViewModel.bookings.collectAsState()
    val checklist by careTailViewModel.checklist.collectAsState()
    
    var selectedBookingId by remember { mutableStateOf<String?>(null) }
    var newItemLabel by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            if (user.getRoleEnum() == com.caretail.data.Role.SITTER) {
                careTailViewModel.loadSitterBookings(user.id)
            } else {
                careTailViewModel.loadOwnerBookings(user.id)
            }
        }
    }
    
    LaunchedEffect(selectedBookingId) {
        selectedBookingId?.let { bookingId ->
            careTailViewModel.loadChecklist(bookingId)
        }
    }
    
    val activeBookings = bookings.filter {
        it.getStatusEnum() == BookingStatus.CONFIRMED || it.getStatusEnum() == BookingStatus.IN_PROGRESS
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Care Checklist") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (currentUser?.getRoleEnum() == com.caretail.data.Role.OWNER && selectedBookingId != null) {
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Filled.Add, "Add Task")
                        }
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
            if (activeBookings.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No active bookings")
                }
            } else {
                Text("Select Booking:", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                
                activeBookings.forEach { booking ->
                    FilterChip(
                        selected = selectedBookingId == booking.id,
                        onClick = { selectedBookingId = booking.id },
                        label = { Text("Booking ${booking.id.take(8)}") },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                
                selectedBookingId?.let {
                    Divider(Modifier.padding(vertical = 16.dp))
                    
                    if (checklist.isEmpty()) {
                        Text("No tasks yet. Add some tasks to get started!")
                    } else {
                        LazyColumn {
                            items(checklist.size) { i ->
                                val item = checklist[i]
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = item.isCompleted,
                                        onCheckedChange = { checked ->
                                            currentUser?.let { user ->
                                                careTailViewModel.toggleChecklistItem(
                                                    itemId = item.id,
                                                    bookingId = it,
                                                    isCompleted = checked,
                                                    userId = user.id
                                                ) { _, _ -> }
                                            }
                                        }
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            item.label,
                                            style = if (item.isCompleted) {
                                                MaterialTheme.typography.bodyLarge.copy(
                                                    textDecoration = TextDecoration.LineThrough
                                                )
                                            } else {
                                                MaterialTheme.typography.bodyLarge
                                            }
                                        )
                                        if (item.description.isNotBlank()) {
                                            Text(
                                                item.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
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
    
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Task") },
            text = {
                OutlinedTextField(
                    value = newItemLabel,
                    onValueChange = { newItemLabel = it },
                    label = { Text("Task Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newItemLabel.isNotBlank() && selectedBookingId != null) {
                            careTailViewModel.addChecklistItem(
                                bookingId = selectedBookingId!!,
                                label = newItemLabel,
                                description = ""
                            ) { success, _ ->
                                if (success) {
                                    newItemLabel = ""
                                    showAddDialog = false
                                }
                            }
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false; newItemLabel = "" }) {
                    Text("Cancel")
                }
            }
        )
    }
}
