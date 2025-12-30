package com.suriname.recipecost.data.local

import androidx.room.*
import com.suriname.recipecost.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {
    @Query("SELECT * FROM ingredients ORDER BY name ASC")
    fun getAllIngredients(): Flow<List<Ingredient>>

    @Query("SELECT * FROM ingredients WHERE category = :category ORDER BY name ASC")
    fun getIngredientsByCategory(category: String): Flow<List<Ingredient>>

    @Query("SELECT * FROM ingredients WHERE id = :id")
    suspend fun getIngredientById(id: Long): Ingredient?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: Ingredient): Long

    @Update
    suspend fun updateIngredient(ingredient: Ingredient)

    @Delete
    suspend fun deleteIngredient(ingredient: Ingredient)

    @Query("SELECT COUNT(*) FROM ingredients")
    fun getIngredientCount(): Flow<Int>
}

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY name ASC")
    fun getAllRecipes(): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentRecipes(limit: Int = 5): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: Long): Recipe?

    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeWithIngredients(id: Long): RecipeWithIngredients?

    @Transaction
    @Query("SELECT * FROM recipes")
    fun getAllRecipesWithIngredients(): Flow<List<RecipeWithIngredients>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe): Long

    @Update
    suspend fun updateRecipe(recipe: Recipe)

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)

    @Query("SELECT COUNT(*) FROM recipes")
    fun getRecipeCount(): Flow<Int>
}

@Dao
interface RecipeIngredientDao {
    @Query("SELECT * FROM recipe_ingredients WHERE recipeId = :recipeId")
    fun getIngredientsForRecipe(recipeId: Long): Flow<List<RecipeIngredient>>

    @Transaction
    @Query("SELECT * FROM recipe_ingredients WHERE recipeId = :recipeId")
    fun getIngredientsWithDetailsForRecipe(recipeId: Long): Flow<List<RecipeIngredientWithDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeIngredient(recipeIngredient: RecipeIngredient): Long

    @Delete
    suspend fun deleteRecipeIngredient(recipeIngredient: RecipeIngredient)

    @Query("DELETE FROM recipe_ingredients WHERE recipeId = :recipeId")
    suspend fun deleteAllForRecipe(recipeId: Long)
}

@Dao
interface MealPlanDao {
    @Query("SELECT * FROM meal_plans ORDER BY weekStart DESC")
    fun getAllMealPlans(): Flow<List<MealPlan>>

    @Query("SELECT * FROM meal_plans WHERE id = :id")
    suspend fun getMealPlanById(id: Long): MealPlan?

    @Transaction
    @Query("SELECT * FROM meal_plans WHERE id = :id")
    suspend fun getMealPlanWithMeals(id: Long): MealPlanWithMeals?

    @Transaction
    @Query("SELECT * FROM meal_plans ORDER BY weekStart DESC")
    fun getAllMealPlansWithMeals(): Flow<List<MealPlanWithMeals>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealPlan(mealPlan: MealPlan): Long

    @Update
    suspend fun updateMealPlan(mealPlan: MealPlan)

    @Delete
    suspend fun deleteMealPlan(mealPlan: MealPlan)
}

@Dao
interface PlannedMealDao {
    @Query("SELECT * FROM planned_meals WHERE mealPlanId = :mealPlanId")
    fun getMealsForPlan(mealPlanId: Long): Flow<List<PlannedMeal>>

    @Transaction
    @Query("SELECT * FROM planned_meals WHERE mealPlanId = :mealPlanId")
    fun getMealsWithRecipesForPlan(mealPlanId: Long): Flow<List<PlannedMealWithRecipe>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlannedMeal(plannedMeal: PlannedMeal): Long

    @Delete
    suspend fun deletePlannedMeal(plannedMeal: PlannedMeal)

    @Query("DELETE FROM planned_meals WHERE mealPlanId = :mealPlanId AND dayOfWeek = :dayOfWeek AND mealType = :mealType")
    suspend fun deleteMealAtSlot(mealPlanId: Long, dayOfWeek: Int, mealType: String)
}
