package pl.projekt.bookingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import pl.projekt.bookingapp.navigation.AppNavigation
import pl.projekt.bookingapp.ui.theme.BookingAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Używamy naszego motywu
            BookingAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // !! TO JEST KLUCZOWA ZMIANA !!
                    // Zamiast domyślnego ekranu, wywołujemy naszą nawigację.
                    AppNavigation()
                }
            }
        }
    }
}