package com.suriname.recipecost.data.repository

import com.suriname.recipecost.data.local.*
import com.suriname.recipecost.data.model.*
import kotlinx.coroutines.flow.Flow

class RecipeRepository(
    private val ingredientDao: IngredientDao,
    private val recipeDao: RecipeDao,
    private val recipeIngredientDao: RecipeIngredientDao,
    private val mealPlanDao: MealPlanDao,
    private val plannedMealDao: PlannedMealDao
) {
    // Ingredients
    val allIngredients: Flow<List<Ingredient>> = ingredientDao.getAllIngredients()
    val ingredientCount: Flow<Int> = ingredientDao.getIngredientCount()

    suspend fun getIngredientById(id: Long) = ingredientDao.getIngredientById(id)
    suspend fun insertIngredient(ingredient: Ingredient) = ingredientDao.insertIngredient(ingredient)
    suspend fun updateIngredient(ingredient: Ingredient) = ingredientDao.updateIngredient(ingredient)
    suspend fun deleteIngredient(ingredient: Ingredient) = ingredientDao.deleteIngredient(ingredient)

    // Recipes
    val allRecipes: Flow<List<Recipe>> = recipeDao.getAllRecipes()
    val allRecipesWithIngredients: Flow<List<RecipeWithIngredients>> = recipeDao.getAllRecipesWithIngredients()
    val recipeCount: Flow<Int> = recipeDao.getRecipeCount()

    fun getRecentRecipes(limit: Int = 5) = recipeDao.getRecentRecipes(limit)
    suspend fun getRecipeById(id: Long) = recipeDao.getRecipeById(id)
    suspend fun getRecipeWithIngredients(id: Long) = recipeDao.getRecipeWithIngredients(id)
    suspend fun insertRecipe(recipe: Recipe) = recipeDao.insertRecipe(recipe)
    suspend fun updateRecipe(recipe: Recipe) = recipeDao.updateRecipe(recipe)
    suspend fun deleteRecipe(recipe: Recipe) = recipeDao.deleteRecipe(recipe)

    // Recipe Ingredients
    fun getIngredientsForRecipe(recipeId: Long) = recipeIngredientDao.getIngredientsWithDetailsForRecipe(recipeId)
    suspend fun addIngredientToRecipe(recipeIngredient: RecipeIngredient) =
        recipeIngredientDao.insertRecipeIngredient(recipeIngredient)
    suspend fun removeIngredientFromRecipe(recipeIngredient: RecipeIngredient) =
        recipeIngredientDao.deleteRecipeIngredient(recipeIngredient)

    // Meal Plans
    val allMealPlans: Flow<List<MealPlan>> = mealPlanDao.getAllMealPlans()
    val allMealPlansWithMeals: Flow<List<MealPlanWithMeals>> = mealPlanDao.getAllMealPlansWithMeals()

    suspend fun getMealPlanById(id: Long) = mealPlanDao.getMealPlanById(id)
    suspend fun getMealPlanWithMeals(id: Long) = mealPlanDao.getMealPlanWithMeals(id)
    suspend fun insertMealPlan(mealPlan: MealPlan) = mealPlanDao.insertMealPlan(mealPlan)
    suspend fun updateMealPlan(mealPlan: MealPlan) = mealPlanDao.updateMealPlan(mealPlan)
    suspend fun deleteMealPlan(mealPlan: MealPlan) = mealPlanDao.deleteMealPlan(mealPlan)

    // Planned Meals
    fun getMealsForPlan(mealPlanId: Long) = plannedMealDao.getMealsWithRecipesForPlan(mealPlanId)
    suspend fun addMealToPlan(plannedMeal: PlannedMeal) = plannedMealDao.insertPlannedMeal(plannedMeal)
    suspend fun removeMealFromPlan(plannedMeal: PlannedMeal) = plannedMealDao.deletePlannedMeal(plannedMeal)

    // Utility functions
    fun calculateRecipeCost(recipeWithIngredients: RecipeWithIngredients): Double {
        return recipeWithIngredients.recipeIngredients.sumOf { ri ->
            ri.recipeIngredient.quantity * ri.ingredient.pricePerUnit
        }
    }

    fun calculateCostPerServing(recipeWithIngredients: RecipeWithIngredients): Double {
        val totalCost = calculateRecipeCost(recipeWithIngredients)
        return if (recipeWithIngredients.recipe.servings > 0) {
            totalCost / recipeWithIngredients.recipe.servings
        } else 0.0
    }

    fun calculateMealPlanCost(mealPlanWithMeals: MealPlanWithMeals, recipeCosts: Map<Long, Double>): Double {
        return mealPlanWithMeals.plannedMeals.sumOf { pm ->
            recipeCosts[pm.recipe.id] ?: 0.0
        }
    }
}
