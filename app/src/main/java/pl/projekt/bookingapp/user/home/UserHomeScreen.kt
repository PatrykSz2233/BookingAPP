package pl.projekt.bookingapp.user.home

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import pl.projekt.bookingapp.data.model.Business

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UserHomeScreen(
    // Przyjmujemy akcję kliknięcia od rodzica (MainScreen)
    onBusinessClick: (String) -> Unit,
    viewModel: UserHomeViewModel = hiltViewModel()
) {
    // Kod do obsługi uprawnień lokalizacji
    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )

    LaunchedEffect(key1 = true) {
        if (!locationPermissionsState.allPermissionsGranted) {
            locationPermissionsState.launchMultiplePermissionRequest()
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    // Ten Scaffold jest tylko dla tego ekranu, aby miał swój własny TopAppBar.
    // Dolne menu jest w Scaffoldzie w MainScreen.
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Znajdź usługę") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), // Używamy paddingu od Scaffolda
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.error != null) {
                Text("Błąd: ${uiState.error}")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.businesses) { business ->
                        // Przekazujemy akcję kliknięcia do karty
                        BusinessCard(business = business, onClick = {
                            onBusinessClick(business.uid) // Wywołujemy akcję z ID firmy
                        })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessCard(
    business: Business,
    onClick: () -> Unit // Karta przyjmuje akcję do wykonania
) {
    Card(
        onClick = onClick, // Ustawiamy akcję na całej karcie
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(business.imageUrl)
                    .crossfade(true)
                    .placeholder(android.R.drawable.ic_menu_gallery) // Placeholder na czas ładowania
                    .error(android.R.drawable.ic_menu_report_image) // Obrazek w razie błędu
                    .build(),
                contentDescription = "Zdjęcie firmy ${business.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = business.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = business.category,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = business.address,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}