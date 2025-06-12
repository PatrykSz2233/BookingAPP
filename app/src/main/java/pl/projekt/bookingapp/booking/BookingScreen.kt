// app/src/main/java/pl/projekt/bookingapp/user/booking/BookingScreen.kt
package pl.projekt.bookingapp.booking

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    businessId: String,
    serviceId: String,
    viewModel: BookingViewModel = hiltViewModel(),
    onBooked: () -> Unit
) {
    // Inicjalizacja rezerwacji
    LaunchedEffect(businessId to serviceId) {
        viewModel.init(businessId, serviceId)
    }

    val ui by viewModel.uiState.collectAsState()
    val ctx = LocalContext.current

    LaunchedEffect(ui.success) {
        if (ui.success) {
            onBooked()
            viewModel.reset()
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Rezerwacja") })
    }) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Data
            Text("Data: ${ui.date.format(DateTimeFormatter.ISO_DATE)}")
            Button(onClick = {
                DatePickerDialog(
                    ctx, { _, y, m, d ->
                        viewModel.onDateSelected(LocalDate.of(y, m + 1, d))
                    },
                    ui.date.year, ui.date.monthValue - 1, ui.date.dayOfMonth
                ).show()
            }) {
                Text("Wybierz datę")
            }

            // Godzina (LazyRow)
            Text("Godzina: ${ui.time}:00")
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items((8..20).toList()) { h ->
                    Button(
                        onClick = { viewModel.onTimeSelected(h) },
                        enabled = !ui.bookedHours.contains(h),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (ui.time == h) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("$h:00")
                    }
                }
            }

            if (ui.bookedHours.contains(ui.time)) {
                Text("Termin zajęty", color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { viewModel.book(businessId, serviceId) },
                enabled = !ui.isLoading && !ui.bookedHours.contains(ui.time),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (ui.isLoading) {
                    CircularProgressIndicator(
                        Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Potwierdź rezerwację")
                }
            }

            ui.error?.let { Text("Błąd: $it", color = MaterialTheme.colorScheme.error) }
        }
    }
}
