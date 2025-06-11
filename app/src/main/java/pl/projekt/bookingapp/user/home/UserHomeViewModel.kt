package pl.projekt.bookingapp.user.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.projekt.bookingapp.data.model.Business // <-- TA LINIA MUSI TU BYĆ
import pl.projekt.bookingapp.data.repository.BusinessRepository
import javax.inject.Inject

data class UserHomeUiState(
    val isLoading: Boolean = true,
    val businesses: List<Business> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class UserHomeViewModel @Inject constructor(
    private val businessRepository: BusinessRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserHomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadBusinesses()
    }

    fun loadBusinesses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = businessRepository.getNearbyBusinesses()
            result.onSuccess { businesses ->
                _uiState.update { it.copy(isLoading = false, businesses = businesses) }
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false, error = exception.message) }
            }
        }
    }
}