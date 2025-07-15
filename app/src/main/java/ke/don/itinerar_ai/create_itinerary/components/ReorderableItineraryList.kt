package ke.don.itinerar_ai.create_itinerary.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ke.don.itinerar_ai.create_itinerary.model.ItineraryFormUiState
import ke.don.itinerar_ai.create_itinerary.model.ItineraryIntentHandler
import ke.don.itinerar_ai.create_itinerary.model.ItineraryItem


@Composable
fun ItineraryList(
    modifier: Modifier = Modifier,
    state: ItineraryFormUiState,
    handleIntent: (ItineraryIntentHandler) -> Unit,
) {
    var items = state.itinerary

    val draggableItinerary by remember {
        derivedStateOf {
            items.size
        }
    }

    val stateList = rememberLazyListState()

    val dragDropState =
        rememberDragDropState(
            lazyListState = stateList,
            draggableItemsNum = draggableItinerary,
            onMove = { fromIndex, toIndex ->
                handleIntent(ItineraryIntentHandler.MoveItem(fromIndex, toIndex))
            }

        )

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .dragContainer(dragDropState),
        state = stateList,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(text = "Itinerary", fontSize = 30.sp)
        }

        draggableItems(items = items, dragDropState = dragDropState) { itemModifier, index, item ->
            val isSelected = index == dragDropState.draggingItemIndex
            Item(
                modifier = itemModifier,
                item = item,
                isSelected = isSelected
            )
        }
    }
}


@Composable
private fun Item(modifier: Modifier = Modifier, item: ItineraryItem, isSelected: Boolean) {
    val backgroundColor =
        if (isSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceVariant.copy(
            alpha = 0.8f
        )

    val animatedScale by animateFloatAsState(targetValue = if (isSelected) 1.05f else 1f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(animatedScale) // 👈 Safe scale
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Text(
            "${item.id} ${item.title}",
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        )
    }
}