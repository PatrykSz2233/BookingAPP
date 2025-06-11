package pl.projekt.bookingapp.navigation

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import pl.projekt.bookingapp.auth.*
import pl.projekt.bookingapp.data.repository.AuthRepository
import pl.projekt.bookingapp.user.details.BusinessDetailsScreen
import pl.projekt.bookingapp.user.home.MainScreen
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(authRepository: AuthRepository): ViewModel() {
    val isLoggedIn = MutableStateFlow(authRepository.isUserLoggedIn())
}

@Composable
fun AppNavigation(splashViewModel: SplashViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val isLoggedIn by splashViewModel.isLoggedIn.collectAsState()
    val startDestination = if (isLoggedIn) "user_home" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { navController.navigate("user_home") { popUpTo("login") { inclusive = true } } },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegistrationSuccess = { navController.navigate("login") { popUpTo("register") { inclusive = true } } },
                onNavigateBackToLogin = { navController.popBackStack() }
            )
        }
        composable("user_home") {
            MainScreen(
                onLogout = { navController.navigate("login") { popUpTo("user_home") { inclusive = true } } },
                onBusinessClick = { businessId -> navController.navigate("business_details/$businessId") }
            )
        }
        composable(
            route = "business_details/{businessId}",
            arguments = listOf(navArgument("businessId") { type = NavType.StringType })
        ) { backStackEntry ->
            BusinessDetailsScreen(businessId = backStackEntry.arguments?.getString("businessId"))
        }
    }
}