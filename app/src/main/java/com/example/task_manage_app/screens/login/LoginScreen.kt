package com.example.task_manage_app.screens.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.task_manage_app.dataStore
import com.example.task_manage_app.validations.ValidationEvent
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    val viewModel: LoginViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val formState = viewModel.uiState.value
    var showAlert by remember { mutableStateOf(false) }

    var isValidForm by remember { mutableStateOf(false) }
    var hidePassword by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = formState, block = {
        viewModel.onEvent(LoginEvent.ValidateFields)
    })

    LaunchedEffect(key1 = Unit) {
        scope.launch {
            viewModel.validationEvent.collect { event ->
                isValidForm = when(event) {
                    is ValidationEvent.Success -> true
                    is ValidationEvent.Failed -> false
                }
            }
        }
    }
    
    if (showAlert) {
        AlertDialog(
            onDismissRequest = { showAlert = false },
            text = {
                Text(text = "Email o contraseña incorrectos")
            },
            confirmButton = {
                Button(onClick = { showAlert = false }) {
                    Text(text = "Aceptar")
                }
            },
            backgroundColor = Color.Red,
            contentColor = Color.White
        )
    }
    
    Column(
        modifier = Modifier
            .padding(
                vertical = 30.dp,
                horizontal = 20.dp
            )
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Iniciar sesión",
            modifier = Modifier
                .padding(bottom = 20.dp),
            style = MaterialTheme.typography.h5
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                label = { Text(text = "Correo electrónico") },
                value = formState.email,
                onValueChange = { value ->
                    viewModel.onEvent(LoginEvent.UpdateEmail(value))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                isError = formState.hasEmailError
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                label = { Text(text = "Contraseña") },
                value = formState.password,
                onValueChange = {
                    viewModel.onEvent(LoginEvent.UpdatePassword(it))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation = if (hidePassword) PasswordVisualTransformation() else VisualTransformation.None,
                isError = formState.hasPasswordError,
                trailingIcon = {
                    Icon(
                        modifier = Modifier.clickable { hidePassword = !hidePassword },
                        imageVector = if (hidePassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Password Icon"
                    )
                }
            )
        }
        Button(
            onClick = {
                scope.launch {
                    val token = viewModel.login(formState.email, formState.password)

                    if (token === null) {
                        showAlert = true
                        return@launch
                    }

                    context.dataStore.edit { settings ->
                        settings[stringPreferencesKey("authToken")] = token
                    }
                }
            },
            enabled = isValidForm
        ) {
            Text(text = "Iniciar sesión")
        }
    }
}
