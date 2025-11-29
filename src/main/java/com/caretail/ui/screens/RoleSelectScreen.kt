package com.caretail.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.caretail.data.Role
import com.caretail.viewmodel.AuthViewModel

@Composable
fun RoleSelectScreen(
    nav: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    
    // Navigate to appropriate home if user already logged in
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            val destination = if (user.getRoleEnum() == Role.OWNER) {
                "owner_home"
            } else {
                "sitter_home"
            }
            nav.navigate(destination) {
                popUpTo("landing") { inclusive = true }
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Welcome to CareTail! 🐾",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(Modifier.height(16.dp))
        
        Text(
            "You're all set! Choose your role to continue:",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(Modifier.height(32.dp))
        
        Button(
            onClick = {
                nav.navigate("owner_home") {
                    popUpTo("landing") { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text("Continue as Pet Owner 🏠")
        }
        
        Spacer(Modifier.height(16.dp))
        
        OutlinedButton(
            onClick = {
                nav.navigate("sitter_home") {
                    popUpTo("landing") { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text("Continue as Pet Sitter 🐾")
        }
    }
}
