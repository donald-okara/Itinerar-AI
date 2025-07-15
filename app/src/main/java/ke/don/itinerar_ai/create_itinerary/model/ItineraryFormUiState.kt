package ke.don.itinerar_ai.create_itinerary.model

import kotlinx.serialization.Serializable

data class ItineraryFormUiState(
    val posterUrl: String? = null,
    val title: String? = null,
    val description: String? = null,
    val isGeneratingDescription: Boolean = false,
    val descriptionIsError: Boolean = false,
    val descriptionErrorMessage: String? = null,
    val itinerary: List<ItineraryItem> = emptyList(),
    val itineraryItem: ItineraryItem? = null,
    val isGeneratingItinerary: Boolean = false,
    val itineraryIsError: Boolean = false,
    val itineraryErrorMessage: String? = null,
)

@Serializable
data class ItineraryItem(
    val id: String? = null,
    val title: String = "",
    val isLocked: Boolean = false,
    val isGenerated: Boolean = false,
)

@Serializable
data class InsertionSuggestion(
    val id: String,
    val title: String,
    val isLocked: Boolean,
    val isGenerated: Boolean,
    val position: Int
)

