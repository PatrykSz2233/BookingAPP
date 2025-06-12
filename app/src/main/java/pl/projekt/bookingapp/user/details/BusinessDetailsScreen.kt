package pl.projekt.bookingapp.user.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pl.projekt.bookingapp.data.model.Service

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessDetailsScreen(
    businessId: String?,
    onServiceClick: (String) -> Unit,
    viewModel: BusinessDetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(businessId) {
        businessId?.let { viewModel.load(it) }
    }

    val uiState by viewModel.uiState.collectAsState(initial = BusinessDetailsUiState())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(uiState.business?.name ?: "Szczegóły") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator()
                uiState.error != null -> Text("Błąd: ${uiState.error}")
                else -> Column(modifier = Modifier.fillMaxSize()) {
                    uiState.business?.let { biz ->
                        Text(
                            text = biz.category,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                        Text(
                            text = biz.address,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                    Text(
                        text = "Usługi",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.services) { service ->
                            ServiceItem(service = service, onClick = { onServiceClick(service.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceItem(
    service: Service,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = service.name, style = MaterialTheme.typography.bodyLarge)
                Text(text = "${service.durationMinutes} min", style = MaterialTheme.typography.bodySmall)
            }
            Text(text = "${service.price} zł", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
