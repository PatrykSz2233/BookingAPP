package pl.projekt.bookingapp.user.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import pl.projekt.bookingapp.data.model.Business
import pl.projekt.bookingapp.data.model.Service
import pl.projekt.bookingapp.data.repository.ServiceRepository
import javax.inject.Inject

// UI state dla szczegółów firmy
data class BusinessDetailsUiState(
    val isLoading: Boolean = true,
    val business: Business? = null,
    val services: List<Service> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class BusinessDetailsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val serviceRepo: ServiceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BusinessDetailsUiState())
    val uiState: StateFlow<BusinessDetailsUiState> = _uiState.asStateFlow()

    fun load(businessId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Wczytaj dokument firmy
                val snap = firestore.collection("businesses")
                    .document(businessId)
                    .get()
                    .await()
                val biz = snap.toObject(Business::class.java)
                    ?.copy(uid = snap.id)
                    ?: throw IllegalStateException("Nie znaleziono firmy: $businessId")

                // Wczytaj usługi
                val list = serviceRepo.getServicesForBusiness(businessId)
                    .onFailure { throw it }
                    .getOrThrow()

                _uiState.update {
                    BusinessDetailsUiState(
                        isLoading = false,
                        business = biz,
                        services = list,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }
}