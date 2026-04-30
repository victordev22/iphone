package com.example.controlh // Adjust your package name

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun SwitchWithLabel(
    modifier: Modifier = Modifier,
    title: String = "",
    initialSwitchState: Boolean = false,
    isLoading: Boolean = false,
    isEnabled: Boolean = true,
    checkedThumbColor: Color = Color.White,
    uncheckedThumbColor: Color = Color.Gray,
    checkedTrackColor: Color = Color.Red,
    uncheckedTrackColor: Color = Color.Red,
    uncheckedBorderColor: Color = Color.LightGray,
    disabledCheckedBorderColor: Color = Color.DarkGray,
    disabledUncheckedTrackColor: Color = Color.White,
    disabledCheckedTrackColor: Color = Color.LightGray,
    disabledUncheckedBorderColor: Color = Color.DarkGray,
    disabledCheckedThumbColor: Color = Color.White,
    disabledUncheckedIconColor: Color = Color.LightGray,
    disabledUncheckedThumbColor: Color = Color.LightGray,
    onCheckChanged: (Boolean, (Boolean) -> Unit) -> Unit
) {
    var isChecked by remember { mutableStateOf(initialSwitchState) }

    Row(
        modifier = modifier
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = title,
            color = if (isEnabled) Color.White else Color.LightGray,
            modifier = Modifier.padding(end = 4.dp)
        )

        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Switch(
                checked = isChecked,
                onCheckedChange = { newCheckedState ->
                    // Optimistically update the UI *before* the external operation
                    isChecked = newCheckedState // <-- THIS IS THE KEY CHANGE

                    // Now, call the external onCheckChanged.
                    // The callback will tell us if the actual operation succeeded.
                    onCheckChanged(newCheckedState) { operationSuccessful ->
                        // If the operation was NOT successful, revert the UI state
                        if (!operationSuccessful) {
                            isChecked = !newCheckedState // Revert to the previous state
                        }
                        // If it was successful, the UI already reflects newCheckedState, so nothing to do here
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = checkedThumbColor,
                    uncheckedThumbColor = uncheckedThumbColor,
                    checkedTrackColor = checkedTrackColor,
                    uncheckedTrackColor = uncheckedTrackColor,
                    uncheckedBorderColor = uncheckedBorderColor,
                    disabledCheckedThumbColor = disabledCheckedThumbColor,
                    disabledCheckedTrackColor = disabledCheckedTrackColor,
                    disabledCheckedBorderColor = disabledCheckedBorderColor,
                    disabledUncheckedThumbColor = disabledUncheckedThumbColor,
                    disabledUncheckedTrackColor = disabledUncheckedTrackColor,
                    disabledUncheckedBorderColor = disabledUncheckedBorderColor,
                    disabledUncheckedIconColor = disabledUncheckedIconColor,
                ),
                enabled = isEnabled
            )
        }
    }
}

// You can keep your @Preview functions here for easy testing of SwitchWithLabel
@Preview(showBackground = true)
@Composable
fun PreviewSwitchWithLabel() {
    SwitchWithLabel(
        title = "Enable Feature",
        initialSwitchState = true,
        onCheckChanged = { isChecked, callback ->
            // Simulate an async operation (e.g., saving setting)
            println("Feature toggle: $isChecked")
            // After your logic, call the callback to update the UI
            callback(true) // Pass true for success, false for failure
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewSwitchWithLabelLoading() {
    SwitchWithLabel(
        title = "Loading...",
        isLoading = true,
        onCheckChanged = { _, _ -> /* No action when loading */ }
    )
}