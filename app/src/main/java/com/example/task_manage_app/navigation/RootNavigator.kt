package com.example.task_manage_app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.task_manage_app.dataStore
import com.example.task_manage_app.screens.home.HomeScreen
import com.example.task_manage_app.screens.login.LoginScreen
import kotlinx.coroutines.flow.map

@Composable
fun RootNavigator() {
    val authToken = LocalContext.current.dataStore.data.map { settings ->
        settings[stringPreferencesKey("authToken")]
    }.collectAsState(initial = null).value

    val navController = rememberNavController()

    if (authToken === null) {
        NavHost(navController = navController, startDestination = "login") {
            composable("login") { LoginScreen(navController) }
        }
    } else {
        NavHost(navController = navController, startDestination = "home") {
            composable("home") { HomeScreen(navController) }
        }
    }
}