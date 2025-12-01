package com.caretail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.caretail.ui.common.CareTailTheme
import com.caretail.ui.screens.*
import com.caretail.viewmodel.AuthViewModel
import com.caretail.viewmodel.CareTailViewModel

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var dark by remember { mutableStateOf(false) }
            CareTailTheme(darkTheme = dark) {
                val nav = rememberNavController()

                // 🔥 CREATE SHARED VIEWMODELS HERE (VERY IMPORTANT)
                val authViewModel: AuthViewModel = viewModel()
                val careTailViewModel: CareTailViewModel = viewModel()

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
                            }
                        )
                    }
                ) { inner ->
                    NavHost(
                        navController = nav,
                        startDestination = "landing",
                        modifier = Modifier.padding(inner)
                    ) {

                        // -------------------------
                        // AUTH SCREENS
                        // -------------------------
                        composable("landing") { LandingScreen(nav) }
                        composable("login") { LoginScreen(nav, authViewModel) }
                        composable("register") { RegisterScreen(nav, authViewModel) }

                        // -------------------------
                        // OWNER SCREENS
                        // -------------------------
                        composable("owner_home") {
                            OwnerHomeScreen(nav, authViewModel)
                        }

                        composable("owner_profile") {
                            OwnerProfileScreen(nav, authViewModel)
                        }

                        composable("owner_pet_list") {
                            OwnerPetListScreen(
                                nav = nav,
                                authViewModel = authViewModel,
                                careTailViewModel = careTailViewModel
                            )
                        }

                        composable("owner_add_pet") {
                            AddPetScreen(
                                nav = nav,
                                authViewModel = authViewModel,
                                careTailViewModel = careTailViewModel
                            )
                        }

                        // -------------------------
                        // EDIT PET (FIXED)
                        // -------------------------
                        composable("owner_edit_pet/{petId}") { backStackEntry ->
                            val petId = backStackEntry.arguments?.getString("petId") ?: ""

                            EditPetScreen(
                                nav = nav,
                                petId = petId,
                                careTailViewModel = careTailViewModel   // 🔥 USE SAME VIEWMODEL
                            )
                        }

                        // -------------------------
                        // SITTER SCREENS
                        // -------------------------
                        composable("sitter_home") {
                            SitterHomeScreen(nav, authViewModel)
                        }
                        composable("pending_orders") { PendingOrdersScreen(nav) }
                        composable("assigned_pets") { AssignedPetsScreen(nav) }
                        composable("sitter_profile") { SitterProfileScreen(nav, authViewModel) }

                        // -------------------------
                        // BOOKING / CHECKLIST / REPORTS
                        // -------------------------
                        composable("booking") {
                            BookingScreen(nav, authViewModel, careTailViewModel)
                        }
                        composable("checklist") { ChecklistScreen(nav) }
                        composable("daily_report") { DailyReportScreen(nav) }

                        // -------------------------
                        // ROLE SELECT
                        // -------------------------
                        composable("role_select") { RoleSelectScreen(nav) }
                    }
                }
            }
        }
    }
}
