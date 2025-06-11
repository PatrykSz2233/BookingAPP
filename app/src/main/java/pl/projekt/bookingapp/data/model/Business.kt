package pl.projekt.bookingapp.data.model

import com.google.firebase.firestore.GeoPoint

// Upewnij się, że plik zawiera DOKŁADNIE ten kod
data class Business(
    val uid: String = "",
    val name: String = "",
    val category: String = "",
    val address: String = "",
    val location: GeoPoint? = null,
    val rating: Double = 0.0,
    val imageUrl: String = ""
)