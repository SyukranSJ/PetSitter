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
import androidx.compose.ui.Alignment
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
    careTailViewModel: CareTailViewModel      // 🔥 use shared ViewModel only
) {
    val scope = rememberCoroutineScope()

    // Observe pets list
    val pets by careTailViewModel.pets.collectAsState()

    // Wait until pets load
    if (pets.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }
        return
    }

    // Find the pet
    val pet = pets.firstOrNull { it.id == petId }

    if (pet == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { Text("Pet not found.") }
        return
    }

    // Editable fields
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
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
                        Icon(Icons.Default.Delete, "Delete Pet")
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

            // Pet type dropdown
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
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded)
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

            // All form fields
            OutlinedTextField(name, { name = it }, label = { Text("Pet Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(breed, { breed = it }, label = { Text("Breed") }, modifier = Modifier.fillMaxWidth())

            OutlinedTextField(
                age, { age = it },
                label = { Text("Age") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                weight, { weight = it },
                label = { Text("Weight (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(color, { color = it }, label = { Text("Color") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(medicalInfo, { medicalInfo = it }, label = { Text("Medical Info") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(specialNeeds, { specialNeeds = it }, label = { Text("Special Needs") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(feedingSchedule, { feedingSchedule = it }, label = { Text("Feeding Schedule") }, modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.height(16.dp))

            // Save button
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
