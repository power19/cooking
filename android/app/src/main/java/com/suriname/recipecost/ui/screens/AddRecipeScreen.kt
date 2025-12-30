package com.suriname.recipecost.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.suriname.recipecost.data.model.Recipe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeScreen(
    onSave: (Recipe) -> Unit,
    onBack: () -> Unit,
    existingRecipe: Recipe? = null
) {
    var name by remember { mutableStateOf(existingRecipe?.name ?: "") }
    var description by remember { mutableStateOf(existingRecipe?.description ?: "") }
    var servings by remember { mutableStateOf(existingRecipe?.servings?.toString() ?: "4") }
    var prepTime by remember { mutableStateOf(existingRecipe?.prepTimeMinutes?.toString() ?: "") }
    var cookTime by remember { mutableStateOf(existingRecipe?.cookTimeMinutes?.toString() ?: "") }
    var instructions by remember { mutableStateOf(existingRecipe?.instructions ?: "") }

    val isEditing = existingRecipe != null
    val isValid = name.isNotBlank() && servings.toIntOrNull() != null && servings.toInt() > 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Edit Recipe" else "New Recipe",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, "Cancel")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            val recipe = Recipe(
                                id = existingRecipe?.id ?: 0,
                                name = name.trim(),
                                description = description.takeIf { it.isNotBlank() },
                                servings = servings.toIntOrNull() ?: 4,
                                prepTimeMinutes = prepTime.toIntOrNull(),
                                cookTimeMinutes = cookTime.toIntOrNull(),
                                instructions = instructions.takeIf { it.isNotBlank() }
                            )
                            onSave(recipe)
                        },
                        enabled = isValid
                    ) {
                        Text("Save", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Recipe Name") },
                placeholder = { Text("e.g., Pom, Roti, Moksi Alesi") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (optional)") },
                placeholder = { Text("Brief description of this dish") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = servings,
                    onValueChange = { servings = it.filter { c -> c.isDigit() } },
                    label = { Text("Servings") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = prepTime,
                    onValueChange = { prepTime = it.filter { c -> c.isDigit() } },
                    label = { Text("Prep (min)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = cookTime,
                    onValueChange = { cookTime = it.filter { c -> c.isDigit() } },
                    label = { Text("Cook (min)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = instructions,
                onValueChange = { instructions = it },
                label = { Text("Instructions (optional)") },
                placeholder = { Text("Step-by-step cooking instructions...") },
                minLines = 6,
                modifier = Modifier.fillMaxWidth()
            )

            if (!isEditing) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "After saving, you'll be able to add ingredients to calculate the recipe cost.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}
