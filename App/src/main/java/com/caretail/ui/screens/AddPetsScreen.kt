package com.caretail.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.caretail.data.PetType
import com.caretail.viewmodel.AuthViewModel
import com.caretail.viewmodel.CareTailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetScreen(
    nav: NavController,
    authViewModel: AuthViewModel = viewModel(),
    careTailViewModel: CareTailViewModel = viewModel()
) {
    val currentUser = authViewModel.currentUser.collectAsState().value
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var medicalInfo by remember { mutableStateOf("") }
    var specialNeeds by remember { mutableStateOf("") }
    var feedingSchedule by remember { mutableStateOf("") }

    var petTypeExpanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf(PetType.DOG) }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Pet") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Button(
                onClick = {
                    if (currentUser == null) return@Button

                    if (name.isBlank() || breed.isBlank() || age.isBlank()) {
                        scope.launch { snackbarHostState.showSnackbar("Please complete all required fields.") }
                        return@Button
                    }

                    careTailViewModel.addPet(
                        ownerId = currentUser.id,
                        name = name,
                        type = selectedType,
                        breed = breed,
                        age = age.toIntOrNull() ?: 0,
                        weight = weight.toDoubleOrNull() ?: 0.0,
                        color = color,
                        medicalInfo = medicalInfo,
                        specialNeeds = specialNeeds,
                        feedingSchedule = feedingSchedule
                    ) { success, message ->
                        scope.launch { snackbarHostState.showSnackbar(message) }
                        if (success) nav.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Add Pet")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Pet Type Dropdown
            ExposedDropdownMenuBox(
                expanded = petTypeExpanded,
                onExpandedChange = { petTypeExpanded = !petTypeExpanded }
            ) {
                OutlinedTextField(
                    value = selectedType.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pet Type") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = petTypeExpanded)
                    }
                )

                ExposedDropdownMenu(
                    expanded = petTypeExpanded,
                    onDismissRequest = { petTypeExpanded = false }
                ) {
                    PetType.values().forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.name) },
                            onClick = {
                                selectedType = type
                                petTypeExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Pet Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = breed,
                onValueChange = { breed = it },
                label = { Text("Breed") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Age (years)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Weight (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = color,
                onValueChange = { color = it },
                label = { Text("Color") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = medicalInfo,
                onValueChange = { medicalInfo = it },
                label = { Text("Medical Info") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = specialNeeds,
                onValueChange = { specialNeeds = it },
                label = { Text("Special Needs") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = feedingSchedule,
                onValueChange = { feedingSchedule = it },
                label = { Text("Feeding Schedule") },
                placeholder = { Text("e.g., Morning & Night") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(80.dp)) // Stops content from being blocked by bottom bar
        }
    }
}
