/*
 * Copyright © 2025 Donald O. Isoe (isoedonald@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ke.don.itinerar_ai.create_itinerary.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp


@Composable
fun FormTextField(
    modifier: Modifier = Modifier,
    label: String,
    text: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    placeholder: String? = null,
    isError: Boolean = false,
    comment: String? = null,
    errorMessage: String? = null,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    showLength: Boolean = false,
    nameLength: Int = 0,
    maxLength: Int = 0,
    onClick: (() -> Unit)? = null,
    trailingIcon: ImageVector? = null,
    leadingIcon: ImageVector? = null,
    isRequired: Boolean = false, // ✅ New parameter
) {
    val counterColor = when {
        isError -> MaterialTheme.colorScheme.error
        nameLength >= maxLength - 5 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth(),
    ) {
        // Optional clickable wrapper
        OutlinedTextField(
            value = text,
            onValueChange = onValueChange,
            isError = isError,
            readOnly = readOnly,
            enabled = enabled,
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            placeholder = { Text(placeholder ?: "") },
            label = {
                Row {
                    Text(label)
                    if (isRequired) {
                        Text(
                            text = " *",
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            modifier = modifier.fillMaxWidth(),
            trailingIcon = if (onClick != null && trailingIcon != null) {
                {
                    IconButton(onClick = { if (enabled) onClick() }) {
                        Icon(imageVector = trailingIcon, contentDescription = label)
                    }
                }
            } else {
                null
            },
            leadingIcon = leadingIcon?.let {
                {
                    Icon(imageVector = it, contentDescription = label)
                }
            },
        )

        if (showLength || isError) {
            Row(
                modifier = modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isError) {
                    Text(
                        text = errorMessage.orEmpty(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = modifier.weight(0.8f),
                    )
                } else {
                    Spacer(modifier = modifier.weight(0.8f))
                }

                if (showLength) {
                    Text(
                        text = "$nameLength / $maxLength",
                        style = MaterialTheme.typography.labelSmall,
                        color = counterColor,
                        modifier = modifier.weight(0.2f),
                        textAlign = TextAlign.End,
                    )
                } else {
                    Spacer(modifier = modifier.weight(0.2f))
                }
            }
        }

        if (!comment.isNullOrEmpty()) {
            Text(
                text = comment,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = modifier
                    .fillMaxWidth(),
            )
        }
    }
}

@Preview
@Composable
fun FormTextFieldPreview(
    @PreviewParameter(ThemeModeProvider::class) isDarkTheme: Boolean,
) {
    ThemedPreviewTemplate(isDarkTheme = isDarkTheme) {
        FormTextField(
            label = "Name",
            text = "",
            onValueChange = {},
            comment = "20% discount",
            enabled = true,
            isError = false,
            errorMessage = "Name is too shortName is too shortName is too shortName is too shortName is too short",
            showLength = true,
            nameLength = 100,
            maxLength = 200,
        )
    }
}
