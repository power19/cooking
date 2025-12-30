package com.suriname.recipecost.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.suriname.recipecost.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Ingredient::class,
        Recipe::class,
        RecipeIngredient::class,
        MealPlan::class,
        PlannedMeal::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ingredientDao(): IngredientDao
    abstract fun recipeDao(): RecipeDao
    abstract fun recipeIngredientDao(): RecipeIngredientDao
    abstract fun mealPlanDao(): MealPlanDao
    abstract fun plannedMealDao(): PlannedMealDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "recipe_cost_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.ingredientDao())
                }
            }
        }

        suspend fun populateDatabase(ingredientDao: IngredientDao) {
            // Sample Surinamese ingredients with typical prices (in SRD)
            val sampleIngredients = listOf(
                Ingredient(name = "Rice (local)", category = "grains", unit = "kg", pricePerUnit = 45.00),
                Ingredient(name = "Chicken", category = "meat", unit = "kg", pricePerUnit = 85.00),
                Ingredient(name = "Cassava", category = "produce", unit = "kg", pricePerUnit = 25.00),
                Ingredient(name = "Plantain", category = "produce", unit = "piece", pricePerUnit = 8.00),
                Ingredient(name = "Onion", category = "produce", unit = "kg", pricePerUnit = 35.00),
                Ingredient(name = "Tomato", category = "produce", unit = "kg", pricePerUnit = 40.00),
                Ingredient(name = "Garlic", category = "produce", unit = "kg", pricePerUnit = 120.00),
                Ingredient(name = "Cooking Oil", category = "oils", unit = "liter", pricePerUnit = 55.00),
                Ingredient(name = "Salt", category = "spices", unit = "kg", pricePerUnit = 15.00),
                Ingredient(name = "Black Pepper", category = "spices", unit = "g", pricePerUnit = 0.50),
                Ingredient(name = "Eggs", category = "dairy", unit = "piece", pricePerUnit = 4.50),
                Ingredient(name = "Milk", category = "dairy", unit = "liter", pricePerUnit = 25.00),
                Ingredient(name = "Fish (fresh)", category = "seafood", unit = "kg", pricePerUnit = 95.00),
                Ingredient(name = "Dried Shrimp", category = "seafood", unit = "g", pricePerUnit = 0.80),
                Ingredient(name = "Coconut Milk", category = "canned", unit = "can", pricePerUnit = 18.00),
                Ingredient(name = "Beans (dried)", category = "grains", unit = "kg", pricePerUnit = 38.00)
            )

            sampleIngredients.forEach { ingredient ->
                ingredientDao.insertIngredient(ingredient)
            }
        }
    }
}
