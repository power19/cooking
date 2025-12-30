package com.suriname.recipecost.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.suriname.recipecost.data.model.RecipeWithIngredients
import com.suriname.recipecost.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    recipeCount: Int,
    ingredientCount: Int,
    mealPlanCount: Int,
    recentRecipes: List<RecipeWithIngredients>,
    costEffectiveRecipes: List<RecipeWithIngredients>,
    onRecipeClick: (Long) -> Unit,
    onViewAllRecipes: () -> Unit,
    onAddRecipe: () -> Unit,
    calculateCost: (RecipeWithIngredients) -> Double,
    calculateCostPerServing: (RecipeWithIngredients) -> Double
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Recipe Cost Calculator",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddRecipe,
                icon = { Icon(Icons.Default.Add, "Add Recipe") },
                text = { Text("New Recipe") },
                containerColor = MaterialTheme.colorScheme.primary
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 88.dp)
        ) {
            // Stats Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Recipes",
                        value = recipeCount.toString(),
                        icon = Icons.Default.MenuBook,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Ingredients",
                        value = ingredientCount.toString(),
                        icon = Icons.Default.Kitchen,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Meal Plans",
                        value = mealPlanCount.toString(),
                        icon = Icons.Default.CalendarMonth,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Most Cost-Effective Recipes
            item {
                SectionHeader(
                    title = "Most Cost-Effective",
                    actionLabel = "View All",
                    onAction = onViewAllRecipes
                )
            }

            if (costEffectiveRecipes.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Default.Savings,
                        title = "No recipes yet",
                        subtitle = "Add recipes to see which are most cost-effective",
                        actionLabel = "Add Recipe",
                        onAction = onAddRecipe
                    )
                }
            } else {
                items(costEffectiveRecipes.take(5)) { recipeWithIngredients ->
                    val cost = calculateCost(recipeWithIngredients)
                    val costPerServing = calculateCostPerServing(recipeWithIngredients)
                    RecipeListItem(
                        name = recipeWithIngredients.recipe.name,
                        servings = recipeWithIngredients.recipe.servings,
                        totalCost = cost,
                        costPerServing = costPerServing,
                        onClick = { onRecipeClick(recipeWithIngredients.recipe.id) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }

            // Recent Recipes
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(
                    title = "Recent Recipes",
                    actionLabel = "View All",
                    onAction = onViewAllRecipes
                )
            }

            if (recentRecipes.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        ) {
                            Text(
                                text = "Start tracking your recipes!",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Add your favorite Surinamese recipes to calculate costs and plan meals.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(recentRecipes.take(3)) { recipeWithIngredients ->
                    val cost = calculateCost(recipeWithIngredients)
                    val costPerServing = calculateCostPerServing(recipeWithIngredients)
                    RecipeListItem(
                        name = recipeWithIngredients.recipe.name,
                        servings = recipeWithIngredients.recipe.servings,
                        totalCost = cost,
                        costPerServing = costPerServing,
                        onClick = { onRecipeClick(recipeWithIngredients.recipe.id) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
