// src/main/java/pl/projekt/bookingapp/navigation/Screens.kt
package pl.projekt.bookingapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screens(val route: String, val label: String, val icon: ImageVector) {
    object Home            : Screens("home",              "Znajdź usługę", Icons.Filled.Home)
    object Bookings        : Screens("bookings",          "Moje wizyty",   Icons.Filled.Book)
    object Profile         : Screens("profile",           "Profil",        Icons.Filled.Person)
    object BusinessDetails : Screens("business_details",  "Szczegóły",     Icons.Filled.Info)
    object LoginScreen     : Screens("login",             "Logowanie",     Icons.Filled.Person)
    object RegisterScreen  : Screens("register",          "Rejestracja",   Icons.Filled.Person)
}
