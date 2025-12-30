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
import com.suriname.recipecost.data.model.Ingredient
import com.suriname.recipecost.data.model.IngredientCategories
import com.suriname.recipecost.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientsScreen(
    ingredients: List<Ingredient>,
    onAddIngredient: () -> Unit,
    onIngredientClick: (Long) -> Unit,
    onBack: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var showFilterSheet by remember { mutableStateOf(false) }

    val filteredIngredients = if (selectedCategory != null) {
        ingredients.filter { it.category == selectedCategory }
    } else {
        ingredients
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ingredients", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterSheet = true }) {
                        Badge(
                            modifier = Modifier.offset(x = 8.dp, y = (-8).dp)
                        ) {
                            if (selectedCategory != null) {
                                Text("1")
                            }
                        }
                        Icon(Icons.Default.FilterList, "Filter")
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
                onClick = onAddIngredient,
                icon = { Icon(Icons.Default.Add, "Add") },
                text = { Text("Add Ingredient") },
                containerColor = MaterialTheme.colorScheme.primary
            )
        }
    ) { padding ->
        if (filteredIngredients.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    icon = Icons.Default.Kitchen,
                    title = if (selectedCategory != null) "No ingredients in this category" else "No ingredients yet",
                    subtitle = "Add ingredients with their prices to calculate recipe costs",
                    actionLabel = "Add Ingredient",
                    onAction = onAddIngredient
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 0.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (selectedCategory != null) {
                    item {
                        FilterChip(
                            selected = true,
                            onClick = { selectedCategory = null },
                            label = {
                                Text(
                                    IngredientCategories.categories
                                        .find { it.first == selectedCategory }?.second ?: selectedCategory!!
                                )
                            },
                            trailingIcon = {
                                Icon(Icons.Default.Close, "Clear filter", Modifier.size(16.dp))
                            },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                items(filteredIngredients) { ingredient ->
                    IngredientListItem(
                        name = ingredient.name,
                        category = ingredient.category,
                        pricePerUnit = ingredient.pricePerUnit,
                        unit = ingredient.unit,
                        onClick = { onIngredientClick(ingredient.id) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Filter by Category",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                // All categories option
                ListItem(
                    headlineContent = { Text("All Categories") },
                    leadingContent = {
                        RadioButton(
                            selected = selectedCategory == null,
                            onClick = {
                                selectedCategory = null
                                showFilterSheet = false
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                IngredientCategories.categories.forEach { (key, label) ->
                    ListItem(
                        headlineContent = { Text(label) },
                        leadingContent = {
                            RadioButton(
                                selected = selectedCategory == key,
                                onClick = {
                                    selectedCategory = key
                                    showFilterSheet = false
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
