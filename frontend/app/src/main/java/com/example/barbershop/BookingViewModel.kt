package com.example.barbershop.booking

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalTime

// Data Models
data class Barber(val id: String, val name: String, val specialization: String)
data class Service(val id: String, val name: String, val price: String, val durationMinutes: Int)

//UI State
data class BookingUiState(
    val availableServices: List<Service> = emptyList(),
    val selectedService: Service? = null,

    val availableBarbers: List<Barber> = emptyList(),
    val selectedBarber: Barber? = null,

    val selectedDate: LocalDate? = null,
    val selectedTime: LocalTime? = null,

    val isLoading: Boolean = false,
    val isBookingSuccessful: Boolean = false
)

// ViewModel Logic
class BookingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        val services = listOf(
            Service("1", "Standard Haircut", "$140", 45),
            Service("2", "Budget Haircut", "$120", 30),
            Service("3", "Expensive Haircut", "$500", 60)
        )

        val barbers = listOf(
            Barber("1", "John Doe", "Fade Specialist"),
            Barber("2", "Mike Smith", "Beard Master"),
            Barber("3", "David Brown", "Classic Cuts")
        )

        _uiState.update {
            it.copy(
                availableServices = services,
                availableBarbers = barbers
            )
        }
    }

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
        if (currentState.selectedService != null &&
            currentState.selectedBarber != null &&
            currentState.selectedDate != null &&
            currentState.selectedTime != null) {

            _uiState.update { it.copy(isLoading = true) }
            // TODO: Send data to Spring
        }
    }
}