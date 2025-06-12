package pl.projekt.bookingapp.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pl.projekt.bookingapp.data.repository.BookingRepository
import pl.projekt.bookingapp.data.model.Booking
import javax.inject.Inject

@HiltViewModel
class BookingListViewModel @Inject constructor(
    private val repo: BookingRepository
) : ViewModel() {
    private val _list = MutableStateFlow<List<Booking>>(emptyList())
    val bookings = _list.asStateFlow()

    init {
        viewModelScope.launch {
            repo.getBookingsForUser("")
                .onSuccess { _list.value = it }
        }
    }
}
