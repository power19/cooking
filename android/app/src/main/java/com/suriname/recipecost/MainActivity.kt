package com.suriname.recipecost

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.suriname.recipecost.navigation.RecipeCostNavGraph
import com.suriname.recipecost.navigation.Screen
import com.suriname.recipecost.ui.theme.RecipeCostTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecipeCostTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Only show bottom nav on main screens
    val showBottomBar = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.Ingredients.route,
        Screen.Recipes.route,
        Screen.MealPlans.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Dashboard, "Dashboard") },
                        label = { Text("Home") },
                        selected = currentRoute == Screen.Dashboard.route,
                        onClick = {
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(Screen.Dashboard.route) { inclusive = true }
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Kitchen, "Ingredients") },
                        label = { Text("Ingredients") },
                        selected = currentRoute == Screen.Ingredients.route,
                        onClick = {
                            navController.navigate(Screen.Ingredients.route) {
                                popUpTo(Screen.Dashboard.route)
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.MenuBook, "Recipes") },
                        label = { Text("Recipes") },
                        selected = currentRoute == Screen.Recipes.route,
                        onClick = {
                            navController.navigate(Screen.Recipes.route) {
                                popUpTo(Screen.Dashboard.route)
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.CalendarMonth, "Meal Plans") },
                        label = { Text("Plans") },
                        selected = currentRoute == Screen.MealPlans.route,
                        onClick = {
                            navController.navigate(Screen.MealPlans.route) {
                                popUpTo(Screen.Dashboard.route)
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            RecipeCostNavGraph(navController = navController)
        }
    }
}
