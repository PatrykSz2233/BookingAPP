package pl.projekt.bookingapp.data.model

data class User(
    val uid: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val userType: String = "client"
)
