package ke.don.itinerar_ai.create_itinerary.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.itinerar_ai.di.GeminiResult
import ke.don.itinerar_ai.di.VertexProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItineraryViewModel @Inject constructor(
    private val vertexProvider: VertexProvider
): ViewModel() {
    private val _uiState = MutableStateFlow(ItineraryFormUiState())
    val uiState: StateFlow<ItineraryFormUiState> = _uiState

    fun handleIntent(intent: ItineraryIntentHandler) {
        when (intent) {
            is ItineraryIntentHandler.UpdateTitle -> updateTitle(intent.title)
            is ItineraryIntentHandler.UpdateDescription -> updateDescription(intent.description)
            is ItineraryIntentHandler.GenerateDescription -> generateDescription()
            is ItineraryIntentHandler.GenerateItinerary -> generateItinerary()
            is ItineraryIntentHandler.AddItineraryItem -> addItineraryItem()
            is ItineraryIntentHandler.UpdateItineraryText -> updateItineraryText(intent.text)
            is ItineraryIntentHandler.UpdateItineraryItem -> updateItineraryItem()
            is ItineraryIntentHandler.MoveItem -> moveItem(intent.fromIndex, intent.toIndex)
            is ItineraryIntentHandler.RemoveItem -> removeItineraryItem(intent.item)
            is ItineraryIntentHandler.EditItem -> editItem(intent.item)
            ItineraryIntentHandler.SuggestItineraryItems -> suggestItineraryItems()
        }
    }

    fun updateUiState(newUiState: ItineraryFormUiState) {
        _uiState.update {
            newUiState
        }
    }

    fun updateTitle(title: String) {
        updateUiState(_uiState.value.copy(title = title))
    }

    fun updateDescription(description: String) {
        updateUiState(_uiState.value.copy(description = description, descriptionIsError = description.length > 500, descriptionErrorMessage = if (description.length > 500) "Description is too long" else null))
    }

    fun editItem(item: ItineraryItem){
        updateUiState(_uiState.value.copy(itineraryItem = item))
    }

    fun updateItineraryText(text: String) {
        val currentItem = uiState.value.itineraryItem
        val itineraryItem = if(currentItem == null) ItineraryItem() else currentItem
        updateUiState(_uiState.value.copy(itineraryItem = itineraryItem.copy(title = text, isGenerated = false)))
    }

    fun addItineraryItem(){
        val updatedList = _uiState.value.itinerary.toMutableList().apply {
            add(ItineraryItem(
                id = _uiState.value.itinerary.size.toString(),
                title = _uiState.value.itineraryItem?.title.orEmpty(),
                isLocked = true,
            ))
        }

        updateUiState(
            _uiState.value.copy(
                itinerary = updatedList,
                itineraryItem = null
            )
        )
    }

    fun updateItineraryItem(){
        val itineraryItem = uiState.value.itineraryItem
        val itineraryList = uiState.value.itinerary

        itineraryItem?.let {
            val updatedList = itineraryList.map { existing ->
                if (existing.id == itineraryItem.id) itineraryItem else existing
            }

            updateUiState(
                _uiState.value.copy(
                    itineraryItem = null,
                    itinerary = updatedList
                )
            )
        }

    }

    fun removeItineraryItem(item: ItineraryItem){
        val updatedList = uiState.value.itinerary - item

        updateUiState(
            _uiState.value.copy(
                itinerary = updatedList,
                itineraryItem = null
            )
        )
    }

    fun generateDescription() {
        viewModelScope.launch {
            updateUiState(
                _uiState.value.copy(
                    isGeneratingDescription = true,
                    descriptionIsError = false,
                    descriptionErrorMessage = null,
                    description = "" // optionally clear
                )
            )

            uiState.value.title?.let {
                vertexProvider.generateDescription(it).collect { result ->
                    when (result) {
                        is GeminiResult.Loading -> {
                            // Already handled above – ignore if redundant
                        }

                        is GeminiResult.Success -> {
                            // Append if you want streaming effect
                            val updated = _uiState.value.description.orEmpty() + result.data
                            updateUiState(_uiState.value.copy(description = updated, isGeneratingDescription = false))
                        }

                        is GeminiResult.Error -> {
                            updateUiState(
                                _uiState.value.copy(
                                    isGeneratingDescription = false,
                                    descriptionIsError = true,
                                    descriptionErrorMessage = "🔥 Failed to generate content"
                                )
                            )
                        }
                    }
                }
            }

            // ✅ Mark done after stream ends (and no error occurred)
            updateUiState(_uiState.value.copy(isGeneratingDescription = false))
        }
    }

    fun generateItinerary(){
        viewModelScope.launch {
            updateUiState(
                _uiState.value.copy(
                    itinerary = emptyList(),
                    isGeneratingItinerary = true,
                    itineraryIsError = false,
                    itineraryErrorMessage = null,
                )
            )
            uiState.value.title?.let {
                uiState.value.description?.let { it1 ->
                    vertexProvider.generateItineraryItems(title = it, description = it1)
                }?.collect { result ->
                    when (result) {
                        is GeminiResult.Loading -> {
                            // Already handled above – ignore if redundant
                        }

                        is GeminiResult.Success -> {
                            // Append if you want streaming effect
                            val updated = _uiState.value.itinerary + result.data
                            updateUiState(_uiState.value.copy(
                                itinerary = updated,
                                isGeneratingItinerary = false
                            ))
                        }

                        is GeminiResult.Error -> {
                            updateUiState(
                                _uiState.value.copy(
                                    isGeneratingItinerary = false,
                                    itineraryIsError = true,
                                    itineraryErrorMessage = "🔥 Failed to generate content"
                                )
                            )
                        }
                    }

                }
            }
        }
    }

    fun suggestItineraryItems(){
        viewModelScope.launch {
            updateUiState(
                _uiState.value.copy(
                    isGeneratingItinerary = true,
                    itineraryIsError = false,
                    itineraryErrorMessage = null
                    )
            )

            val itinerary = uiState.value.itinerary
            val title = uiState.value.title
            val description = uiState.value.description

            if (itinerary.isNotEmpty() && !title.isNullOrBlank() && !description.isNullOrBlank()) {
                val result = vertexProvider.insertItineraryItems(
                    title = title,
                    description = description,
                    itineraryList = itinerary
                )

                result.collect {
                    when (it) {
                        is GeminiResult.Loading -> {

                        }
                        is GeminiResult.Error -> {
                            updateUiState(
                                _uiState.value.copy(
                                    isGeneratingItinerary = false,
                                    itineraryIsError = true,
                                    itineraryErrorMessage = "🔥 Failed to generate content"
                                )
                            )
                        }
                        is GeminiResult.Success -> {
                            updateUiState(
                                _uiState.value.copy(
                                    isGeneratingItinerary = false
                                )
                            )

                            insertSuggestedItems(it.data)
                        }
                    }
                }

            }
        }
    }

    fun insertSuggestedItems(suggestions: List<InsertionSuggestion>) {
        // Step 1: Untoggle previous AI-generated flags
        val updatedList = _uiState.value.itinerary.map {
            if (it.isGenerated) it.copy(isGenerated = false) else it
        }.toMutableList()

        // Step 2: Sort suggestions by intended position
        val sorted = suggestions.sortedBy { it.position }

        // Step 3: Insert suggestions with smart offset
        var offset = 0
        for (i in sorted.indices) {
            val suggestion = sorted[i]
            val insertPosition = (suggestion.position + offset).coerceIn(0, updatedList.size)
            updatedList.add(insertPosition, suggestion.toItineraryItem())

            // If this is the last one, skip comparison
            if (i < sorted.lastIndex) {
                val current = suggestion.position
                val next = sorted[i + 1].position

                // Only increment offset if the next is not consecutive
                if (next != current + 1) {
                    offset++
                }
            }
        }

        // Step 4: Push new state
        updateUiState(_uiState.value.copy(itinerary = updatedList))
    }

    // Optional: convert suggestion to regular item
    private fun InsertionSuggestion.toItineraryItem(): ItineraryItem {
        return ItineraryItem(
            id = id,
            title = title,
            isGenerated = isGenerated,
            isLocked = isLocked
        )
    }


    fun moveItem(fromIndex: Int, toIndex: Int) {
        val updatedList = _uiState.value.itinerary.toMutableList().apply {
            add(toIndex, removeAt(fromIndex))
        }

        updateUiState(
            _uiState.value.copy(itinerary = updatedList)
        )
    }

}