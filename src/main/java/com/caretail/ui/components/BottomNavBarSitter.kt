package com.caretail.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController


@Composable
fun BottomNavBarSitter(nav: NavController, currentRoute: String) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == "sitter_home",
            onClick = {
                if (currentRoute != "sitter_home") nav.navigate("sitter_home")
            },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )

        NavigationBarItem(
            selected = currentRoute == "assigned_pets",
            onClick = {
                if (currentRoute != "assigned_pets") nav.navigate("assigned_pets")
            },
            icon = { Icon(Icons.Filled.Favorite, contentDescription = "Assigned Pets") },
            label = { Text("Assigned") }
        )

        NavigationBarItem(
            selected = currentRoute == "pending_orders",
            onClick = {
                if (currentRoute != "pending_orders") nav.navigate("pending_orders")
            },
            icon = { Icon(Icons.Filled.Schedule, contentDescription = "Pending Orders") },
            label = { Text("Pending") }
        )

        NavigationBarItem(
            selected = currentRoute == "sitter_profile",
            onClick = {
                if (currentRoute != "sitter_profile") nav.navigate("sitter_profile")
            },
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = { Text("Profile") }
        )
    }
}


