package com.example.barbershop.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barbershop.network.AppointmentRequest
import com.example.barbershop.network.BarberDto
import com.example.barbershop.network.NetworkClient
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
data class Service(val id: Long, val name: String, val description: String, val price: String, val durationMinutes: Int)

data class BookingUiState(
    val selectedService: Service? = null,

    val availableBarbers: List<Barber> = emptyList(),
    val selectedBarber: Barber? = null,

    val selectedDate: LocalDate? = null,
    val selectedTime: LocalTime? = null,
    val availableTimeSlots: List<LocalTime> = emptyList(),

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
                
                Log.d("BookingViewModel", "Fetching service 1...")
                val serviceResponse = NetworkClient.serviceApi.getServiceById(1L)

                if (barbersResponse.isSuccessful && serviceResponse.isSuccessful) {
                    val barbers = barbersResponse.body()?.map { it.toBarber() } ?: emptyList()
                    val service = serviceResponse.body()?.toService()

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            availableBarbers = barbers,
                            selectedService = service
                        )
                    }
                } else {
                    Log.e("BookingViewModel", "API error - barbers: ${barbersResponse.code()}, service: ${serviceResponse.code()}")
                    val errorMessage = "Failed to load data"
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

    private fun loadAvailableTimes() {
        val barberId = _uiState.value.selectedBarber?.id ?: return
        val date = _uiState.value.selectedDate ?: return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = NetworkClient.barberApi.getAvailableTimes(barberId, date.toString())
                if (response.isSuccessful) {
                    val times = response.body()?.map { LocalTime.parse(it) } ?: emptyList()
                    _uiState.update { it.copy(availableTimeSlots = times) }
                } else {
                    Log.e("BookingViewModel", "Failed to load times: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("BookingViewModel", "Error loading times", e)
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
        description = description ?: "",
        price = "$${price.toInt()}",
        durationMinutes = durationMinutes
    )

    fun onBarberSelected(barber: Barber) {
        _uiState.update { it.copy(selectedBarber = barber, selectedTime = null, availableTimeSlots = emptyList()) }
        loadAvailableTimes()
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date, selectedTime = null, availableTimeSlots = emptyList()) }
        loadAvailableTimes()
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
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Booking failed")
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
