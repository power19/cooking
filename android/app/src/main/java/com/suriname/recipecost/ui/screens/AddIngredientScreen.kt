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
import com.suriname.recipecost.data.model.Ingredient
import com.suriname.recipecost.data.model.IngredientCategories
import com.suriname.recipecost.data.model.Units

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIngredientScreen(
    onSave: (Ingredient) -> Unit,
    onBack: () -> Unit,
    existingIngredient: Ingredient? = null
) {
    var name by remember { mutableStateOf(existingIngredient?.name ?: "") }
    var category by remember { mutableStateOf(existingIngredient?.category ?: "produce") }
    var unit by remember { mutableStateOf(existingIngredient?.unit ?: "kg") }
    var pricePerUnit by remember { mutableStateOf(existingIngredient?.pricePerUnit?.toString() ?: "") }
    var notes by remember { mutableStateOf(existingIngredient?.notes ?: "") }

    var categoryExpanded by remember { mutableStateOf(false) }
    var unitExpanded by remember { mutableStateOf(false) }

    val isEditing = existingIngredient != null
    val isValid = name.isNotBlank() && pricePerUnit.toDoubleOrNull() != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Edit Ingredient" else "Add Ingredient",
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
                            val ingredient = Ingredient(
                                id = existingIngredient?.id ?: 0,
                                name = name.trim(),
                                category = category,
                                unit = unit,
                                pricePerUnit = pricePerUnit.toDoubleOrNull() ?: 0.0,
                                notes = notes.takeIf { it.isNotBlank() }
                            )
                            onSave(ingredient)
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
                label = { Text("Ingredient Name") },
                placeholder = { Text("e.g., Rice (local)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it }
            ) {
                OutlinedTextField(
                    value = IngredientCategories.categories.find { it.first == category }?.second ?: category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    IngredientCategories.categories.forEach { (key, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                category = key
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Price field
                OutlinedTextField(
                    value = pricePerUnit,
                    onValueChange = { pricePerUnit = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Price (SRD)") },
                    placeholder = { Text("0.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                // Unit Dropdown
                ExposedDropdownMenuBox(
                    expanded = unitExpanded,
                    onExpandedChange = { unitExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = Units.units.find { it.first == unit }?.second ?: unit,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unit") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = unitExpanded,
                        onDismissRequest = { unitExpanded = false }
                    ) {
                        Units.units.forEach { (key, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    unit = key
                                    unitExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                placeholder = { Text("Price source, alternatives, etc.") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            // Helper text
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Enter the current market price for this ingredient. Update it regularly to keep recipe costs accurate.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
