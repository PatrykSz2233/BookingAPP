// app/src/main/java/pl/projekt/bookingapp/user/booking/BookingViewModel.kt
package pl.projekt.bookingapp.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pl.projekt.bookingapp.data.model.Booking
import pl.projekt.bookingapp.data.repository.BookingRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject

data class BookingUiState(
    val date: LocalDate = LocalDate.now(),
    val time: Int = 9,
    val bookedHours: List<Int> = emptyList(),      // lista zajętych godzin
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val repo: BookingRepository
) : ViewModel() {
    private val _ui = MutableStateFlow(BookingUiState())
    val uiState = _ui.asStateFlow()

    fun onDateSelected(d: LocalDate) {
        _ui.update { it.copy(date = d) }
        checkAvailability(_ui.value.date, _ui.value.time, currentBiz, currentSvc)
    }
    fun onTimeSelected(h: Int) {
        _ui.update { it.copy(time = h) }
    }

    // przechowujemy aktualne biz/svc, by można było ponownie sprawdzić
    private var currentBiz = ""
    private var currentSvc = ""

    fun init(businessId: String, serviceId: String) {
        currentBiz = businessId
        currentSvc = serviceId
        checkAvailability(uiState.value.date, uiState.value.time, businessId, serviceId)
    }

    private fun checkAvailability(date: LocalDate, time: Int, biz: String, svc: String) {
        viewModelScope.launch {
            repo.getBookingsForBusinessServiceDate(biz, svc, date)
                .onSuccess { list ->
                    val hours = list.map {
                        it.bookingTime.toDate()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
                            .hour
                    }
                    _ui.update { it.copy(bookedHours = hours) }
                }
        }
    }

    fun book(biz: String, svc: String) {
        viewModelScope.launch {
            val s = uiState.value
            if (s.bookedHours.contains(s.time)) {
                _ui.update { it.copy(error = "Termin zajęty") }
                return@launch
            }
            _ui.update { it.copy(isLoading = true, error = null) }

            // Konwersja na Timestamp
            val ldt = LocalDateTime.of(s.date.year, s.date.monthValue, s.date.dayOfMonth, s.time, 0)
            val ts = Timestamp(Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant()))

            val booking = Booking(
                id = "",
                clientId = "",
                businessId = biz,
                serviceId = svc,
                bookingTime = ts,
                status = "confirmed"
            )
            repo.createBooking(booking)
                .onSuccess {
                    _ui.update { it.copy(isLoading = false, success = true) }
                }
                .onFailure { e ->
                    _ui.update { it.copy(isLoading = false, error = e.localizedMessage) }
                }
        }
    }
    fun reset() = _ui.update { it.copy(success = false) }
}
