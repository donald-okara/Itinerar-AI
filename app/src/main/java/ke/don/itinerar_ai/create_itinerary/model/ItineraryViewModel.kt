package ke.don.itinerar_ai.create_itinerary.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.itinerar_ai.di.DescriptionResult
import ke.don.itinerar_ai.di.VertexProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.map
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
        updateUiState(_uiState.value.copy(description = description))
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
                        is DescriptionResult.Loading -> {
                            // Already handled above – ignore if redundant
                        }

                        is DescriptionResult.Success -> {
                            // Append if you want streaming effect
                            val updated = _uiState.value.description.orEmpty() + result.text
                            updateUiState(_uiState.value.copy(description = updated))
                        }

                        is DescriptionResult.Error -> {
                            updateUiState(
                                _uiState.value.copy(
                                    isGeneratingDescription = false,
                                    descriptionIsError = true,
                                    descriptionErrorMessage = result.message
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

}