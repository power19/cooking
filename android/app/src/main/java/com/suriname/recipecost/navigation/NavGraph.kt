package com.suriname.recipecost.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.suriname.recipecost.RecipeCostApp
import com.suriname.recipecost.ui.screens.*

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Ingredients : Screen("ingredients")
    object AddIngredient : Screen("add_ingredient")
    object EditIngredient : Screen("edit_ingredient/{ingredientId}") {
        fun createRoute(ingredientId: Long) = "edit_ingredient/$ingredientId"
    }
    object Recipes : Screen("recipes")
    object AddRecipe : Screen("add_recipe")
    object EditRecipe : Screen("edit_recipe/{recipeId}") {
        fun createRoute(recipeId: Long) = "edit_recipe/$recipeId"
    }
    object RecipeDetail : Screen("recipe/{recipeId}") {
        fun createRoute(recipeId: Long) = "recipe/$recipeId"
    }
    object MealPlans : Screen("meal_plans")
    object AddMealPlan : Screen("add_meal_plan")
    object MealPlanDetail : Screen("meal_plan/{mealPlanId}") {
        fun createRoute(mealPlanId: Long) = "meal_plan/$mealPlanId"
    }
}

@Composable
fun RecipeCostNavGraph(
    navController: NavHostController,
    viewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(
            (LocalContext.current.applicationContext as RecipeCostApp).repository
        )
    )
) {
    val ingredients by viewModel.allIngredients.collectAsState(initial = emptyList())
    val recipesWithIngredients by viewModel.allRecipesWithIngredients.collectAsState(initial = emptyList())
    val mealPlansWithMeals by viewModel.allMealPlansWithMeals.collectAsState(initial = emptyList())
    val ingredientCount by viewModel.ingredientCount.collectAsState(initial = 0)
    val recipeCount by viewModel.recipeCount.collectAsState(initial = 0)

    val recipeCosts = remember(recipesWithIngredients) {
        recipesWithIngredients.associate { it.recipe.id to viewModel.calculateRecipeCost(it) }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                recipeCount = recipeCount,
                ingredientCount = ingredientCount,
                mealPlanCount = mealPlansWithMeals.size,
                recentRecipes = recipesWithIngredients.sortedByDescending { it.recipe.createdAt }.take(5),
                costEffectiveRecipes = recipesWithIngredients
                    .filter { viewModel.calculateRecipeCost(it) > 0 }
                    .sortedBy { viewModel.calculateCostPerServing(it) },
                onRecipeClick = { navController.navigate(Screen.RecipeDetail.createRoute(it)) },
                onViewAllRecipes = { navController.navigate(Screen.Recipes.route) },
                onAddRecipe = { navController.navigate(Screen.AddRecipe.route) },
                calculateCost = { viewModel.calculateRecipeCost(it) },
                calculateCostPerServing = { viewModel.calculateCostPerServing(it) }
            )
        }

        composable(Screen.Ingredients.route) {
            IngredientsScreen(
                ingredients = ingredients,
                onAddIngredient = { navController.navigate(Screen.AddIngredient.route) },
                onIngredientClick = { navController.navigate(Screen.EditIngredient.createRoute(it)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AddIngredient.route) {
            AddIngredientScreen(
                onSave = { ingredient ->
                    viewModel.insertIngredient(ingredient)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditIngredient.route,
            arguments = listOf(navArgument("ingredientId") { type = NavType.LongType })
        ) { backStackEntry ->
            val ingredientId = backStackEntry.arguments?.getLong("ingredientId") ?: return@composable
            val ingredient = ingredients.find { it.id == ingredientId }

            if (ingredient != null) {
                AddIngredientScreen(
                    existingIngredient = ingredient,
                    onSave = { updated ->
                        viewModel.updateIngredient(updated)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.Recipes.route) {
            RecipesScreen(
                recipes = recipesWithIngredients,
                onAddRecipe = { navController.navigate(Screen.AddRecipe.route) },
                onRecipeClick = { navController.navigate(Screen.RecipeDetail.createRoute(it)) },
                onBack = { navController.popBackStack() },
                calculateCost = { viewModel.calculateRecipeCost(it) },
                calculateCostPerServing = { viewModel.calculateCostPerServing(it) }
            )
        }

        composable(Screen.AddRecipe.route) {
            AddRecipeScreen(
                onSave = { recipe ->
                    viewModel.insertRecipe(recipe)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.MealPlans.route) {
            MealPlansScreen(
                mealPlans = mealPlansWithMeals,
                recipeCosts = recipeCosts,
                onAddMealPlan = { /* TODO: implement */ },
                onMealPlanClick = { /* TODO: implement */ },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
