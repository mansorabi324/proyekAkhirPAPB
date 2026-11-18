package com.example.satuhati.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.satuhati.domain.model.CoupleStatus
import com.example.satuhati.domain.model.UserRole
import com.example.satuhati.presentation.auth.AuthViewModel
import com.example.satuhati.presentation.auth.EnterPairingCodeScreen
import com.example.satuhati.presentation.auth.LandingScreen
import com.example.satuhati.presentation.auth.LoginScreen
import com.example.satuhati.presentation.auth.RegisterScreen
import com.example.satuhati.presentation.auth.ShowPairingCodeScreen

sealed class Screen(val route: String) {
    object Landing : Screen("landing")
    object Login : Screen("login")
    object Register : Screen("register")
    object ShowCode : Screen("show_code")
    object EnterCode : Screen("enter_code")
    object MainDashboard : Screen("main_dashboard")
}

@Composable
fun SatuHatiNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier,
    onDashboardScreen: @Composable () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()

    val startDestination = when {
        uiState.currentUser == null -> Screen.Landing.route
        uiState.currentCouple?.status == CoupleStatus.CONNECTED -> Screen.MainDashboard.route
        uiState.currentUser?.role == UserRole.ISTRI -> Screen.ShowCode.route
        else -> Screen.EnterCode.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier.fillMaxSize()
    ) {
        composable(Screen.Landing.route) {
            LandingScreen(
                onNavigateToLogin = {
                    authViewModel.clearMessages()
                    navController.navigate(Screen.Login.route)
                },
                onNavigateToRegister = {
                    authViewModel.clearMessages()
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                uiState = uiState,
                onLoginClick = { email, password ->
                    authViewModel.login(email, password)
                },
                onSendEmailLinkClick = { email ->
                    authViewModel.sendEmailLink(email)
                },
                onNavigateToRegister = {
                    authViewModel.clearMessages()
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                uiState = uiState,
                onRegisterClick = { name, email, pass, confirm, role ->
                    authViewModel.register(name, email, pass, confirm, role)
                },
                onNavigateToLogin = {
                    authViewModel.clearMessages()
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        composable(Screen.ShowCode.route) {
            ShowPairingCodeScreen(
                uiState = uiState,
                onGenerateNewCode = { authViewModel.generatePairingCode() },
                onAcceptRequest = { authViewModel.acceptRequest() },
                onRejectRequest = { authViewModel.rejectRequest() },
                onConnectedNavigate = {
                    navController.navigate(Screen.MainDashboard.route) {
                        popUpTo(Screen.ShowCode.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.EnterCode.route) {
            EnterPairingCodeScreen(
                uiState = uiState,
                onSubmitCode = { code ->
                    authViewModel.submitPairingCode(code)
                },
                onConnectedNavigate = {
                    navController.navigate(Screen.MainDashboard.route) {
                        popUpTo(Screen.EnterCode.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.MainDashboard.route) {
            onDashboardScreen()
        }
    }
}
