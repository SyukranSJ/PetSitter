package com.caretail.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.caretail.data.Repo
import com.caretail.data.Role
import com.caretail.ui.components.BottomNavBarOwner

@Composable
fun OwnerProfileScreen(nav: NavController) {
    val currentUser = Repo.users.lastOrNull { it.role == Role.OWNER } ?: Repo.ownerSarah
    val currentRoute = nav.currentBackStackEntryAsState().value?.destination?.route ?: ""

    Scaffold(
        bottomBar = { BottomNavBarOwner(nav = nav, currentRoute = currentRoute) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            // Profile Avatar Placeholder (with initial)
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(120.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = currentUser.name.first().toString(),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = currentUser.name,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "🏠 Pet Owner",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(Modifier.height(24.dp))

            // Owner info / stats card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("📧 Email: owner@caretail.test")
                    Text("📞 Phone: +6011-2345678")
                    Text("🐶 Pets Registered: 2")
                    Text("📅 Bookings Made: ${Repo.bookings.count { it.pet.ownerId == currentUser.id }}")
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    nav.navigate("landing") {
                        popUpTo("landing") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }

            Spacer(Modifier.height(8.dp))
            Text(
                "Thanks for trusting CareTail to care for your furry friends 💜",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


