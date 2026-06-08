package com.example.barbershop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barbershop.network.NetworkClient
import com.example.barbershop.network.ServiceDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ManageServicesViewModel : ViewModel() {
    private val _services = MutableStateFlow<List<ServiceDto>>(emptyList())
    val services: StateFlow<List<ServiceDto>> = _services.asStateFlow()

    init {
        loadServices()
    }

    private fun loadServices() {
        viewModelScope.launch {
            try {
                val response = NetworkClient.serviceApi.getServices()
                if (response.isSuccessful) {
                    _services.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addService(name: String, price: Double, duration: Int) {
        viewModelScope.launch {
            try {
                val newService = ServiceDto(
                    serviceId = 0,
                    name = name,
                    price = price,
                    durationMinutes = duration,
                    description = null,
                    isActive = true
                )
                val response = NetworkClient.serviceApi.createService(newService)

                if (response.isSuccessful) {
                    loadServices()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateService(serviceId: Long, name: String, price: Double, duration: Int) {
        viewModelScope.launch {
            try {
                val updatedService = ServiceDto(
                    serviceId = serviceId,
                    name = name,
                    price = price,
                    durationMinutes = duration,
                    description = null,
                    isActive = true
                )
                val response = NetworkClient.serviceApi.updateService(serviceId, updatedService)

                if (response.isSuccessful) {
                    loadServices()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteService(serviceId: Long) {
        viewModelScope.launch {
            try {
                val response = NetworkClient.serviceApi.deleteService(serviceId)
                if (response.isSuccessful) {
                    loadServices()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}