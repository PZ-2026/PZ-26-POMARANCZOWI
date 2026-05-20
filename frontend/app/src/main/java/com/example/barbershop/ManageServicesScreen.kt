package com.example.barbershop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

// Tymczasowy model danych
data class MockService(
    val id: Long,
    val name: String,
    val durationMinutes: Int,
    val price: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageServicesScreen(
    //dodac viewmodel
) {
    // Tymczasowy stan listy
    var services by remember {
        mutableStateOf(
            listOf(
                MockService(1, "Strzyżenie męskie", 30, 60.0),
                MockService(2, "Trymowanie brody", 20, 40.0),
                MockService(3, "Combo (Włosy + Broda)", 60, 90.0)
            )
        )
    }

    // czy okienko dodawania jest otwarte
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                    title = { Text("Services management", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Dodaj usługę")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        //Główna lista usług
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            items(services) { service ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = service.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${service.durationMinutes} min | ${service.price} zł",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = {
                            // Symulacja usuwania
                            services = services.filterNot { it.id == service.id }
                        }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Usuń",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }

    // Okno dialogowe do dodawania nowej usługi
    if (showAddDialog) {
        AddServiceDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, price, duration ->
                // Symulacja dodawania
                val newId = (services.maxOfOrNull { it.id } ?: 0L) + 1L
                services = services + MockService(newId, name, duration, price)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddServiceDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dodaj nową usługę") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nazwa usługi") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Cena (zł)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Czas trwania (minuty)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedPrice = price.replace(",", ".").toDoubleOrNull() ?: 0.0
                    val parsedDuration = duration.toIntOrNull() ?: 0

                    if (name.isNotBlank() && parsedPrice > 0 && parsedDuration > 0) {
                        onConfirm(name, parsedPrice, parsedDuration)
                    }
                }
            ) {
                Text("Dodaj")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Anuluj") }
        }
    )

}