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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val homeViewModel: HomeViewModel = viewModel()
            val settingsViewModel: SettingsViewModel = viewModel()
            val loginViewModel: LoginViewModel = viewModel()
            // DODANE: Inicjalizacja RegisterViewModel
            val registerViewModel: RegisterViewModel = viewModel()

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
                            onNavigateToLogin = { navController.navigate("login") }
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
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
