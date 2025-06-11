package pl.projekt.bookingapp.user.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import pl.projekt.bookingapp.navigation.Screens
import pl.projekt.bookingapp.user.profile.ProfileScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onLogout: () -> Unit,
    onBusinessClick: (String) -> Unit // Przyjmujemy akcję kliknięcia
) {
    // Ten wewnętrzny navController zarządza TYLKO dolnym menu
    val navController = rememberNavController()
    val bottomNavItems = listOf(Screens.Home, Screens.Bookings, Screens.Profile)

    // Scaffold jest "szkieletem" ekranu, który zawiera dolne menu
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // Ten NavHost wyświetla treść w zależności od wybranej zakładki
        NavHost(
            navController,
            startDestination = Screens.Home.route,
            Modifier.padding(innerPadding)
        ) {
            composable(Screens.Home.route) {
                // Przekazujemy akcję kliknięcia do ekranu głównego
                UserHomeScreen(onBusinessClick = onBusinessClick)
            }
            composable(Screens.Bookings.route) { Text("Moje wizyty") }
            composable(Screens.Profile.route) {
                ProfileScreen(onLogout = onLogout)
            }
        }
    }
}