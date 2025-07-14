package ke.don.itinerar_ai.create_itinerary.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import ke.don.itinerar_ai.ui.theme.ItinerarAITheme

class ThemeModeProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean> = sequenceOf(false, true) // false = light, true = dark
}

@Composable
fun ThemedPreviewTemplate(
    isDarkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    ItinerarAITheme(isDarkTheme) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
        ) {
            content()
        }
    }
}