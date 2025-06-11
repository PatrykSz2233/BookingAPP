package pl.projekt.bookingapp.data.model

import com.google.firebase.Timestamp

data class Booking(
    val id: String = "",
    val clientId: String = "",
    val businessId: String = "",
    val serviceId: String = "",
    val bookingTime: Timestamp = Timestamp.now(),
    val status: String = "confirmed" // np. confirmed, completed, cancelled
)