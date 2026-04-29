package com.example.barbershop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.barbershop.ui.theme.BarbershopTheme
// Pamiętaj o imporcie dla ekranu rezerwacji! Jeśli Android Studio podświetli na czerwono,
// kliknij Alt+Enter na BookingScreen i BookingViewModel, aby je zaimportować.
import com.example.barbershop.booking.BookingScreen
import com.example.barbershop.booking.BookingViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val homeViewModel: HomeViewModel = viewModel()
            val settingsViewModel: SettingsViewModel = viewModel()
            val loginViewModel: LoginViewModel = viewModel()
            val registerViewModel: RegisterViewModel = viewModel()
            val bookingViewModel: BookingViewModel = viewModel()

            val settingsUiState by settingsViewModel.uiState.collectAsState()
            val navController = rememberNavController()

            BarbershopTheme(darkTheme = settingsUiState.isDarkTheme) {
                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        HomeScreen(
                            viewModel = homeViewModel,
                            onNavigateToSettings = { navController.navigate("settings") },
                            onNavigateToLogin = { navController.navigate("login") },
                            onNavigateToBooking = { navController.navigate("booking") }
                        )
                    }
                    composable("settings") {
                        SettingsScreen(
                            viewModel = settingsViewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable("login") {
                        LoginScreen(
                            viewModel = loginViewModel,
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToRegister = { navController.navigate("register") },
                            onForgotPassword = {  }
                        )
                    }
                    composable("register") {
                        RegisterScreen(
                            viewModel = registerViewModel,
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToLogin = { navController.navigate("login") },
                        )
                    }
                    composable("booking") {
                        BookingScreen(
                            viewModel = bookingViewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}