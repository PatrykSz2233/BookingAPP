// src/main/java/pl/projekt/bookingapp/navigation/AppNavigation.kt
package pl.projekt.bookingapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import pl.projekt.bookingapp.auth.LoginScreen
import pl.projekt.bookingapp.auth.RegisterScreen
import pl.projekt.bookingapp.booking.BookingListScreen
import pl.projekt.bookingapp.booking.BookingListViewModel
import pl.projekt.bookingapp.booking.BookingScreen
import pl.projekt.bookingapp.booking.BookingViewModel
import pl.projekt.bookingapp.ui.theme.BookingAppTheme
import pl.projekt.bookingapp.user.details.BusinessDetailsScreen
import pl.projekt.bookingapp.user.home.UserHomeScreen
import pl.projekt.bookingapp.user.home.UserHomeViewModel
import pl.projekt.bookingapp.user.profile.ProfileScreen
import pl.projekt.bookingapp.user.profile.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val backEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backEntry?.destination?.route
    val showBottomBar = currentRoute != Screens.LoginScreen.route &&
            currentRoute != Screens.RegisterScreen.route

    BookingAppTheme {
        Scaffold(
            bottomBar = { if (showBottomBar) BottomNavBar(navController) }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = if (FirebaseAuth.getInstance().currentUser != null)
                    Screens.Home.route else Screens.LoginScreen.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Screens.LoginScreen.route) {
                    LoginScreen(
                        onLoginSuccess = {
                            navController.navigate(Screens.Home.route) {
                                popUpTo(Screens.LoginScreen.route) { inclusive = true }
                            }
                        },
                        onNavigateToRegister = {
                            navController.navigate(Screens.RegisterScreen.route)
                        }
                    )
                }

                composable(Screens.RegisterScreen.route) {
                    RegisterScreen(
                        onRegistrationSuccess = { navController.popBackStack() },
                        onNavigateBackToLogin = { navController.popBackStack() }
                    )
                }

                composable(Screens.Home.route) {
                    val vm: UserHomeViewModel = hiltViewModel()
                    UserHomeScreen(
                        viewModel = vm,
                        onBusinessClick = { id ->
                            navController.navigate("${Screens.BusinessDetails.route}/$id")
                        }
                    )
                }

                composable(
                    route = "${Screens.BusinessDetails.route}/{businessId}",
                    arguments = listOf(navArgument("businessId") { type = NavType.StringType })
                ) { entry ->
                    val bizId = entry.arguments?.getString("businessId") ?: return@composable
                    BusinessDetailsScreen(
                        businessId = bizId,
                        onServiceClick = { svcId ->
                            navController.navigate("booking/$bizId/$svcId")
                        }
                    )
                }

                composable(
                    route = "booking/{businessId}/{serviceId}",
                    arguments = listOf(
                        navArgument("businessId") { type = NavType.StringType },
                        navArgument("serviceId") { type = NavType.StringType }
                    )
                ) { entry ->
                    val bizId = entry.arguments?.getString("businessId") ?: return@composable
                    val svcId = entry.arguments?.getString("serviceId") ?: return@composable
                    BookingScreen(
                        businessId = bizId,
                        serviceId = svcId,
                        viewModel = hiltViewModel<BookingViewModel>(),
                        onBooked = { navController.popBackStack() }
                    )
                }

                composable(Screens.Bookings.route) {
                    val listVm: BookingListViewModel = hiltViewModel()
                    BookingListScreen(viewModel = listVm)
                }

                composable(Screens.Profile.route) {
                    val vm: ProfileViewModel = hiltViewModel()
                    ProfileScreen(
                        viewModel = vm,
                        onLogout = {
                            navController.navigate(Screens.LoginScreen.route) {
                                popUpTo(0)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomNavBar(navController: NavHostController) {
    val items = listOf(Screens.Home, Screens.Bookings, Screens.Profile)
    val backEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backEntry?.destination?.route

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}