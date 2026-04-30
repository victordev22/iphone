package com.example.controlh.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.controlh.data.UserFull

@Composable
fun EditUserDialog(
    user: UserFull,
    onDismiss: () -> Unit,
    onConfirm: (UserFull, Int) -> Unit
) {
    // Evitamos el NullPointerException inicializando con "" si el campo es nulo
    var nickname by remember { mutableStateOf(user.nickname ?: "") }
    var email by remember { mutableStateOf(user.email ?: "") }
    var onTime by remember { mutableStateOf(user.on_control ?: "") }
    var offTime by remember { mutableStateOf(user.of_control ?: "") }

    var selectedRoleId by remember {
        mutableStateOf(if (user.roles.any { it.erole == "ROLE_ADMIN" }) 2 else 1)
    }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Editar Usuario") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text("Nickname") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text("Rol del Usuario", style = MaterialTheme.typography.labelMedium)

                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (selectedRoleId == 2) "ADMINISTRADOR" else "USUARIO")
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text("USUARIO") },
                            onClick = { selectedRoleId = 1; expanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("ADMINISTRADOR") },
                            onClick = { selectedRoleId = 2; expanded = false }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = onTime,
                    onValueChange = { onTime = it },
                    label = { Text("Encendido (HH:mm:ss)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = offTime,
                    onValueChange = { offTime = it },
                    label = { Text("Apagado (HH:mm:ss)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(
                    user.copy(
                        nickname = nickname,
                        email = email,
                        on_control = onTime,
                        of_control = offTime
                    ),
                    selectedRoleId
                )
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}