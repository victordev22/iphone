package org.example.project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// This is the data class that was implied in your previous code,
// but is necessary for the DropdownMenuV composable to work.
data class MenuItemData(val text: String, val icon: ImageVector)

/**
 * Your DropdownMenuV composable function.
 * I've included it here for a complete, runnable example.
 */
@Composable
fun DropdownMenuV() {
    var expanded by remember { mutableStateOf(false) }

    // List of menu items with associated icons
    val menuItems = listOf(
        MenuItemData("Edit", Icons.Filled.Edit),
        MenuItemData("Settings", Icons.Filled.Settings),
        MenuItemData("Delete", Icons.Filled.Delete)
    )

    // State to hold the currently selected item, initialized to the first item
    var selectedItem by remember { mutableStateOf(menuItems[0]) }

    Box(
        contentAlignment = Alignment.TopCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { expanded = true }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = selectedItem.icon,
                        contentDescription = selectedItem.text,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(selectedItem.text)
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                menuItems.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item.text) },
                        leadingIcon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.text
                            )
                        },
                        onClick = {
                            selectedItem = item
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}