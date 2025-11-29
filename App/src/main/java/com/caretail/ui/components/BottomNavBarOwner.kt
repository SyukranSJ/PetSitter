package com.caretail.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController

@Composable
fun BottomNavBarOwner(nav: NavController, currentRoute: String) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == "owner_home",
            onClick = {
                if (currentRoute != "owner_home") nav.navigate("owner_home")
            },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = currentRoute == "booking",
            onClick = {
                if (currentRoute != "booking") nav.navigate("booking")
            },
            icon = { Icon(Icons.Filled.DateRange, contentDescription = "Booking") },
            label = { Text("Booking") }
        )
        NavigationBarItem(
            selected = currentRoute == "daily_report",
            onClick = {
                if (currentRoute != "daily_report") nav.navigate("daily_report")
            },
            icon = { Icon(Icons.Filled.List, contentDescription = "Reports") },
            label = { Text("Reports") }
        )
        NavigationBarItem(
            selected = currentRoute == "owner_profile",
            onClick = {
                if (currentRoute != "owner_profile") nav.navigate("owner_profile")
            },
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = { Text("Profile") }
        )
    }
}

