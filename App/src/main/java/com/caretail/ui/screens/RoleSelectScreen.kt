package com.caretail.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.caretail.data.Repo
import com.caretail.data.Role

@Composable
fun RoleSelectScreen(nav: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // 🔹 Back Button
        IconButton(onClick = { nav.popBackStack() }) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
        }

        Spacer(Modifier.height(12.dp))

        Text(
            "Select Your Role 🐾",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                // Assign role as Pet Owner
                Repo.users.lastOrNull()?.let {
                    Repo.users[Repo.users.indexOf(it)] = it.copy(role = Role.OWNER)
                }
                nav.navigate("owner_home")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("I’m a Pet Owner 🏠")
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                // Assign role as Pet Sitter
                Repo.users.lastOrNull()?.let {
                    Repo.users[Repo.users.indexOf(it)] = it.copy(role = Role.SITTER)
                }
                nav.navigate("sitter_home")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("I’m a Pet Sitter 🐶")
        }
    }
}

