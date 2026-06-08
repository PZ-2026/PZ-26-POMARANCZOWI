package com.example.barbershop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.barbershop.network.NetworkClient
import com.example.barbershop.network.TokenManager
import com.example.barbershop.ui.theme.BarbershopTheme
import com.example.barbershop.booking.BookingScreen
import com.example.barbershop.booking.BookingViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NetworkClient.init(this)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val tokenManager = remember { TokenManager(context) }
            val navController = rememberNavController()

            val factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return when {
                        modelClass.isAssignableFrom(AdminViewModel::class.java) -> AdminViewModel(tokenManager) as T
                        else -> modelClass.getDeclaredConstructor().newInstance()
                    }
                }
            }

            val settingsViewModel: SettingsViewModel = viewModel()
            val settingsUiState by settingsViewModel.uiState.collectAsState()

            BarbershopTheme(darkTheme = settingsUiState.isDarkTheme) {
                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        val homeViewModel: HomeViewModel = viewModel()
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
                        val loginViewModel: LoginViewModel = viewModel()
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
                        val registerViewModel: RegisterViewModel = viewModel()
                        RegisterScreen(
                            viewModel = registerViewModel,
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToLogin = { navController.navigate("login") },
                        )
                    }
                    composable("forgot") {
                        val forgotPasswordViewModel: ForgotPasswordViewModel = viewModel()
                        ForgotPasswordScreen(
                            viewModel = forgotPasswordViewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable("reset_password") {
                        val resetPasswordViewModel: ResetPasswordViewModel = viewModel()
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
                        val bookingViewModel: BookingViewModel = viewModel()
                        BookingScreen(
                            viewModel = bookingViewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable("admin_panel") {
                        val adminViewModel: AdminViewModel = viewModel(factory = factory)
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
                        val employeeViewModel: EmployeeViewModel = viewModel()
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
                        val userProfileViewModel: UserProfileViewModel = viewModel()
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
                    composable("manage_services") {
                        ManageServicesScreen(
                            onNavigate = { route -> navController.navigate(route) }
                        )
                    }
                    composable("manage_users") {
                        ManageUsersScreen(
                            onNavigate = { route -> navController.navigate(route) }
                        )
                    }
                }
            }
        }
    }
}