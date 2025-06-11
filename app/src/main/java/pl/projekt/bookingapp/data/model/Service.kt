package pl.projekt.bookingapp.data.model

data class Service(
    val id: String = "",
    val businessId: String = "",
    val name: String = "",
    val durationMinutes: Int = 30,
    val price: Double = 0.0
)