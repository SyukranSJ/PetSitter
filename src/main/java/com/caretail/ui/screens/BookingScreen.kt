package com.caretail.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.caretail.data.Pet
import com.caretail.data.User
import com.caretail.viewmodel.AuthViewModel
import com.caretail.viewmodel.CareTailViewModel

@Composable
fun BookingScreen(
    nav: NavController,
    authViewModel: AuthViewModel = viewModel(),
    careTailViewModel: CareTailViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val pets by careTailViewModel.pets.collectAsState()
    val sitters by careTailViewModel.sitters.collectAsState()
    
    var selectedPet by remember { mutableStateOf<Pet?>(null) }
    var selectedSitter by remember { mutableStateOf<User?>(null) }
    var totalDays by remember { mutableStateOf("3") }
    var pricePerDay by remember { mutableStateOf("50") }
    var specialInstructions by remember { mutableStateOf("") }
    var isOnSite by remember { mutableStateOf(true) }
    var showMessage by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            careTailViewModel.loadPets(user.id)
            careTailViewModel.loadSitters()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book a Sitter") },
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
                Text("Select Your Pet:", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                
                if (pets.isEmpty()) {
                    Text("No pets found. Add a pet first!", color = MaterialTheme.colorScheme.error)
                } else {
                    pets.forEach { pet ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selectedPet = pet },
                            colors = if (selectedPet == pet) {
                                CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            } else {
                                CardDefaults.cardColors()
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedPet == pet,
                                    onClick = { selectedPet = pet }
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(pet.name, style = MaterialTheme.typography.titleSmall)
                                    Text("${pet.type} • ${pet.breed}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
                
                Spacer(Modifier.height(20.dp))
                Text("Select a Sitter:", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
            }
            
            items(sitters.size) { i ->
                val sitter = sitters[i]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { selectedSitter = sitter },
                    colors = if (selectedSitter == sitter) {
                        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    } else {
                        CardDefaults.cardColors()
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedSitter == sitter,
                            onClick = { selectedSitter = sitter }
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(sitter.name, style = MaterialTheme.typography.titleSmall)
                                if (sitter.isVerified) {
                                    Spacer(Modifier.width(4.dp))
                                    Text("✓", color = MaterialTheme.colorScheme.primary)
                                }
                            }
                            Text("⭐ ${sitter.rating} (${sitter.totalReviews} reviews)", style = MaterialTheme.typography.bodySmall)
                            if (sitter.location.isNotBlank()) {
                                Text("📍 ${sitter.location}", style = MaterialTheme.typography.bodySmall)
                            }
                            if (sitter.expertise.isNotBlank()) {
                                Text("Expertise: ${sitter.expertise}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
            
            item {
                Spacer(Modifier.height(20.dp))
                Text("Booking Details:", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = totalDays,
                    onValueChange = { totalDays = it },
                    label = { Text("Number of Days") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = pricePerDay,
                    onValueChange = { pricePerDay = it },
                    label = { Text("Price per Day ($)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = specialInstructions,
                    onValueChange = { specialInstructions = it },
                    label = { Text("Special Instructions") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                
                Spacer(Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilterChip(
                        selected = isOnSite,
                        onClick = { isOnSite = true },
                        label = { Text("On-Site (Sitter comes to you)") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = !isOnSite,
                        onClick = { isOnSite = false },
                        label = { Text("Off-Site (Drop off pet)") },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(Modifier.height(20.dp))
                
                val days = totalDays.toIntOrNull() ?: 0
                val price = pricePerDay.toDoubleOrNull() ?: 0.0
                val total = days * price
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Total Cost", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "$$total",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text("$days days × $$price per day")
                    }
                }
                
                showMessage?.let { message ->
                    Spacer(Modifier.height(8.dp))
                    Text(message, color = if (message.contains("Success")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                }
                
                Spacer(Modifier.height(16.dp))
                
                Button(
                    onClick = {
                        if (selectedPet != null && selectedSitter != null && currentUser != null && days > 0 && price > 0) {
                            careTailViewModel.createBooking(
                                ownerId = currentUser!!.id,
                                sitterId = selectedSitter!!.id,
                                petId = selectedPet!!.id,
                                startDate = System.currentTimeMillis(),
                                endDate = System.currentTimeMillis() + (days * 24 * 60 * 60 * 1000L),
                                totalDays = days,
                                pricePerDay = price,
                                specialInstructions = specialInstructions,
                                pickupAddress = currentUser!!.address,
                                isOnSite = isOnSite,
                                isPremium = false,
                                additionalServices = emptyList()
                            ) { success, message ->
                                showMessage = message
                                if (success) {
                                    // Navigate back after short delay
                                    nav.popBackStack()
                                }
                            }
                        } else {
                            showMessage = "Please fill in all required fields"
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedPet != null && selectedSitter != null && days > 0 && price > 0
                ) {
                    Text("Create Booking")
                }
                
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
