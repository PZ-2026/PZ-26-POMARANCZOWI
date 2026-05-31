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
import com.example.barbershop.network.NetworkClient
import com.example.barbershop.ui.theme.BarbershopTheme
import com.example.barbershop.booking.BookingScreen
import com.example.barbershop.booking.BookingViewModel
import com.example.barbershop.booking.BookingSuccessScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NetworkClient.init(this)
        enableEdgeToEdge()
        setContent {
            val homeViewModel: HomeViewModel = viewModel()
            val settingsViewModel: SettingsViewModel = viewModel()
            val loginViewModel: LoginViewModel = viewModel()
            val registerViewModel: RegisterViewModel = viewModel()
            val bookingViewModel: BookingViewModel = viewModel()
            val forgotPasswordViewModel: ForgotPasswordViewModel = viewModel()
            val resetPasswordViewModel: ResetPasswordViewModel = viewModel()
            val adminViewModel: AdminViewModel = viewModel()
            val employeeViewModel: EmployeeViewModel = viewModel()
            val userProfileViewModel: UserProfileViewModel = viewModel()

            val settingsUiState by settingsViewModel.uiState.collectAsState()
            val navController = rememberNavController()

            BarbershopTheme(darkTheme = settingsUiState.isDarkTheme) {
                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("manage_services") {
                        ManageServicesScreen()
                    }
                    composable("manage_users") {
                        ManageUsersScreen()
                    }
                    composable("home") {
                        HomeScreen(
                            viewModel = homeViewModel,
                            onNavigateToSettings = { navController.navigate("settings") },
                            onNavigateToLogin = { navController.navigate("login") },
                            onNavigateToBooking = { navController.navigate("booking") },
                            onNavigateToProfile = { navController.navigate("profile") },
                            onNavigateToEmployeePanel = { navController.navigate("employee_panel") },
                            onNavigateToAdminPanel = { navController.navigate("admin_panel") }
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
                            onForgotPassword = { navController.navigate("forgot") },
                            onNavigateToHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("register") {
                        RegisterScreen(
                            viewModel = registerViewModel,
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToLogin = { navController.navigate("login") },
                        )
                    }
                    composable("forgot") {
                        ForgotPasswordScreen(
                            viewModel = forgotPasswordViewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable("reset_password") {
                        ResetPasswordScreen(
                            viewModel = resetPasswordViewModel,
                            onNavigateToLogin = {
                                navController.navigate("login") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("booking") {
                        BookingScreen(
                            viewModel = bookingViewModel,
                            onNavigateBack = { navController.popBackStack() },
                            onBookingSuccess = {
                                navController.navigate("booking_success") {
                                    popUpTo("home") { inclusive = false }
                                }
                            }
                        )
                    }
                    composable("booking_success") {
                        BookingSuccessScreen(
                            viewModel = bookingViewModel,
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("admin_panel") {
                        AdminScreen(
                            viewModel = adminViewModel,
                            onNavigate = { route -> navController.navigate(route) },
                            onNavigateToLogin = {
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("employee_panel") {
                        EmployeeScreen(
                            viewModel = employeeViewModel,
                            onNavigateToLogin = {
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("profile") {
                        UserProfileScreen(
                            viewModel = userProfileViewModel,
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToBooking = { navController.navigate("booking") }
                        )
                    }
                }
            }
        }
    }
}