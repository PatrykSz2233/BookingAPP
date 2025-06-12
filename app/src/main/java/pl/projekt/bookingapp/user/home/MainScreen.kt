// app/src/main/java/pl/projekt/bookingapp/user/home/MainScreen.kt
package pl.projekt.bookingapp.user.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import pl.projekt.bookingapp.navigation.Screens
import pl.projekt.bookingapp.user.profile.ProfileScreen
import pl.projekt.bookingapp.booking.BookingListScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onLogout: () -> Unit,
    onBusinessClick: (String) -> Unit
) {
    val navController = rememberNavController()

    // status bar
    val sysUi = rememberSystemUiController()
    val bg = MaterialTheme.colorScheme.background
    SideEffect {
        sysUi.setStatusBarColor(bg, darkIcons = bg.luminance() > 0.5f)
    }

    val tabs = listOf(Screens.Home, Screens.Bookings, Screens.Profile)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val backEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backEntry?.destination?.route
                tabs.forEach { screen ->
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screens.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screens.Home.route) {
                UserHomeScreen(onBusinessClick = onBusinessClick)
            }
            composable(Screens.Bookings.route) {
                BookingListScreen()
            }
            composable(Screens.Profile.route) {
                ProfileScreen(onLogout = onLogout)
            }
        }
    }
}
