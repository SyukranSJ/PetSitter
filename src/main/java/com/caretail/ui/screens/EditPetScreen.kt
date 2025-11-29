package com.caretail.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.caretail.data.PetType
import com.caretail.viewmodel.AuthViewModel
import com.caretail.viewmodel.CareTailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPetScreen(
    nav: NavController,
    petId: String,
    authViewModel: AuthViewModel = viewModel(),
    careTailViewModel: CareTailViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()

    val currentUser = authViewModel.currentUser.collectAsState().value
    val pet = careTailViewModel.getPetById(petId)

    if (pet == null) {
        Text("Pet not found.")
        return
    }

    // Pre-filled editable fields
    var name by remember { mutableStateOf(pet.name) }
    var breed by remember { mutableStateOf(pet.breed) }
    var age by remember { mutableStateOf(pet.age.toString()) }
    var weight by remember { mutableStateOf(pet.weight.toString()) }
    var color by remember { mutableStateOf(pet.color) }
    var medicalInfo by remember { mutableStateOf(pet.medicalInfo) }
    var specialNeeds by remember { mutableStateOf(pet.specialNeeds) }
    var feedingSchedule by remember { mutableStateOf(pet.feedingSchedule) }

    var typeExpanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf(pet.getTypeEnum()) }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Pet") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },

                actions = {
                    IconButton(onClick = {
                        careTailViewModel.deletePet(
                            petId = pet.id,
                            ownerId = pet.ownerId
                        ) { success, msg ->
                            scope.launch { snackbarHostState.showSnackbar(msg) }
                            if (success) nav.popBackStack()
                        }
                    }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete Pet")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(18.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Type dropdown
            ExposedDropdownMenuBox(
                expanded = typeExpanded,
                onExpandedChange = { typeExpanded = !typeExpanded }
            ) {
                OutlinedTextField(
                    value = selectedType.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pet Type") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = typeExpanded
                        )
                    }
                )

                ExposedDropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = { typeExpanded = false }
                ) {
                    PetType.values().forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.name) },
                            onClick = {
                                selectedType = type
                                typeExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Pet Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = breed, onValueChange = { breed = it },
                label = { Text("Breed") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = age, onValueChange = { age = it },
                label = { Text("Age") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = weight, onValueChange = { weight = it },
                label = { Text("Weight (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = color, onValueChange = { color = it },
                label = { Text("Color") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = medicalInfo, onValueChange = { medicalInfo = it },
                label = { Text("Medical Info") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = specialNeeds, onValueChange = { specialNeeds = it },
                label = { Text("Special Needs") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = feedingSchedule, onValueChange = { feedingSchedule = it },
                label = { Text("Feeding Schedule") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val updates = mapOf(
                        "name" to name,
                        "breed" to breed,
                        "age" to (age.toIntOrNull() ?: 0),
                        "weight" to (weight.toDoubleOrNull() ?: 0.0),
                        "color" to color,
                        "medicalInfo" to medicalInfo,
                        "specialNeeds" to specialNeeds,
                        "feedingSchedule" to feedingSchedule,
                        "type" to selectedType.name
                    )

                    careTailViewModel.updatePet(
                        petId = pet.id,
                        ownerId = pet.ownerId,
                        updates = updates
                    ) { success, msg ->
                        scope.launch { snackbarHostState.showSnackbar(msg) }
                        if (success) nav.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }
        }
    }
}
