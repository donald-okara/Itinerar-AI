package ke.don.itinerar_ai.create_itinerary.model

data class ItineraryFormUiState(
    val posterUrl: String? = null,
    val title: String? = null,
    val description: String? = null,
    val isGeneratingDescription: Boolean = false,
    val descriptionIsError: Boolean = false,
    val descriptionErrorMessage: String? = null,
)
