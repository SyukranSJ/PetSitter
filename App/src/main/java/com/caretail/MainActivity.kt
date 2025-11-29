package com.caretail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.caretail.ui.common.CareTailTheme
import com.caretail.ui.screens.*

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var dark by remember { mutableStateOf(false) }
            CareTailTheme(darkTheme = dark) {
                val nav = rememberNavController()
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text("CareTail 🐾") },
                            actions = {
                                Row(Modifier.padding(end = 12.dp)) {
                                    Text(if (dark) "Dark" else "Light")
                                    Spacer(Modifier.width(8.dp))
                                    Switch(
                                        checked = dark,
                                        onCheckedChange = { dark = it }
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
                        )
                    }
                ) { inner ->
                    NavHost(
                        navController = nav,
                        startDestination = "landing",
                        modifier = Modifier.padding(inner)
                    ) {
                        // 🔹 Auth & Landing screens
                        composable("landing") { LandingScreen(nav) }
                        composable("login") { LoginScreen(nav) }
                        composable("register") { RegisterScreen(nav) }

                        // 🔹 Main user flow screens
                        composable("owner_home") { OwnerHomeScreen(nav) }
                        composable("sitter_home") { SitterHomeScreen(nav) }
                        composable("booking") { BookingScreen(nav) }
                        composable("checklist") { ChecklistScreen(nav) }
                        composable("daily_report") { DailyReportScreen(nav) }

                        // 🔹 Role selection & new profile screen
                        composable("role_select") { RoleSelectScreen(nav) }
                        composable("owner_profile") { OwnerProfileScreen(nav) }

                        //PetSitter Nav
                        composable("pending_orders") { PendingOrdersScreen(nav) }
                        composable("assigned_pets") { AssignedPetsScreen(nav) }
                        composable("sitter_profile") { SitterProfileScreen(nav) }


                    }
                }
            }
        }
    }
}
