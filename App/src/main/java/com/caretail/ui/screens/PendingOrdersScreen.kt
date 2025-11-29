package com.caretail.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.caretail.ui.components.BottomNavBarSitter
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun PendingOrdersScreen(nav: NavController) {
    val currentRoute = nav.currentBackStackEntryAsState().value?.destination?.route ?: ""

    Scaffold(
        bottomBar = { BottomNavBarSitter(nav = nav, currentRoute = currentRoute) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("⏳ Pending Orders", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))
            Text("No pending orders right now (demo).")
        }
    }
}
