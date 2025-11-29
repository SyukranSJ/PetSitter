package com.caretail.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.caretail.viewmodel.AuthViewModel
import com.caretail.viewmodel.CareTailViewModel
import com.caretail.viewmodel.DataState
import androidx.compose.ui.Alignment

@Composable
fun OwnerPetListScreen(
    nav: NavController,
    authViewModel: AuthViewModel = viewModel(),
    careTailViewModel: CareTailViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val pets by careTailViewModel.pets.collectAsState()
    val petsState by careTailViewModel.petsState.collectAsState()

    // Load pets when user is available
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            careTailViewModel.loadPets(user.id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Pets") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { nav.navigate("owner_add_pet") }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Pet")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when (petsState) {
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
                        "Error: ${(petsState as DataState.Error).message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                else -> {
                    if (pets.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("You have no pets yet.", style = MaterialTheme.typography.titleMedium)
                        }
                    } else {
                        LazyColumn {
                            items(pets.size) { i ->
                                val pet = pets[i]

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Column(Modifier.padding(16.dp)) {

                                        // ROW: Name + Edit Button
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = pet.name,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )

                                            TextButton(
                                                onClick = {
                                                    nav.navigate("owner_edit_pet/${pet.id}")
                                                }
                                            ) {
                                                Text("Edit")
                                            }
                                        }

                                        Spacer(Modifier.height(4.dp))

                                        Text("Type: ${pet.type}")
                                        Text("Breed: ${pet.breed}")
                                        Text("Age: ${pet.age} years")
                                        Text("Color: ${pet.color}")
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
