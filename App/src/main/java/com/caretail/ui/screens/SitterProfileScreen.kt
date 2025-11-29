package com.caretail.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.caretail.viewmodel.AuthViewModel

@Composable
fun SitterProfileScreen(
    nav: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    
    var showEditDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf("") }
    var editedPhone by remember { mutableStateOf("") }
    var editedWorkHours by remember { mutableStateOf("") }
    var editedExpertise by remember { mutableStateOf("") }
    var editedLocation by remember { mutableStateOf("") }
    var editedBio by remember { mutableStateOf("") }
    
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            editedName = user.name
            editedPhone = user.phone
            editedWorkHours = user.workHours
            editedExpertise = user.expertise
            editedLocation = user.location
            editedBio = user.bio
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Filled.Edit, "Edit Profile")
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        currentUser?.name ?: "Pet Sitter",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "🐾 Pet Sitter",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    if (currentUser?.isVerified == true) {
                        Spacer(Modifier.height(4.dp))
                        Text("✓ Verified", color = MaterialTheme.colorScheme.tertiary)
                    }
                    
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "⭐ ${currentUser?.rating ?: 0.0} (${currentUser?.totalReviews ?: 0} reviews)",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            
            Spacer(Modifier.height(20.dp))
            
            Text("Professional Information", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    InfoRow("Email", currentUser?.email ?: "")
                    InfoRow("Phone", currentUser?.phone ?: "Not set")
                    InfoRow("Location", currentUser?.location?.ifBlank { "Not set" } ?: "Not set")
                    InfoRow("Work Hours", currentUser?.workHours?.ifBlank { "Not set" } ?: "Not set")
                    InfoRow("Expertise", currentUser?.expertise?.ifBlank { "Not set" } ?: "Not set")
                }
            }
            
            if (currentUser?.bio?.isNotBlank() == true) {
                Spacer(Modifier.height(16.dp))
                Text("About Me", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        currentUser?.bio ?: "",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            Spacer(Modifier.height(20.dp))
            
            OutlinedButton(
                onClick = {
                    authViewModel.logout()
                    nav.navigate("landing") {
                        popUpTo("landing") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout", color = MaterialTheme.colorScheme.error)
            }
        }
    }
    
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Profile") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editedPhone,
                        onValueChange = { editedPhone = it },
                        label = { Text("Phone") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editedLocation,
                        onValueChange = { editedLocation = it },
                        label = { Text("Location") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editedWorkHours,
                        onValueChange = { editedWorkHours = it },
                        label = { Text("Work Hours") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editedExpertise,
                        onValueChange = { editedExpertise = it },
                        label = { Text("Expertise") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editedBio,
                        onValueChange = { editedBio = it },
                        label = { Text("Bio") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val updates = mapOf(
                            "name" to editedName,
                            "phone" to editedPhone,
                            "location" to editedLocation,
                            "workHours" to editedWorkHours,
                            "expertise" to editedExpertise,
                            "bio" to editedBio
                        )
                        authViewModel.updateProfile(updates) { success, _ ->
                            if (success) {
                                showEditDialog = false
                            }
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Text(value, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
