package ke.don.itinerar_ai.create_itinerary.model

sealed class ItineraryIntentHandler {
    data class UpdateTitle(val title: String) : ItineraryIntentHandler()
    data class UpdateDescription(val description: String) : ItineraryIntentHandler()
    data class MoveItem(val fromIndex: Int, val toIndex: Int) : ItineraryIntentHandler()

    data object GenerateDescription : ItineraryIntentHandler()
    data object GenerateItinerary : ItineraryIntentHandler()
}