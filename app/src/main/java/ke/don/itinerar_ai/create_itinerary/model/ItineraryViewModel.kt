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
            is ItineraryIntentHandler.MoveItem -> moveItem(intent.fromIndex, intent.toIndex)
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

    fun moveItem(fromIndex: Int, toIndex: Int) {
        val updatedList = _uiState.value.itinerary.toMutableList().apply {
            add(toIndex, removeAt(fromIndex))
        }

        updateUiState(
            _uiState.value.copy(itinerary = updatedList)
        )
    }

}