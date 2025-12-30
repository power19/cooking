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
import com.suriname.recipecost.data.model.MealPlanWithMeals
import com.suriname.recipecost.ui.components.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlansScreen(
    mealPlans: List<MealPlanWithMeals>,
    recipeCosts: Map<Long, Double>,
    onAddMealPlan: () -> Unit,
    onMealPlanClick: (Long) -> Unit,
    onBack: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meal Plans", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddMealPlan,
                icon = { Icon(Icons.Default.Add, "Add") },
                text = { Text("New Plan") },
                containerColor = MaterialTheme.colorScheme.primary
            )
        }
    ) { padding ->
        if (mealPlans.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    icon = Icons.Default.CalendarMonth,
                    title = "No meal plans yet",
                    subtitle = "Plan your weekly meals and track your food budget",
                    actionLabel = "Create Plan",
                    onAction = onAddMealPlan
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mealPlans) { mealPlanWithMeals ->
                    val totalCost = mealPlanWithMeals.plannedMeals.sumOf { pm ->
                        recipeCosts[pm.recipe.id] ?: 0.0
                    }
                    val mealCount = mealPlanWithMeals.plannedMeals.size

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onMealPlanClick(mealPlanWithMeals.mealPlan.id) }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = mealPlanWithMeals.mealPlan.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Week of ${dateFormat.format(Date(mealPlanWithMeals.mealPlan.weekStart))}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "$mealCount meals planned",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Total: ${formatCurrency(totalCost)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                mealPlanWithMeals.mealPlan.budget?.let { budget ->
                                    val remaining = budget - totalCost
                                    BudgetStatusChip(
                                        remaining = remaining,
                                        budget = budget
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}
