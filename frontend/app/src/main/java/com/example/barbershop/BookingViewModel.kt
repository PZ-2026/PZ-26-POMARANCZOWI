package com.example.barbershop.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barbershop.network.AppointmentRequest
import com.example.barbershop.network.AppointmentResponse
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
import org.json.JSONObject

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
    val bookingResponse: AppointmentResponse? = null,
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

            try {
                val barbersResponse = NetworkClient.barberApi.getBarbers()
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
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Failed to load shop data")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Connection error: Check your internet")
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
                }
            } catch (_: Exception) { }
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
        _uiState.update { it.copy(
            selectedBarber = barber, 
            selectedTime = null, 
            availableTimeSlots = emptyList(),
            errorMessage = null 
        ) }
        loadAvailableTimes()
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(
            selectedDate = date, 
            selectedTime = null, 
            availableTimeSlots = emptyList(),
            errorMessage = null 
        ) }
        loadAvailableTimes()
    }

    fun onTimeSelected(time: LocalTime) {
        _uiState.update { it.copy(
            selectedTime = time,
            errorMessage = null 
        ) }
    }

    fun confirmBooking() {
        val currentState = _uiState.value
        if (currentState.selectedService == null ||
            currentState.selectedBarber == null ||
            currentState.selectedDate == null ||
            currentState.selectedTime == null) {
            _uiState.update { it.copy(errorMessage = "Please complete all selections") }
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
                    val appointment = response.body()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isBookingSuccessful = true,
                            bookingResponse = appointment
                        )
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("BookingViewModel", "Failure: $errorBody")
                    
                    val errorMessage = try {
                        val json = JSONObject(errorBody)
                        when {
                            json.has("message") -> json.getString("message")
                            json.has("error") -> json.getString("error")
                            else -> "Booking failed: ${response.code()}"
                        }
                    } catch (e: Exception) {
                        "Server error (${response.code()}). Please try another slot."
                    }
                    
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = errorMessage)
                    }
                }
            } catch (e: Exception) {
                Log.e("BookingViewModel", "Network Error", e)
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Network failure. Check connection.")
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun resetBookingSuccess() {
        _uiState.update { it.copy(isBookingSuccessful = false, bookingResponse = null) }
    }
}
