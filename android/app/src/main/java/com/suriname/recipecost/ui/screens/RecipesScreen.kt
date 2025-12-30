package com.suriname.recipecost.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.suriname.recipecost.data.model.RecipeWithIngredients
import com.suriname.recipecost.ui.components.*

enum class RecipeSortOrder(val label: String) {
    NAME("Name"),
    COST_LOW("Cost (Low to High)"),
    COST_HIGH("Cost (High to Low)"),
    RECENT("Recently Added")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipesScreen(
    recipes: List<RecipeWithIngredients>,
    onAddRecipe: () -> Unit,
    onRecipeClick: (Long) -> Unit,
    onBack: () -> Unit,
    calculateCost: (RecipeWithIngredients) -> Double,
    calculateCostPerServing: (RecipeWithIngredients) -> Double
) {
    var sortOrder by remember { mutableStateOf(RecipeSortOrder.NAME) }
    var showSortMenu by remember { mutableStateOf(false) }

    val sortedRecipes = remember(recipes, sortOrder) {
        when (sortOrder) {
            RecipeSortOrder.NAME -> recipes.sortedBy { it.recipe.name }
            RecipeSortOrder.COST_LOW -> recipes.sortedBy { calculateCostPerServing(it) }
            RecipeSortOrder.COST_HIGH -> recipes.sortedByDescending { calculateCostPerServing(it) }
            RecipeSortOrder.RECENT -> recipes.sortedByDescending { it.recipe.createdAt }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recipes", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.Default.Sort, "Sort")
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            RecipeSortOrder.entries.forEach { order ->
                                DropdownMenuItem(
                                    text = { Text(order.label) },
                                    onClick = {
                                        sortOrder = order
                                        showSortMenu = false
                                    },
                                    leadingIcon = {
                                        if (sortOrder == order) {
                                            Icon(Icons.Default.Check, "Selected")
                                        }
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddRecipe,
                icon = { Icon(Icons.Default.Add, "Add") },
                text = { Text("Add Recipe") },
                containerColor = MaterialTheme.colorScheme.primary
            )
        }
    ) { padding ->
        if (sortedRecipes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    icon = Icons.Default.MenuBook,
                    title = "No recipes yet",
                    subtitle = "Create your first recipe to start tracking costs",
                    actionLabel = "Add Recipe",
                    onAction = onAddRecipe
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "Sorted by: ${sortOrder.label}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }

                items(sortedRecipes) { recipeWithIngredients ->
                    val cost = calculateCost(recipeWithIngredients)
                    val costPerServing = calculateCostPerServing(recipeWithIngredients)
                    RecipeListItem(
                        name = recipeWithIngredients.recipe.name,
                        servings = recipeWithIngredients.recipe.servings,
                        totalCost = cost,
                        costPerServing = costPerServing,
                        onClick = { onRecipeClick(recipeWithIngredients.recipe.id) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}
