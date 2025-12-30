package com.suriname.recipecost.data.model

import androidx.room.*
import java.util.Date

@Entity(tableName = "ingredients")
data class Ingredient(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String,
    val unit: String,
    val pricePerUnit: Double,
    val notes: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String? = null,
    val servings: Int = 4,
    val prepTimeMinutes: Int? = null,
    val cookTimeMinutes: Int? = null,
    val instructions: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "recipe_ingredients",
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Ingredient::class,
            parentColumns = ["id"],
            childColumns = ["ingredientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("recipeId"), Index("ingredientId")]
)
data class RecipeIngredient(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recipeId: Long,
    val ingredientId: Long,
    val quantity: Double,
    val notes: String? = null
)

@Entity(tableName = "meal_plans")
data class MealPlan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val weekStart: Long,
    val budget: Double? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "planned_meals",
    foreignKeys = [
        ForeignKey(
            entity = MealPlan::class,
            parentColumns = ["id"],
            childColumns = ["mealPlanId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("mealPlanId"), Index("recipeId")]
)
data class PlannedMeal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mealPlanId: Long,
    val recipeId: Long,
    val dayOfWeek: Int, // 0 = Monday, 6 = Sunday
    val mealType: String // breakfast, lunch, dinner, snack
)

// Data classes for joined queries
data class RecipeWithIngredients(
    @Embedded val recipe: Recipe,
    @Relation(
        entity = RecipeIngredient::class,
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val recipeIngredients: List<RecipeIngredientWithDetails>
)

data class RecipeIngredientWithDetails(
    @Embedded val recipeIngredient: RecipeIngredient,
    @Relation(
        parentColumn = "ingredientId",
        entityColumn = "id"
    )
    val ingredient: Ingredient
)

data class MealPlanWithMeals(
    @Embedded val mealPlan: MealPlan,
    @Relation(
        entity = PlannedMeal::class,
        parentColumn = "id",
        entityColumn = "mealPlanId"
    )
    val plannedMeals: List<PlannedMealWithRecipe>
)

data class PlannedMealWithRecipe(
    @Embedded val plannedMeal: PlannedMeal,
    @Relation(
        parentColumn = "recipeId",
        entityColumn = "id"
    )
    val recipe: Recipe
)

// Category options for ingredients
object IngredientCategories {
    val categories = listOf(
        "produce" to "Produce",
        "meat" to "Meat & Poultry",
        "seafood" to "Seafood",
        "dairy" to "Dairy & Eggs",
        "grains" to "Grains & Rice",
        "spices" to "Spices & Seasonings",
        "oils" to "Oils & Fats",
        "canned" to "Canned & Preserved",
        "beverages" to "Beverages",
        "other" to "Other"
    )
}

object Units {
    val units = listOf(
        "kg" to "Kilogram",
        "g" to "Gram",
        "liter" to "Liter",
        "ml" to "Milliliter",
        "piece" to "Piece",
        "bunch" to "Bunch",
        "can" to "Can",
        "bottle" to "Bottle",
        "pack" to "Pack",
        "cup" to "Cup",
        "tbsp" to "Tablespoon",
        "tsp" to "Teaspoon"
    )
}
