package ke.don.itinerar_ai.create_itinerary.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ke.don.itinerar_ai.R
import ke.don.itinerar_ai.create_itinerary.components.FormTextField
import ke.don.itinerar_ai.create_itinerary.components.ItineraryList
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
    val itineraryItemLength = uiState.itineraryItem?.title?.length ?: 0
    val itineraryLengthError = itineraryItemLength > 250

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Icon(
            painter = painterResource(id = R.drawable.event_list_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
            contentDescription = "app icon",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(16.dp)
                .size(32.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start,
            modifier = modifier
                .verticalScroll(rememberScrollState())
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
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Words
                ),
            )

            FormTextField(
                label = "Description",
                placeholder = " Start typing, or let AI write it for you.",
                onValueChange = { handleIntent(ItineraryIntentHandler.UpdateDescription(it)) },
                text = uiState.description.orEmpty(),
                trailingIcon = Icons.Outlined.Lightbulb,
                enabled = !uiState.isGeneratingDescription && uiState.title?.isNotBlank() == true,
                onClick = { handleIntent(ItineraryIntentHandler.GenerateDescription) },
                isError = uiState.descriptionIsError || uiState.title.isNullOrBlank(),
                maxLength = 500,
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                nameLength = uiState.description?.length ?: 0,
                showLength = true,
                singleLine = false,
                errorMessage = if(uiState.descriptionIsError) uiState.descriptionErrorMessage else if (uiState.title.isNullOrBlank() && uiState.description.isNullOrBlank()) "Title is required" else null,
                comment = animatedGeneratingText(isActive = uiState.isGeneratingDescription)
            )

            FormTextField(
                label = "Itinerary Item",
                placeholder = "Go to the mall.",
                onValueChange = { handleIntent(ItineraryIntentHandler.UpdateItineraryText(it)) },
                text = uiState.itineraryItem?.title.orEmpty(),
                trailingIcon = Icons.Outlined.Check,
                enabled = !itineraryLengthError,
                onClick = {
                    if (uiState.itineraryItem?.id == null) handleIntent(ItineraryIntentHandler.AddItineraryItem)
                    else handleIntent(ItineraryIntentHandler.UpdateItineraryItem)
                },
                isError = itineraryLengthError,
                maxLength = 250,
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                nameLength = itineraryItemLength,
                showLength = true,
                singleLine = true,
                errorMessage = if(itineraryLengthError) "Itinerary title is too long" else null,
            )

            if (uiState.itinerary.isEmpty()){
                Button(
                    enabled = uiState.description?.isNotBlank() == true && !uiState.isGeneratingItinerary,
                    onClick = { handleIntent(ItineraryIntentHandler.GenerateItinerary) }
                ) {
                    Text(text = "Generate Itinerary")
                }
            }else{
                Button(
                    enabled = uiState.description?.isNotBlank() == true && !uiState.isGeneratingItinerary,
                    onClick = { handleIntent(ItineraryIntentHandler.SuggestItineraryItems) }
                ) {
                    Text(text = "Suggest changes")
                }
            }


            Text(
                text = animatedGeneratingText(
                    "Generating itinerary",
                    isActive = uiState.isGeneratingItinerary
                ) ?: "",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .fillMaxWidth(),
            )


            if (uiState.itineraryIsError){
                Text(
                    text = uiState.itineraryErrorMessage ?: "Something went wrong",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth(),
                )
            }


            if (uiState.itinerary.isNotEmpty()) {
                ItineraryList(
                    state = uiState,
                    handleIntent = handleIntent
                )
            }

        }
    }

}

@Composable
fun animatedGeneratingText(prefix: String = "Generating", isActive: Boolean): String? {
    var dotCount by remember { mutableStateOf(0) }

    LaunchedEffect(isActive) {
        while (isActive) {
            kotlinx.coroutines.delay(500)
            dotCount = (dotCount + 1) % 4 // cycles from 0 to 3
        }
    }

    return if (isActive) prefix + ".".repeat(dotCount) else null
}
