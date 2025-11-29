package com.caretail.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.caretail.data.Repo

@Composable
fun ChecklistScreen(nav: NavController) {
    val items = remember { Repo.defaultChecklist.map { it.copy() }.toMutableStateList() }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Care Checklist", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        LazyColumn {
            items(items.size) { i ->
                ListItem(
                    headlineContent = { Text(items[i].label) },
                    trailingContent = { Checkbox(items[i].done, { items[i].done = it }) }
                )
                Divider()
            }
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = { nav.popBackStack() }) { Text("Save (demo)") }
    }
}