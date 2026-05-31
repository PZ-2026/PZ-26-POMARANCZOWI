package com.example.barbershop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeScreen(
    viewModel: EmployeeViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // Load data on entry
    LaunchedEffect(Unit) {
        viewModel.loadEmployeeData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Employee Panel", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.logout(navigateToHome = onNavigateToHome) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        bottomBar = {
            Column {
                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                NavigationBar(containerColor = MaterialTheme.colorScheme.background, tonalElevation = 0.dp) {
                    val navItemColors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = Color.Transparent
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        selected = true,
                        onClick = { },
                        colors = navItemColors
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        selected = false,
                        onClick = onNavigateToHome,
                        colors = navItemColors
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        selected = false,
                        onClick = onNavigateToSettings,
                        colors = navItemColors
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Employee Info Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(32.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = uiState.employeeName.take(1).uppercase(),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = uiState.employeeName.ifEmpty { "Employee Name" },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = uiState.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (uiState.phone.isNotEmpty()) {
                        Text(
                            text = uiState.phone,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            SectionHeader("Today's Schedule")

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text(text = "Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                }
            } else if (uiState.appointments.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No appointments scheduled for today.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                uiState.appointments.forEach { appointment ->
                    AppointmentCard(
                        appointment = appointment,
                        onMarkCompleted = { viewModel.markAppointmentAsCompleted(appointment.id) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AppointmentCard(
    appointment: EmployeeAppointment,
    onMarkCompleted: () -> Unit
) {
    val isCompleted = appointment.status == "Completed"
    
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = appointment.service,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.tertiary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = appointment.clientName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = appointment.status,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.2f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.tertiary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${appointment.date} @ ${appointment.time}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }

                if (!isCompleted) {
                    Button(
                        onClick = onMarkCompleted,
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Complete", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}
