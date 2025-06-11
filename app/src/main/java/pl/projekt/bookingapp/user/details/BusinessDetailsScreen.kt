package pl.projekt.bookingapp.user.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun BusinessDetailsScreen(businessId: String?) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Szczegóły firmy o ID: $businessId")
        // Tutaj w przyszłości będzie cała logika wczytywania
        // danych o firmie i jej usługach z Firestore.
    }
}