//package com.example.controlh.view
//
//import android.widget.Toast
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.wrapContentSize
//import androidx.compose.material3.Button
//import androidx.compose.material3.DropdownMenu
//import androidx.compose.material3.DropdownMenuItem
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.tooling.preview.Preview
//import com.example.controlh.data.User
//import com.example.controlh.data.Role
//import kotlinx.datetime.LocalDateTime
//import java.sql.Time
//import java.util.Date
//
//// This composable takes a User object and shows a different menu based on their role.
//@Composable
//fun RoleBasedMenu(user: User) {
//    // State to track if the dropdown menu is expanded
//    var expanded by remember { mutableStateOf(false) }
//    val context = LocalContext.current
//
//    // Use a Box to anchor the dropdown menu to the button
//    Box(
//        modifier = Modifier.wrapContentSize(Alignment.TopStart)
//    ) {
//        // The button that opens the menu
//        Button(onClick = { expanded = true }) {
//            Text("Open Menu")
//        }
//
//        // The DropdownMenu that appears when expanded is true
//        DropdownMenu(
//            expanded = expanded,
//            onDismissRequest = { expanded = false }
//        ) {
//            // This is a menu item that all users can see
//            DropdownMenuItem(
//                text = { Text("Profile") },
//                onClick = {
//                    expanded = false
//                    Toast.makeText(context, "Profile clicked!", Toast.LENGTH_SHORT).show()
//                }
//            )
//
//            // This is the conditional part: only render if the user is an admin.
//            // We use the 'any' function to check if any Role object in the list has the name "ADMIN".
//            if (user.roles.any { it.name == "ROLE_ADMIN" }) {
//                DropdownMenuItem(
//                    text = { Text("Manage Users") },
//                    onClick = {
//                        expanded = false
//                        Toast.makeText(context, "Admin menu clicked!", Toast.LENGTH_SHORT).show()
//                    }
//                )
//                DropdownMenuItem(
//                    text = { Text("Analytics Dashboard") },
//                    onClick = {
//                        expanded = false
//                        Toast.makeText(context, "Analytics clicked!", Toast.LENGTH_SHORT).show()
//                    }
//                )
//            }
//        }
//    }
//}
//
//// --- Preview Composable to demonstrate the menu for different roles ---
//
//@Composable
//fun MenuDemoScreen(user: User) {
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        RoleBasedMenu(user)
//    }
//}
//
//@Preview(showBackground = true, name = "Admin User Menu Preview")
//@Composable
//fun AdminMenuPreview() {
//    val currentTimePlaceholder = Time(Date().time)
//    // Correctly creating a list of Role objects for the admin user.
//    val adminUser = User(
//        nickname = "Jane",
//        roles = listOf(Role("ROLE_ADMIN"), Role("ROLE_USER")),
//        email = "jane@example.com"
//        //on_control = currentTimePlaceholder,
//        //of_control = currentTimePlaceholder
//    )
//    MenuDemoScreen(user = adminUser)
//}
//
//@Preview(showBackground = true, name = "Standard User Menu Preview")
//@Composable
//fun StandardUserMenuPreview() {
//    val currentTimePlaceholder = Time(Date().time)
//    // Correctly creating a list of Role objects for the regular user.
//    val regularUser = User(
//        nickname = "John",
//        roles = listOf(Role("ROLE_USER")),
//        email = "john@example.com"
//        //on_control = currentTimePlaceholder,
//        //of_control = currentTimePlaceholder
//    )
//    MenuDemoScreen(user = regularUser)
//}
