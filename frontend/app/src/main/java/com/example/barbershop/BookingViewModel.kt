package com.example.barbershop.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barbershop.network.AppointmentApi
import com.example.barbershop.network.AppointmentRequest
import com.example.barbershop.network.BarberApi
import com.example.barbershop.network.BarberDto
import com.example.barbershop.network.NetworkClient
import com.example.barbershop.network.ServiceApi
import com.example.barbershop.network.ServiceDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import android.util.Log
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class Barber(val id: Long, val name: String, val specialization: String)
data class Service(val id: Long, val name: String, val price: String, val durationMinutes: Int)

data class BookingUiState(
    val availableServices: List<Service> = emptyList(),
    val selectedService: Service? = null,

    val availableBarbers: List<Barber> = emptyList(),
    val selectedBarber: Barber? = null,

    val selectedDate: LocalDate? = null,
    val selectedTime: LocalTime? = null,

    val isLoading: Boolean = false,
    val isBookingSuccessful: Boolean = false,
    val errorMessage: String? = null
)

class BookingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }

            Log.d("BookingViewModel", "Starting data load...")

            try {
                Log.d("BookingViewModel", "Fetching barbers...")
                val barbersResponse = NetworkClient.barberApi.getBarbers()
                Log.d("BookingViewModel", "Barbers response code: ${barbersResponse.code()}")

                Log.d("BookingViewModel", "Fetching services...")
                val servicesResponse = NetworkClient.serviceApi.getServices()
                Log.d("BookingViewModel", "Services response code: ${servicesResponse.code()}")

                if (barbersResponse.isSuccessful && servicesResponse.isSuccessful) {
                    val barbers = barbersResponse.body()?.map { it.toBarber() } ?: emptyList()
                    val services = servicesResponse.body()?.map { it.toService() } ?: emptyList()

                    Log.d("BookingViewModel", "Barbers count: ${barbers.size}, Services count: ${services.size}")

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            availableBarbers = barbers,
                            availableServices = services
                        )
                    }
                } else {
                    Log.e("BookingViewModel", "API error - barbers: ${barbersResponse.code()}, services: ${servicesResponse.code()}")
                    val errorMessage = if (barbersResponse.code() == 401 || barbersResponse.code() == 403 || servicesResponse.code() == 401 || servicesResponse.code() == 403) {
                        "You must be logged in to view available services"
                    } else {
                        "Failed to load data"
                    }
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = errorMessage)
                    }
                }
            } catch (e: Exception) {
                Log.e("BookingViewModel", "Connection error: ${e.message}", e)
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Connection error: ${e.message}")
                }
            }
        }
    }

    private fun BarberDto.toBarber() = Barber(
        id = barberId,
        name = name,
        specialization = specialization ?: ""
    )

    private fun ServiceDto.toService() = Service(
        id = serviceId,
        name = name,
        price = "$${price.toInt()}",
        durationMinutes = durationMinutes
    )

    fun onServiceSelected(service: Service) {
        _uiState.update { it.copy(selectedService = service) }
    }

    fun onBarberSelected(barber: Barber) {
        _uiState.update { it.copy(selectedBarber = barber) }
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
    }

    fun onTimeSelected(time: LocalTime) {
        _uiState.update { it.copy(selectedTime = time) }
    }

    fun confirmBooking() {
        val currentState = _uiState.value
        if (currentState.selectedService == null ||
            currentState.selectedBarber == null ||
            currentState.selectedDate == null ||
            currentState.selectedTime == null) {
            _uiState.update { it.copy(errorMessage = "Fill in all fields") }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val dateTime = LocalDateTime.of(
                    currentState.selectedDate!!,
                    currentState.selectedTime!!
                ).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

                val response = NetworkClient.appointmentApi.createAppointment(
                    AppointmentRequest(
                        barberId = currentState.selectedBarber.id,
                        serviceIds = listOf(currentState.selectedService.id),
                        startTime = dateTime
                    )
                )

                if (response.isSuccessful) {
                    _uiState.update {
                        it.copy(isLoading = false, isBookingSuccessful = true)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("BookingViewModel", "Booking failed: ${response.code()} - $errorBody")
                    val errorMessage = when (response.code()) {
                        401, 403 -> "You must be logged in to make a booking"
                        else -> {
                            if (!errorBody.isNullOrEmpty() && errorBody.contains("message")) {
                                try {
                                    org.json.JSONObject(errorBody).optString("message", "Booking failed")
                                } catch (e: Exception) {
                                    "Booking failed"
                                }
                            } else {
                                "Booking failed"
                            }
                        }
                    }
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = errorMessage)
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Connection error: ${e.message}")
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun resetBookingSuccess() {
        _uiState.update { it.copy(isBookingSuccessful = false) }
    }
}