package com.example.barbershop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeScreen(
    viewModel: EmployeeViewModel,
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var editingAppointment by remember { mutableStateOf<EmployeeAppointment?>(null) }

    //pobierz dane
    LaunchedEffect(Unit) {
        viewModel.loadEmployeeData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Barber Shop",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.logout()
                        onNavigateToLogin()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingAppointment = null
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Appointment", tint = MaterialTheme.colorScheme.onPrimary)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome back,",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // wyświetlenie imienia
            Text(
                text = if (uiState.employeeName.isNotEmpty()) uiState.employeeName else "Employee",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Manage your schedule for today.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            //obsługa stanu ładowania i błędów
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                }
            } else if (uiState.appointments.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No appointments scheduled.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(uiState.appointments) { appointment ->
                        AppointmentCard(
                            appointment = appointment,
                            onEdit = {
                                editingAppointment = appointment
                                showDialog = true
                            },
                            onDelete = { viewModel.deleteAppointment(appointment.id) },
                            onMarkCompleted = { viewModel.markAppointmentAsCompleted(appointment.id) }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AppointmentDialog(
            appointment = editingAppointment,
            onDismiss = { showDialog = false },
            onConfirm = { newAppointment ->
                if (editingAppointment == null) {
                    viewModel.addAppointment(newAppointment)
                } else {
                    viewModel.editAppointment(newAppointment)
                }
                showDialog = false
            }
        )
    }
}

@Composable
fun AppointmentCard(
    appointment: EmployeeAppointment,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMarkCompleted: () -> Unit
) {
    val isCompleted = appointment.status == "Completed"
    val cardColor = if (isCompleted) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = appointment.clientName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${appointment.date} at ${appointment.time}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = appointment.service,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = appointment.status,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isCompleted) MaterialTheme.colorScheme.primary else Color.Gray,
                    modifier = Modifier
                        .background(
                            if (isCompleted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            if (!isCompleted) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onMarkCompleted,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Mark as Completed")
                }
            }
        }
    }
}

@Composable
fun AppointmentDialog(
    appointment: EmployeeAppointment?,
    onDismiss: () -> Unit,
    onConfirm: (EmployeeAppointment) -> Unit
) {
    var clientName by remember { mutableStateOf(appointment?.clientName ?: "") }
    var date by remember { mutableStateOf(appointment?.date ?: "") }
    var time by remember { mutableStateOf(appointment?.time ?: "") }
    var service by remember { mutableStateOf(appointment?.service ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (appointment == null) "Add Appointment" else "Edit Appointment",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = clientName,
                    onValueChange = { clientName = it },
                    label = { Text("Client Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (e.g., 2026-05-10)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Time (e.g., 14:00)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = service,
                    onValueChange = { service = it },
                    label = { Text("Service") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val newAppt = EmployeeAppointment(
                                id = appointment?.id ?: System.currentTimeMillis().toString(),
                                clientName = clientName,
                                date = date,
                                time = time,
                                service = service,
                                status = appointment?.status ?: "Booked"
                            )
                            onConfirm(newAppt)
                        },
                        enabled = clientName.isNotBlank() && date.isNotBlank() && time.isNotBlank() && service.isNotBlank()
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}