package com.example.barbershop

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barbershop.network.AvailabilityDto
import com.example.barbershop.network.NetworkClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OpeningHoursViewModel : ViewModel() {
    private val api = NetworkClient.availabilityApi
    private val _availabilities = MutableStateFlow<List<AvailabilityDto>>(emptyList())
    val availabilities: StateFlow<List<AvailabilityDto>> = _availabilities.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    var currentSelectedBarberId = 1L

    fun loadAvailabilities(context: Context, barberId: Long) {
        currentSelectedBarberId = barberId
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = api.getAvailabilityByBarber(barberId)
                if (response.isSuccessful) {
                    _availabilities.value = response.body() ?: emptyList()
                } else {
                    Toast.makeText(context, "Error loading: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun saveAvailability(context: Context, day: Int, start: String, end: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val existingDay = _availabilities.value.find { it.dayOfWeek == day }

                val dto = AvailabilityDto(
                    availabilityId = existingDay?.availabilityId,
                    barberId = currentSelectedBarberId,
                    dayOfWeek = day,
                    startTime = start,
                    endTime = end
                )

                val response = if (existingDay != null && existingDay.availabilityId != null) {
                    api.updateAvailability(existingDay.availabilityId, dto)
                } else {
                    api.createAvailability(dto)
                }

                if (response.isSuccessful) {
                    val msg = if (existingDay != null) "Updated successfully" else "Added successfully"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    loadAvailabilities(context, currentSelectedBarberId)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Server error"
                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Connection error", Toast.LENGTH_SHORT).show()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteAvailability(context: Context, id: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = api.deleteAvailability(id)
                if (response.isSuccessful) {
                    Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show()
                    loadAvailabilities(context, currentSelectedBarberId)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Delete error"
                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpeningHoursScreen(
    viewModel: OpeningHoursViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val availabilities by viewModel.availabilities.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    var barberIdInput by remember { mutableStateOf("1") }
    var dayOfWeek by remember { mutableStateOf("1") }
    var startTime by remember { mutableStateOf("09:00:00") }
    var endTime by remember { mutableStateOf("17:00:00") }
    var selectedItem by remember { mutableIntStateOf(2) }

    LaunchedEffect(Unit) {
        viewModel.loadAvailabilities(context, 1L)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schedule", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Column {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background,
                    tonalElevation = 0.dp
                ) {
                    val navItemColors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = Color.Transparent
                    )

                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        selected = selectedItem == 2,
                        onClick = {
                            selectedItem = 2
                            onNavigate("admin_panel")
                        },
                        colors = navItemColors
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        selected = selectedItem == 0,
                        onClick = {
                            selectedItem = 0
                            onNavigate("home")
                        },
                        colors = navItemColors
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.DateRange, contentDescription = "Calendar") },
                        selected = selectedItem == 1,
                        onClick = {
                            selectedItem = 1
                            onNavigate("booking")
                        },
                        colors = navItemColors
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = barberIdInput,
                    onValueChange = { barberIdInput = it.filter { char -> char.isDigit() } },
                    label = { Text("Manage Barber Schedule (ID)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {
                    viewModel.loadAvailabilities(context, barberIdInput.toLongOrNull() ?: 1L)
                    dayOfWeek = "1"
                    startTime = "09:00:00"
                    endTime = "17:00:00"
                }) {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val currentDayInt = dayOfWeek.toIntOrNull() ?: -1
            val isEditingExistingDay = availabilities.any { it.dayOfWeek == currentDayInt }

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Schedule (Barber ID: ${viewModel.currentSelectedBarberId})", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = dayOfWeek,
                        onValueChange = { dayOfWeek = it.filter { char -> char.isDigit() } },
                        label = { Text("Day of week (1=Mon, 7=Sun)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = startTime, onValueChange = { startTime = it }, label = { Text("From (HH:mm:ss)") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = endTime, onValueChange = { endTime = it }, label = { Text("To (HH:mm:ss)") }, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val dayInt = dayOfWeek.toIntOrNull() ?: 1
                            viewModel.saveAvailability(context, dayInt, startTime, endTime)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        Text(if (isEditingExistingDay) "Update Hours" else "Add to Schedule")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Current Barber Schedule:", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading && availabilities.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (availabilities.isEmpty()) {
                Text("No working hours added.", color = MaterialTheme.colorScheme.error)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(availabilities) { avail ->
                        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    val dayName = when(avail.dayOfWeek) {
                                        1 -> "Monday"; 2 -> "Tuesday"; 3 -> "Wednesday"
                                        4 -> "Thursday"; 5 -> "Friday"; 6 -> "Saturday"
                                        7 -> "Sunday"; else -> "Day ${avail.dayOfWeek}"
                                    }
                                    Text(text = dayName, fontWeight = FontWeight.Bold)
                                    Text(text = "${avail.startTime} - ${avail.endTime}")
                                }
                                Row {
                                    IconButton(
                                        onClick = {
                                            dayOfWeek = avail.dayOfWeek.toString()
                                            startTime = avail.startTime
                                            endTime = avail.endTime
                                        },
                                        enabled = !isLoading
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(
                                        onClick = { avail.availabilityId?.let { viewModel.deleteAvailability(context, it) } },
                                        enabled = !isLoading
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}