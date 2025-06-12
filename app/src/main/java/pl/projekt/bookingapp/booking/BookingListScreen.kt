// app/src/main/java/pl/projekt/bookingapp/booking/BookingListScreen.kt
package pl.projekt.bookingapp.booking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pl.projekt.bookingapp.data.model.Booking
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun BookingListScreen(
    viewModel: BookingListViewModel = hiltViewModel()
) {
    // podajemy initial = emptyList(), żeby delegacja działała
    val list by viewModel.bookings.collectAsState(initial = emptyList())

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(list) { b: Booking ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* opcjonalnie: szczegóły wizyty */ }
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Firma: ${b.businessId}")
                    Text("Usługa: ${b.serviceId}")
                    // konwersja Timestamp -> LocalDateTime -> String
                    val dt = b.bookingTime.toDate()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                    Text("Termin: ${dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}")
                    Text("Status: ${b.status}")
                }
            }
        }
    }
}
