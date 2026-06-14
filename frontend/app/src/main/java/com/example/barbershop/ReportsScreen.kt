package com.example.barbershop

import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.barbershop.network.NetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ReportsViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val reportApi = NetworkClient.reportApi

    fun downloadReport(context: Context, reportType: String, barberId: Long = 1L) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    when (reportType) {
                        "barber-statistics" -> reportApi.getBarberStatistics(barberId)
                        "revenue" -> reportApi.getRevenue("month")
                        "services-popularity" -> reportApi.getServicePopularity()
                        else -> throw IllegalArgumentException("Unknown report type")
                    }
                }

                if (response.isSuccessful && response.body() != null) {
                    val bytes = response.body()!!.bytes()
                    savePdf(context, bytes, "Report_${reportType}.pdf")
                } else {
                    Toast.makeText(context, "Download error (code ${response.code()})", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun savePdf(context: Context, bytes: ByteArray, fileName: String) {
        try {
            val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val file = File(dir, fileName)
            FileOutputStream(file).use { it.write(bytes) }
            Toast.makeText(context, "Report downloaded to:\n${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error saving PDF file", Toast.LENGTH_SHORT).show()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel,
    onNavigateBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current
    var barberIdInput by remember { mutableStateOf("1") }
    var selectedItem by remember { mutableIntStateOf(2) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reports Panel", fontWeight = FontWeight.Bold) },
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
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Generate PDF Reports",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = barberIdInput,
                    onValueChange = { barberIdInput = it.filter { char -> char.isDigit() } },
                    label = { Text("Barber ID (for statistics report)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        AdminMenuCard(
                            title = "Barber Statistics",
                            icon = Icons.Default.Person,
                            onClick = {
                                val id = barberIdInput.toLongOrNull() ?: 1L
                                viewModel.downloadReport(context, "barber-statistics", id)
                            }
                        )
                    }
                    item {
                        AdminMenuCard(
                            title = "Company Revenue",
                            icon = Icons.Default.ShoppingCart,
                            onClick = { viewModel.downloadReport(context, "revenue") }
                        )
                    }
                    item {
                        AdminMenuCard(
                            title = "Services Popularity",
                            icon = Icons.Default.Star,
                            onClick = { viewModel.downloadReport(context, "services-popularity") }
                        )
                    }
                }
            }

            if (isLoading) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}