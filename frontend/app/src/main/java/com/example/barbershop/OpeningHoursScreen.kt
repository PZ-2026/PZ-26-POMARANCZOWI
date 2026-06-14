package com.example.barbershop

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barbershop.network.AvailabilityDto
import com.example.barbershop.network.BarberDto
import com.example.barbershop.network.NetworkClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OpeningHoursViewModel : ViewModel() {
    private val api = NetworkClient.availabilityApi
    private val barberApi = NetworkClient.barberApi

    private val _availabilities = MutableStateFlow<List<AvailabilityDto>>(emptyList())
    val availabilities: StateFlow<List<AvailabilityDto>> = _availabilities.asStateFlow()

    private val _barbers = MutableStateFlow<List<BarberDto>>(emptyList())
    val barbers: StateFlow<List<BarberDto>> = _barbers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    var currentSelectedBarberId = 1L

    init {
        loadBarbers()
    }

    fun loadBarbers() {
        viewModelScope.launch {
            try {
                val response = barberApi.getBarbers()
                if (response.isSuccessful) {
                    _barbers.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

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
                    Toast.makeText(context, "Saved successfully", Toast.LENGTH_SHORT).show()
                    loadAvailabilities(context, currentSelectedBarberId)
                }
            } catch (e: Exception) {
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
                    loadAvailabilities(context, currentSelectedBarberId)
                }
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
    val barbers by viewModel.barbers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    var selectedBarberName by remember { mutableStateOf("Select Barber") }
    var expandedBarber by remember { mutableStateOf(false) }

    var dayOfWeek by remember { mutableStateOf("1") }
    var startTime by remember { mutableStateOf("09:00:00") }
    var endTime by remember { mutableStateOf("17:00:00") }
    var selectedItem by remember { mutableIntStateOf(2) }

    var expandedDay by remember { mutableStateOf(false) }
    val daysOfWeek = mapOf(1 to "Monday", 2 to "Tuesday", 3 to "Wednesday", 4 to "Thursday", 5 to "Friday", 6 to "Saturday", 7 to "Sunday")

    LaunchedEffect(Unit) {
        viewModel.loadAvailabilities(context, 1L)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schedule", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
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
                    NavigationBarItem(icon = { Icon(Icons.Default.Person, null) }, selected = selectedItem == 2, onClick = { selectedItem = 2; onNavigate("admin_panel") }, colors = navItemColors)
                    NavigationBarItem(icon = { Icon(Icons.Default.Home, null) }, selected = selectedItem == 0, onClick = { selectedItem = 0; onNavigate("home") }, colors = navItemColors)
                    NavigationBarItem(icon = { Icon(Icons.Default.DateRange, null) }, selected = selectedItem == 1, onClick = { selectedItem = 1; onNavigate("booking") }, colors = navItemColors)
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {

            ExposedDropdownMenuBox(
                expanded = expandedBarber,
                onExpandedChange = { expandedBarber = !expandedBarber },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedBarberName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Barber") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBarber) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedBarber,
                    onDismissRequest = { expandedBarber = false }
                ) {
                    barbers.forEach { barber ->
                        DropdownMenuItem(
                            text = { Text(barber.name) },
                            onClick = {
                                selectedBarberName = barber.name
                                expandedBarber = false
                                viewModel.loadAvailabilities(context, barber.barberId)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Schedule for: $selectedBarberName", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = expandedDay,
                        onExpandedChange = { expandedDay = !expandedDay },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = daysOfWeek[dayOfWeek.toIntOrNull()] ?: "Select Day",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Day of week") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDay) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedDay,
                            onDismissRequest = { expandedDay = false }
                        ) {
                            daysOfWeek.forEach { (key, value) ->
                                DropdownMenuItem(text = { Text(value) }, onClick = { dayOfWeek = key.toString(); expandedDay = false })
                            }
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = startTime, onValueChange = { startTime = it }, label = { Text("From (HH:mm:ss)") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = endTime, onValueChange = { endTime = it }, label = { Text("To (HH:mm:ss)") }, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.saveAvailability(context, dayOfWeek.toInt(), startTime, endTime) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        Text("Save to Schedule")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(availabilities) { avail ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(text = daysOfWeek[avail.dayOfWeek] ?: "", fontWeight = FontWeight.Bold)
                                Text(text = "${avail.startTime} - ${avail.endTime}")
                            }
                            IconButton(onClick = { viewModel.deleteAvailability(context, avail.availabilityId!!) }) {
                                Icon(Icons.Default.Delete, null, tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}