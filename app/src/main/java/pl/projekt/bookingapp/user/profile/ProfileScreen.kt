package pl.projekt.bookingapp.user.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user
    val snackbarHostState = remember { SnackbarHostState() }

    // --- KLUCZOWY FRAGMENT ---
    // Te zmienne stanu `remember` przechowują to, co użytkownik wpisuje.
    // Są niezależne od `uiState` aż do momentu zapisu.
    // Klucz `user` sprawia, że resetują się, gdy dane z ViewModelu się zmienią.
    var firstName by remember(user) { mutableStateOf(user?.firstName ?: "") }
    var lastName by remember(user) { mutableStateOf(user?.lastName ?: "") }
    var phone by remember(user) { mutableStateOf(user?.phoneNumber ?: "") }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            snackbarHostState.showSnackbar("Profil zaktualizowany!")
            viewModel.onSavedMessageShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (uiState.isLoading && user == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (user != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Twój Profil", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(8.dp))
                Text("Uzupełnij dane, aby móc rezerwować wizyty.", style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(24.dp))

                // Ta lambda `onValueChange = { firstName = it }` aktualizuje stan,
                // co powoduje, że pole tekstowe się odświeża z nową wartością.
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("Imię") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Nazwisko") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Numer telefonu") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.updateUserProfile(firstName, lastName, phone) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Zapisz zmiany")
                }

                Spacer(Modifier.height(16.dp))
                OutlinedButton(
                    onClick = {
                        viewModel.logout()
                        onLogout()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Wyloguj")
                }
            }
        }
    }
}