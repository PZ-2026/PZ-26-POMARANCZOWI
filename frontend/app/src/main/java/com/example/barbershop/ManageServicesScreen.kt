package com.example.barbershop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class ServiceItem(val id: String, val name: String, val price: Double, val durationMinutes: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageServicesScreen(
    services: List<ServiceItem>,
    onAddService: () -> Unit,
    onDeleteService: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Manage Services") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddService) {
                Icon(Icons.Default.Add, contentDescription = "Add service")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            items(services) { service ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = service.name, style = MaterialTheme.typography.titleMedium)
                            Text(text = "${service.durationMinutes} mins | $${service.price}")
                        }
                        IconButton(onClick = { onDeleteService(service.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}