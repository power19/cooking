package com.suriname.recipecost

import android.app.Application
import com.suriname.recipecost.data.local.AppDatabase
import com.suriname.recipecost.data.repository.RecipeRepository

class RecipeCostApp : Application() {

    private val database by lazy { AppDatabase.getDatabase(this) }

    val repository by lazy {
        RecipeRepository(
            ingredientDao = database.ingredientDao(),
            recipeDao = database.recipeDao(),
            recipeIngredientDao = database.recipeIngredientDao(),
            mealPlanDao = database.mealPlanDao(),
            plannedMealDao = database.plannedMealDao()
        )
    }
}
