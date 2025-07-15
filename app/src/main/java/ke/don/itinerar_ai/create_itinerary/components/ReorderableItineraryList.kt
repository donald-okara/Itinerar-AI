package ke.don.itinerar_ai.create_itinerary.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DragIndicator
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
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
    val items = state.itinerary

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
            .height(800.dp)
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
                isSelected = isSelected || state.itineraryItem?.id == item.id,
                onRemoveItem = {handleIntent(ItineraryIntentHandler.RemoveItem(it))},
                onEditItem = {handleIntent(ItineraryIntentHandler.EditItem(it))}
            )
        }
    }
}

@Composable
private fun Item(
    modifier: Modifier = Modifier,
    item: ItineraryItem,
    isSelected: Boolean,
    onRemoveItem: (ItineraryItem) -> Unit,
    onEditItem: (ItineraryItem) -> Unit
) {
    val enabled = !isSelected
    val backgroundColor = if (isSelected)
        MaterialTheme.colorScheme.surfaceVariant
    else
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)

    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        label = "itemScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(animatedScale)
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Outlined.DragIndicator,
                contentDescription = "Drag handle",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(32.dp)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(enabled = enabled, onClick = { onEditItem(item) }) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit item",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(enabled = enabled, onClick = { onRemoveItem(item) }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Remove item",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }

                if (item.isGenerated) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Generated by AI",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun ItemPreview(
    @PreviewParameter(ThemeModeProvider::class)isDark: Boolean
) {
    ThemedPreviewTemplate(isDark) {
        Item(
            item = ItineraryItem(
                title = "Visit the Eiffel Tower",
                isGenerated = true,
                isLocked = false,
                id = "a1",
            ),
            isSelected = false,
            onRemoveItem = {},
            onEditItem = {}
        )
    }

}