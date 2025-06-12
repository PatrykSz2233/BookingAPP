// LoginViewModel.kt
package pl.projekt.bookingapp.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.projekt.bookingapp.data.repository.AuthRepository
import javax.inject.Inject

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(error = "Email i hasło nie mogą być puste.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            // używamy signIn(), bo tak definiuje to AuthRepository :contentReference[oaicite:0]{index=0}
            val result = authRepository.signIn(email, pass)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(isLoading = false, error = exception.message ?: "Wystąpił błąd logowania")
                }
            }
        }
    }

    fun checkIfUserIsLoggedIn() {
        if (authRepository.isUserLoggedIn()) {
            _uiState.update { it.copy(loginSuccess = true) }
        }
    }
}
