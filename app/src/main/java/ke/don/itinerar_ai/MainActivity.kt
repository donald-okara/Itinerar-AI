package ke.don.itinerar_ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import ke.don.itinerar_ai.create_itinerary.model.ItineraryViewModel
import ke.don.itinerar_ai.create_itinerary.screen.ItineraryApp
import ke.don.itinerar_ai.create_itinerary.screen.ItineraryForm
import ke.don.itinerar_ai.ui.theme.ItinerarAITheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ItinerarAITheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ItineraryApp(
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ItinerarAITheme {
        Greeting("Android")
    }
}