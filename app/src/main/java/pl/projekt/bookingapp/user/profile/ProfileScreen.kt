// ProfileScreen.kt
package pl.projekt.bookingapp.user.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import pl.projekt.bookingapp.data.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Lokalny stan pól po wczytaniu użytkownika
    var firstName by remember(uiState.user) { mutableStateOf(uiState.user?.firstName ?: "") }
    var lastName by remember(uiState.user) { mutableStateOf(uiState.user?.lastName ?: "") }
    var email by remember(uiState.user) { mutableStateOf(uiState.user?.email ?: "") }
    var phone by remember(uiState.user) { mutableStateOf(uiState.user?.phoneNumber ?: "") }

    // Pokazanie komunikatu o zapisaniu
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Profil został zapisany")
            }
            viewModel.onSavedMessageShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Profil") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text("Imię") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("Nazwisko") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = {},
                        label = { Text("Email") },
                        singleLine = true,
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Telefon") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    uiState.error?.let { err ->
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    Button(
                        onClick = {
                            // Uaktualniamy profil przez VM
                            uiState.user?.let { current ->
                                val updated = current.copy(
                                    firstName = firstName,
                                    lastName = lastName,
                                    phoneNumber = phone
                                )
                                viewModel.updateProfile(updated)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Zapisz zmiany")
                    }

                    TextButton(
                        onClick = {
                            viewModel.logout {
                                onLogout()
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Wyloguj się")
                    }
                }
            }
        }
    }
}
