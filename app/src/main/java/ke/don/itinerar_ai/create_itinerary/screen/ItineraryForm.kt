package ke.don.itinerar_ai.create_itinerary.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ke.don.itinerar_ai.create_itinerary.components.FormTextField
import ke.don.itinerar_ai.create_itinerary.model.ItineraryFormUiState
import ke.don.itinerar_ai.create_itinerary.model.ItineraryIntentHandler
import ke.don.itinerar_ai.create_itinerary.model.ItineraryViewModel

@Composable
fun ItineraryApp(
    modifier: Modifier,
){
    val viewModel:ItineraryViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val handleIntent = viewModel::handleIntent

    ItineraryForm(
        modifier = modifier,
        uiState = state,
        handleIntent = handleIntent,
    )
}

@Composable
fun ItineraryForm(
    modifier: Modifier,
    uiState: ItineraryFormUiState,
    handleIntent: (ItineraryIntentHandler) -> Unit,
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize(),
    ){
        FormTextField(
            label = "Title",
            placeholder = "Trip to Malawi",
            onValueChange = {handleIntent(ItineraryIntentHandler.UpdateTitle(it))},
            text = uiState.title.orEmpty(),
            enabled = true,
            isError = false,
        )
        FormTextField(
            label = "Description",
            placeholder = "Me and the gang blah blah",
            onValueChange = {handleIntent(ItineraryIntentHandler.UpdateDescription(it))},
            text = uiState.description.orEmpty(),
            trailingIcon = Icons.Outlined.Lightbulb,
            enabled = !uiState.isGeneratingDescription,
            onClick = { handleIntent(ItineraryIntentHandler.GenerateDescription) },
            isError = uiState.descriptionIsError,
            errorMessage = uiState.descriptionErrorMessage
        )
    }
}