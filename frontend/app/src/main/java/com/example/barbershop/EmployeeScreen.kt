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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.barbershop.network.AppointmentResponse
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeScreen(
    viewModel: EmployeeViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // PDF states
    val isReportLoading by viewModel.isReportLoading.collectAsState()
    val context = LocalContext.current

    val currentBarberId = uiState.barberId
    val isBarberIdLoaded = uiState.isBarberIdLoaded

    // Filter states
    var selectedStatuses by remember { mutableStateOf(setOf("BOOKED")) }
    var showTodayOnly by remember { mutableStateOf(true) }

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

            Spacer(modifier = Modifier.height(24.dp))

            // PDF generation
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "My Performance & Statistics",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Generate and download a comprehensive PDF report containing your service history and statistics.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Button(
                        onClick = { currentBarberId?.let { viewModel.downloadMyStatistics(context, it) } },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isReportLoading && currentBarberId != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isBarberIdLoaded && currentBarberId == null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        if (isReportLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generating PDF...")
                        } else if (!isBarberIdLoaded) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Loading Profile...")
                        } else if (currentBarberId == null) {
                            Icon(Icons.Default.Warning, contentDescription = "Error")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Barber Profile Missing!")
                        } else {
                            Icon(Icons.Default.Assessment, contentDescription = "PDF Report")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Download My PDF Statistics")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Schedule Filters",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Filter Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusFilterItem("Booked", "BOOKED", selectedStatuses) { updated -> selectedStatuses = updated }
                        StatusFilterItem("Done", "COMPLETED", selectedStatuses) { updated -> selectedStatuses = updated }
                        StatusFilterItem("Cancelled", "CANCELLED", selectedStatuses) { updated -> selectedStatuses = updated }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = showTodayOnly,
                            onCheckedChange = { showTodayOnly = it }
                        )
                        Text(text = "Show today only", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val filteredAppointments = uiState.appointments.filter { appointment ->
                val statusMatches = appointment.status in selectedStatuses
                val dateMatches = if (showTodayOnly) {
                    val dateTime = try { LocalDateTime.parse(appointment.startTime) } catch (e: Exception) { null }
                    dateTime?.toLocalDate() == LocalDate.now()
                } else {
                    true
                }
                statusMatches && dateMatches
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text(text = "Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                }
            } else if (filteredAppointments.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (uiState.appointments.isEmpty()) "No appointments scheduled." else "No available time slots for the selected day.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                filteredAppointments.forEach { appointment ->
                    EmployeeAppointmentCard(
                        appointment = appointment,
                        onMarkCompleted = { viewModel.markAppointmentAsCompleted(appointment.appointmentId) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun StatusFilterItem(
    label: String,
    status: String,
    selectedStatuses: Set<String>,
    onFilterChanged: (Set<String>) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = status in selectedStatuses,
            onCheckedChange = { isChecked ->
                val newSet = selectedStatuses.toMutableSet()
                if (isChecked) newSet.add(status) else newSet.remove(status)
                onFilterChanged(newSet)
            }
        )
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun EmployeeAppointmentCard(
    appointment: AppointmentResponse,
    onMarkCompleted: () -> Unit
) {
    val isCompleted = appointment.status == "COMPLETED"
    val isCancelled = appointment.status == "CANCELLED"

    val dateTime = try { LocalDateTime.parse(appointment.startTime) } catch (e: Exception) { null }
    val dateFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM", Locale.ENGLISH)
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val statusColor = when (appointment.status) {
        "COMPLETED" -> Color(0xFF2E7D32)
        "CANCELLED" -> Color(0xFFC62828)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val statusContainerColor = when (appointment.status) {
        "COMPLETED" -> Color(0xFFE8F5E9)
        "CANCELLED" -> Color(0xFFFFEBEE)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inverseOnSurface),
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
                        text = appointment.services.joinToString(", ") { it.name }.ifEmpty { "Service" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = appointment.client?.name ?: "Unknown Client",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusContainerColor,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = appointment.status,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 0.5.dp,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (dateTime != null) "${dateTime.format(dateFormatter)} @ ${dateTime.format(timeFormatter)}" else appointment.startTime,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                    )
                }

                if (!isCompleted && !isCancelled) {
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