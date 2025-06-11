package pl.projekt.bookingapp.data.model

data class User(
    val uid: String = "",
    val email: String = "",
    // Stare pole "name" dzielimy na imię i nazwisko
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val userType: String = "client" // "client" lub "business"
)