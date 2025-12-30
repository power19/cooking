package com.suriname.recipecost.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.suriname.recipecost.data.model.*
import com.suriname.recipecost.data.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: RecipeRepository) : ViewModel() {

    // Ingredients
    val allIngredients: Flow<List<Ingredient>> = repository.allIngredients
    val ingredientCount: Flow<Int> = repository.ingredientCount

    fun insertIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            repository.insertIngredient(ingredient)
        }
    }

    fun updateIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            repository.updateIngredient(ingredient)
        }
    }

    fun deleteIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            repository.deleteIngredient(ingredient)
        }
    }

    // Recipes
    val allRecipes: Flow<List<Recipe>> = repository.allRecipes
    val allRecipesWithIngredients: Flow<List<RecipeWithIngredients>> = repository.allRecipesWithIngredients
    val recipeCount: Flow<Int> = repository.recipeCount

    fun insertRecipe(recipe: Recipe) {
        viewModelScope.launch {
            repository.insertRecipe(recipe)
        }
    }

    fun updateRecipe(recipe: Recipe) {
        viewModelScope.launch {
            repository.updateRecipe(recipe)
        }
    }

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            repository.deleteRecipe(recipe)
        }
    }

    fun addIngredientToRecipe(recipeIngredient: RecipeIngredient) {
        viewModelScope.launch {
            repository.addIngredientToRecipe(recipeIngredient)
        }
    }

    fun removeIngredientFromRecipe(recipeIngredient: RecipeIngredient) {
        viewModelScope.launch {
            repository.removeIngredientFromRecipe(recipeIngredient)
        }
    }

    // Meal Plans
    val allMealPlans: Flow<List<MealPlan>> = repository.allMealPlans
    val allMealPlansWithMeals: Flow<List<MealPlanWithMeals>> = repository.allMealPlansWithMeals

    fun insertMealPlan(mealPlan: MealPlan) {
        viewModelScope.launch {
            repository.insertMealPlan(mealPlan)
        }
    }

    fun updateMealPlan(mealPlan: MealPlan) {
        viewModelScope.launch {
            repository.updateMealPlan(mealPlan)
        }
    }

    fun deleteMealPlan(mealPlan: MealPlan) {
        viewModelScope.launch {
            repository.deleteMealPlan(mealPlan)
        }
    }

    fun addMealToPlan(plannedMeal: PlannedMeal) {
        viewModelScope.launch {
            repository.addMealToPlan(plannedMeal)
        }
    }

    fun removeMealFromPlan(plannedMeal: PlannedMeal) {
        viewModelScope.launch {
            repository.removeMealFromPlan(plannedMeal)
        }
    }

    // Cost calculations
    fun calculateRecipeCost(recipeWithIngredients: RecipeWithIngredients): Double {
        return repository.calculateRecipeCost(recipeWithIngredients)
    }

    fun calculateCostPerServing(recipeWithIngredients: RecipeWithIngredients): Double {
        return repository.calculateCostPerServing(recipeWithIngredients)
    }
}

class MainViewModelFactory(private val repository: RecipeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
