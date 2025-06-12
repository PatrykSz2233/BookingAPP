package pl.projekt.bookingapp.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    fun register(email: String, password: String, fullName: String, phone: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        val nameParts = fullName.trim().split(" ", limit = 2)
        val firstName = nameParts.getOrElse(0) { "" }
        val lastName = nameParts.getOrElse(1) { "" }

        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    val uid = authResult.user?.uid ?: return@addOnSuccessListener
                    val userMap = mapOf(
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "phoneNumber" to phone,
                        "email" to email,
                        "userType" to "client"
                    )
                    firestore.collection("users").document(uid)
                        .set(userMap)
                        .addOnSuccessListener {
                            _uiState.update { it.copy(isLoading = false, success = true) }
                        }
                        .addOnFailureListener { e ->
                            _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
                        }
                }
                .addOnFailureListener { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
                }
        }
    }
}
