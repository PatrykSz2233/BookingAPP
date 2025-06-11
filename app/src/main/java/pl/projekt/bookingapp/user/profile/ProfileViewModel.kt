package pl.projekt.bookingapp.user.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pl.projekt.bookingapp.data.model.User
import pl.projekt.bookingapp.data.repository.AuthRepository
import pl.projekt.bookingapp.data.repository.UserRepository
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val error: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            val uid = authRepository.getCurrentUserId()
            if (uid != null) {
                userRepository.getUserProfile(uid).onSuccess { user ->
                    _uiState.update { it.copy(isLoading = false, user = user) }
                }.onFailure {
                    _uiState.update { it.copy(isLoading = false, error = "Nie udało się wczytać profilu") }
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Brak zalogowanego użytkownika") }
            }
        }
    }

    fun updateUserProfile(firstName: String, lastName: String, phone: String) {
        val currentUser = _uiState.value.user ?: return
        val updatedUser = currentUser.copy(firstName = firstName, lastName = lastName, phoneNumber = phone)
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isSaved = false) }
            userRepository.updateUserProfile(updatedUser).onSuccess {
                _uiState.update { it.copy(isLoading = false, isSaved = true, user = updatedUser) }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false, error = "Błąd zapisu") }
            }
        }
    }

    fun onSavedMessageShown() { _uiState.update { it.copy(isSaved = false) } }

    fun logout() { authRepository.logout() }
}