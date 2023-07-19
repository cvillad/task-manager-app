package com.example.task_manage_app.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.task_manage_app.dataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "FlowOperatorInvokedInComposition")
@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeViewModel = viewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authToken = context.dataStore.data.map { settings ->
        settings[stringPreferencesKey("authToken")]
    }.collectAsState(initial = null).value

    var menuOpen by remember { mutableStateOf(false) }
    val openDialog = remember { mutableStateOf(false) }

    if (openDialog.value) {
        CreateTaskDialog(
            openDialog = openDialog,
            viewModel = viewModel
        )
    }

    LaunchedEffect(key1 = authToken) {
        authToken?.let { viewModel.fetchTasks(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Todo App")
                },
                actions = {
                    IconButton(
                        onClick = { menuOpen = !menuOpen }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Account"
                        )
                    }
                    DropdownMenu(
                        expanded = menuOpen,
                        onDismissRequest = { menuOpen = false }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                scope.launch {
                                    context.dataStore.edit { settings ->
                                        settings.remove(stringPreferencesKey("authToken"))
                                    }
                                    menuOpen = false
                                }
                            }
                        ) {
                            Text(text = "Cerrar sesión")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { openDialog.value = true },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color.LightGray
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add task button")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(all = 20.dp)
                .fillMaxWidth()
        ) {
            viewModel.todoList.value.forEach { task ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = task.description)
                    Checkbox(
                        checked = task.completed,
                        onCheckedChange = {
                            scope.launch {
                                viewModel.markAsCompleted(authToken!!, task, it)
                            }
                        }
                    )
                }
            }
        }
    }
}

@SuppressLint("FlowOperatorInvokedInComposition")
@Composable
fun CreateTaskDialog(openDialog: MutableState<Boolean>, viewModel: HomeViewModel) {
    var description by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val authToken = LocalContext.current.dataStore.data.map { settings ->
        settings[stringPreferencesKey("authToken")]
    }.collectAsState(initial = null).value

    AlertDialog(
        title = {
            Text(text = "Crear tarea", modifier = Modifier.padding(bottom = 20.dp))
        },
        onDismissRequest = { openDialog.value = false },
        text = {
            TextField(
                label = {
                    Text(text = "Description")
                },
                value = description,
                onValueChange = { description = it },
                isError = description.isEmpty()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    scope.launch {
                        viewModel.addTask(authToken!!, description)
                        openDialog.value = false
                    }
                },
                modifier = Modifier.padding(end = 20.dp),
                enabled = description.isNotEmpty()
            ) {
                Text(text = "Crear")
            }
        },
    )
}