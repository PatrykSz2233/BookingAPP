package pl.projekt.bookingapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screens(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screens("home", "Główna", Icons.Default.Home)
    object Bookings : Screens("bookings", "Wizyty", Icons.Default.DateRange)
    object Profile : Screens("profile", "Profil", Icons.Default.Person)
}