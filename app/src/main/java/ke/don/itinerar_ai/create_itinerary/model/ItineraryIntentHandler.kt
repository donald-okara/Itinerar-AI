package ke.don.itinerar_ai.create_itinerary.model

sealed class ItineraryIntentHandler {
    data class UpdateTitle(val title: String) : ItineraryIntentHandler()
    data class UpdateDescription(val description: String) : ItineraryIntentHandler()
    data class UpdateItineraryText(val text: String) : ItineraryIntentHandler()
    data class MoveItem(val fromIndex: Int, val toIndex: Int) : ItineraryIntentHandler()
    data class RemoveItem(val item: ItineraryItem) : ItineraryIntentHandler()
    data class EditItem(val item: ItineraryItem) : ItineraryIntentHandler()

    data object UpdateItineraryItem : ItineraryIntentHandler()
    data object GenerateDescription : ItineraryIntentHandler()
    data object AddItineraryItem : ItineraryIntentHandler()
    data object GenerateItinerary : ItineraryIntentHandler()
    data object SuggestItineraryItems : ItineraryIntentHandler()
}