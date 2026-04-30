package com.example.controlh.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.controlh.AuthViewModel
import com.example.controlh.RetrofitClient
import com.example.controlh.data.UserFull
import com.example.controlh.data.RoleUpdateRequest
import com.example.controlh.navigation.AppScreens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListUser(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val userList = remember { mutableStateListOf<UserFull>() }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    var showEditDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<UserFull?>(null) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var userToDelete by remember { mutableStateOf<UserFull?>(null) }

    fun refreshData() {
        coroutineScope.launch(Dispatchers.Default) {
            try {
                isLoading = true
                errorMessage = null
                val response = RetrofitClient.instance.getAllUsersFull()
                if (response.isSuccessful) {
                    response.body()?.let { fetchedUsers ->
                        withContext(Dispatchers.Main) {
                            userList.clear()
                            userList.addAll(fetchedUsers)
                        }
                    }
                } else {
                    errorMessage = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
    
    val onDeleteConfirm: (Long) -> Unit = { userId ->
        coroutineScope.launch(Dispatchers.Default) {
            try {
                val response = RetrofitClient.instanceA.deleteUser(userId)
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        showDeleteDialog = false
                        refreshData()
                    }
                }
            } catch (e: Exception) {
                println("Error al eliminar: ${e.message}")
            }
        }
    }

    if (showDeleteDialog && userToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Eliminar usuario?") },
            text = { Text("¿Estás seguro de que quieres eliminar a ${userToDelete!!.nickname}? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = { onDeleteConfirm(userToDelete!!.id.toLong()) },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }

    val onEditClick: (UserFull) -> Unit = { user ->
        selectedUser = user
        showEditDialog = true
    }

    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) refreshData()
    }

    if (showEditDialog && selectedUser != null) {
        EditUserDialog(
            user = selectedUser!!,
            onDismiss = { showEditDialog = false },
            onConfirm = { updatedUser, newRoleId ->
                coroutineScope.launch(Dispatchers.Default) {
                    try {
                        val resUser = RetrofitClient.instanceA.updateUser(updatedUser.id, updatedUser)
                        val resRole = RetrofitClient.instanceA.updateUserRole(
                            updatedUser.email,
                            RoleUpdateRequest(newRoleId)
                        )

                        if (resUser.isSuccessful && resRole.isSuccessful) {
                            withContext(Dispatchers.Main) {
                                showEditDialog = false
                                refreshData()
                            }
                        } else {
                            println("Fallo - User: ${resUser.code()}, Role: ${resRole.code()}")
                        }
                    } catch (e: Exception) {
                        println("Error fatal en actualización: ${e.message}")
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Gestión de Usuarios") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        ListBodyContent(
            navController = navController,
            paddingValues = paddingValues,
            userList = userList,
            isLoading = isLoading,
            errorMessage = errorMessage,
            onRetry = { refreshData() },
            onEditClick = onEditClick,
            onDeleteClick = { user ->
                userToDelete = user
                showDeleteDialog = true
            },
            onRefresh = { refreshData() }
        )
    }
}

@Composable
fun ListBodyContent(
    navController: NavController,
    paddingValues: PaddingValues,
    userList: List<UserFull>,
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit,
    onEditClick: (UserFull) -> Unit,
    onDeleteClick: (UserFull) -> Unit,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (errorMessage != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
                Button(onClick = onRetry) { Text("Reintentar") }
            }
        } else {
            LasHoras(userList, onEditClick, onDeleteClick)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate(AppScreens.Home.route) }) {
            Text("Volver al Inicio")
        }
    }
}

@Composable
fun LasHoras(
    user: List<UserFull>,
    onEditClick: (UserFull) -> Unit,
    onDeleteClick: (UserFull) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(
            items = user,
            key = { it.id }
        ) { item ->
            MyComponent(use = item, onEditClick = onEditClick, onDeleteClick = onDeleteClick)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun MyComponent(use: UserFull, onEditClick: (UserFull) -> Unit, onDeleteClick: (UserFull) -> Unit) {
    CardItemComposable(use = use, onEditClick = onEditClick, onDeleteClick = onDeleteClick)
}

@Composable
fun CardItemComposable(use: UserFull, onEditClick: (UserFull) -> Unit, onDeleteClick: (UserFull) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = use.nickname, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                Text(text = "Email: ${use.email}", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "Rol: ${if (use.roles.any { it.erole == "ROLE_ADMIN" }) "ADMIN" else "USER"}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(text = "ON: ${use.on_control} | OFF: ${use.of_control}", style = MaterialTheme.typography.bodySmall)
            }

            IconButton(onClick = { onEditClick(use) }) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = { onDeleteClick(use) }) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
