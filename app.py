from datetime import datetime, timedelta
from collections import defaultdict
from flask import Flask, render_template, request, redirect, url_for, flash, jsonify
from config import Config
from models import db, Ingredient, PriceHistory, Recipe, RecipeIngredient, MealPlan, PlannedMeal
from forms import (IngredientForm, RecipeForm, RecipeIngredientForm,
                   MealPlanForm, PlannedMealForm, INGREDIENT_CATEGORIES)

app = Flask(__name__)
app.config.from_object(Config)
db.init_app(app)


@app.context_processor
def utility_processor():
    """Make utility functions available to all templates."""
    def format_currency(amount):
        return f"SRD {amount:,.2f}"
    return dict(format_currency=format_currency)


# ============== Home ==============
@app.route('/')
def index():
    """Dashboard with overview of recipes, costs, and meal plans."""
    recipes = Recipe.query.order_by(Recipe.created_at.desc()).limit(5).all()
    ingredients = Ingredient.query.order_by(Ingredient.last_updated.desc()).limit(5).all()
    meal_plans = MealPlan.query.order_by(MealPlan.week_start.desc()).limit(3).all()

    # Calculate some stats
    total_recipes = Recipe.query.count()
    total_ingredients = Ingredient.query.count()

    # Find most cost-effective recipes
    all_recipes = Recipe.query.all()
    cost_effective = sorted(all_recipes, key=lambda r: r.cost_per_serving if r.cost_per_serving else float('inf'))[:5]

    return render_template('index.html',
                          recipes=recipes,
                          ingredients=ingredients,
                          meal_plans=meal_plans,
                          total_recipes=total_recipes,
                          total_ingredients=total_ingredients,
                          cost_effective=cost_effective)


# ============== Ingredients ==============
@app.route('/ingredients')
def ingredient_list():
    """List all ingredients with current prices."""
    category = request.args.get('category', '')
    sort = request.args.get('sort', 'name')

    query = Ingredient.query

    if category:
        query = query.filter_by(category=category)

    if sort == 'price':
        query = query.order_by(Ingredient.price_per_unit.desc())
    elif sort == 'updated':
        query = query.order_by(Ingredient.last_updated.desc())
    else:
        query = query.order_by(Ingredient.name)

    ingredients = query.all()
    return render_template('ingredients/list.html',
                          ingredients=ingredients,
                          categories=INGREDIENT_CATEGORIES,
                          current_category=category,
                          current_sort=sort)


@app.route('/ingredients/add', methods=['GET', 'POST'])
def ingredient_add():
    """Add a new ingredient."""
    form = IngredientForm()

    if form.validate_on_submit():
        ingredient = Ingredient(
            name=form.name.data,
            category=form.category.data,
            unit=form.unit.data,
            price_per_unit=form.price_per_unit.data,
            notes=form.notes.data
        )
        db.session.add(ingredient)

        # Record initial price in history
        price_record = PriceHistory(
            ingredient=ingredient,
            price=form.price_per_unit.data,
            source='Initial entry'
        )
        db.session.add(price_record)

        db.session.commit()
        flash(f'Ingredient "{ingredient.name}" added successfully!', 'success')
        return redirect(url_for('ingredient_list'))

    return render_template('ingredients/form.html', form=form, title='Add Ingredient')


@app.route('/ingredients/<int:id>/edit', methods=['GET', 'POST'])
def ingredient_edit(id):
    """Edit an ingredient."""
    ingredient = Ingredient.query.get_or_404(id)
    form = IngredientForm(obj=ingredient)
    old_price = ingredient.price_per_unit

    if form.validate_on_submit():
        form.populate_obj(ingredient)
        ingredient.last_updated = datetime.utcnow()

        # Record price change if different
        if old_price != form.price_per_unit.data:
            price_record = PriceHistory(
                ingredient=ingredient,
                price=form.price_per_unit.data,
                source='Price update'
            )
            db.session.add(price_record)

        db.session.commit()
        flash(f'Ingredient "{ingredient.name}" updated!', 'success')
        return redirect(url_for('ingredient_list'))

    return render_template('ingredients/form.html', form=form,
                          title='Edit Ingredient', ingredient=ingredient)


@app.route('/ingredients/<int:id>/delete', methods=['POST'])
def ingredient_delete(id):
    """Delete an ingredient."""
    ingredient = Ingredient.query.get_or_404(id)
    name = ingredient.name
    db.session.delete(ingredient)
    db.session.commit()
    flash(f'Ingredient "{name}" deleted.', 'info')
    return redirect(url_for('ingredient_list'))


@app.route('/ingredients/<int:id>/history')
def ingredient_history(id):
    """View price history for an ingredient."""
    ingredient = Ingredient.query.get_or_404(id)
    history = ingredient.price_history.order_by(PriceHistory.recorded_at.desc()).all()
    return render_template('ingredients/history.html', ingredient=ingredient, history=history)


# ============== Recipes ==============
@app.route('/recipes')
def recipe_list():
    """List all recipes with cost information."""
    sort = request.args.get('sort', 'name')

    recipes = Recipe.query.all()

    if sort == 'cost':
        recipes = sorted(recipes, key=lambda r: r.total_cost)
    elif sort == 'serving':
        recipes = sorted(recipes, key=lambda r: r.cost_per_serving)
    elif sort == 'date':
        recipes = sorted(recipes, key=lambda r: r.created_at, reverse=True)
    else:
        recipes = sorted(recipes, key=lambda r: r.name)

    return render_template('recipes/list.html', recipes=recipes, current_sort=sort)


@app.route('/recipes/add', methods=['GET', 'POST'])
def recipe_add():
    """Add a new recipe."""
    form = RecipeForm()

    if form.validate_on_submit():
        recipe = Recipe(
            name=form.name.data,
            description=form.description.data,
            servings=form.servings.data,
            prep_time_minutes=form.prep_time_minutes.data,
            cook_time_minutes=form.cook_time_minutes.data,
            instructions=form.instructions.data
        )
        db.session.add(recipe)
        db.session.commit()
        flash(f'Recipe "{recipe.name}" created! Now add ingredients.', 'success')
        return redirect(url_for('recipe_edit', id=recipe.id))

    return render_template('recipes/form.html', form=form, title='Add Recipe')


@app.route('/recipes/<int:id>')
def recipe_view(id):
    """View a recipe with full details and cost breakdown."""
    recipe = Recipe.query.get_or_404(id)
    return render_template('recipes/view.html', recipe=recipe)


@app.route('/recipes/<int:id>/edit', methods=['GET', 'POST'])
def recipe_edit(id):
    """Edit a recipe."""
    recipe = Recipe.query.get_or_404(id)
    form = RecipeForm(obj=recipe)
    ingredients = Ingredient.query.order_by(Ingredient.name).all()

    if form.validate_on_submit():
        form.populate_obj(recipe)
        db.session.commit()
        flash(f'Recipe "{recipe.name}" updated!', 'success')
        return redirect(url_for('recipe_view', id=recipe.id))

    return render_template('recipes/edit.html', form=form, recipe=recipe,
                          ingredients=ingredients, title='Edit Recipe')


@app.route('/recipes/<int:id>/ingredient/add', methods=['POST'])
def recipe_ingredient_add(id):
    """Add an ingredient to a recipe."""
    recipe = Recipe.query.get_or_404(id)

    ingredient_id = request.form.get('ingredient_id', type=int)
    quantity = request.form.get('quantity', type=float)
    notes = request.form.get('notes', '')

    if ingredient_id and quantity:
        ri = RecipeIngredient(
            recipe_id=recipe.id,
            ingredient_id=ingredient_id,
            quantity=quantity,
            notes=notes
        )
        db.session.add(ri)
        db.session.commit()
        flash('Ingredient added to recipe.', 'success')

    return redirect(url_for('recipe_edit', id=recipe.id))


@app.route('/recipes/<int:recipe_id>/ingredient/<int:ri_id>/delete', methods=['POST'])
def recipe_ingredient_delete(recipe_id, ri_id):
    """Remove an ingredient from a recipe."""
    ri = RecipeIngredient.query.get_or_404(ri_id)
    db.session.delete(ri)
    db.session.commit()
    flash('Ingredient removed from recipe.', 'info')
    return redirect(url_for('recipe_edit', id=recipe_id))


@app.route('/recipes/<int:id>/delete', methods=['POST'])
def recipe_delete(id):
    """Delete a recipe."""
    recipe = Recipe.query.get_or_404(id)
    name = recipe.name
    db.session.delete(recipe)
    db.session.commit()
    flash(f'Recipe "{name}" deleted.', 'info')
    return redirect(url_for('recipe_list'))


# ============== Meal Plans ==============
@app.route('/meal-plans')
def meal_plan_list():
    """List all meal plans."""
    meal_plans = MealPlan.query.order_by(MealPlan.week_start.desc()).all()
    return render_template('meal_plans/list.html', meal_plans=meal_plans)


@app.route('/meal-plans/add', methods=['GET', 'POST'])
def meal_plan_add():
    """Create a new meal plan."""
    form = MealPlanForm()

    if form.validate_on_submit():
        meal_plan = MealPlan(
            name=form.name.data,
            week_start=form.week_start.data,
            budget=form.budget.data
        )
        db.session.add(meal_plan)
        db.session.commit()
        flash(f'Meal plan "{meal_plan.name}" created!', 'success')
        return redirect(url_for('meal_plan_edit', id=meal_plan.id))

    return render_template('meal_plans/form.html', form=form, title='Create Meal Plan')


@app.route('/meal-plans/<int:id>')
def meal_plan_view(id):
    """View a meal plan."""
    meal_plan = MealPlan.query.get_or_404(id)

    # Organize meals by day
    meals_by_day = defaultdict(lambda: {'breakfast': None, 'lunch': None, 'dinner': None, 'snack': None})
    for meal in meal_plan.meals:
        meals_by_day[meal.day_of_week][meal.meal_type] = meal

    return render_template('meal_plans/view.html', meal_plan=meal_plan, meals_by_day=dict(meals_by_day))


@app.route('/meal-plans/<int:id>/edit', methods=['GET', 'POST'])
def meal_plan_edit(id):
    """Edit a meal plan."""
    meal_plan = MealPlan.query.get_or_404(id)
    form = MealPlanForm(obj=meal_plan)
    recipes = Recipe.query.order_by(Recipe.name).all()

    # Organize existing meals by day
    meals_by_day = defaultdict(lambda: {'breakfast': None, 'lunch': None, 'dinner': None, 'snack': None})
    for meal in meal_plan.meals:
        meals_by_day[meal.day_of_week][meal.meal_type] = meal

    if form.validate_on_submit():
        form.populate_obj(meal_plan)
        db.session.commit()
        flash(f'Meal plan updated!', 'success')
        return redirect(url_for('meal_plan_view', id=meal_plan.id))

    return render_template('meal_plans/edit.html', form=form, meal_plan=meal_plan,
                          recipes=recipes, meals_by_day=dict(meals_by_day))


@app.route('/meal-plans/<int:id>/meal/add', methods=['POST'])
def meal_plan_add_meal(id):
    """Add a meal to the plan."""
    meal_plan = MealPlan.query.get_or_404(id)

    recipe_id = request.form.get('recipe_id', type=int)
    day_of_week = request.form.get('day_of_week', type=int)
    meal_type = request.form.get('meal_type')

    if recipe_id and day_of_week is not None and meal_type:
        # Check if there's already a meal for this slot
        existing = PlannedMeal.query.filter_by(
            meal_plan_id=meal_plan.id,
            day_of_week=day_of_week,
            meal_type=meal_type
        ).first()

        if existing:
            existing.recipe_id = recipe_id
        else:
            meal = PlannedMeal(
                meal_plan_id=meal_plan.id,
                recipe_id=recipe_id,
                day_of_week=day_of_week,
                meal_type=meal_type
            )
            db.session.add(meal)

        db.session.commit()
        flash('Meal added to plan.', 'success')

    return redirect(url_for('meal_plan_edit', id=meal_plan.id))


@app.route('/meal-plans/<int:plan_id>/meal/<int:meal_id>/delete', methods=['POST'])
def meal_plan_remove_meal(plan_id, meal_id):
    """Remove a meal from the plan."""
    meal = PlannedMeal.query.get_or_404(meal_id)
    db.session.delete(meal)
    db.session.commit()
    flash('Meal removed from plan.', 'info')
    return redirect(url_for('meal_plan_edit', id=plan_id))


@app.route('/meal-plans/<int:id>/delete', methods=['POST'])
def meal_plan_delete(id):
    """Delete a meal plan."""
    meal_plan = MealPlan.query.get_or_404(id)
    name = meal_plan.name
    db.session.delete(meal_plan)
    db.session.commit()
    flash(f'Meal plan "{name}" deleted.', 'info')
    return redirect(url_for('meal_plan_list'))


# ============== Shopping List ==============
@app.route('/meal-plans/<int:id>/shopping-list')
def shopping_list(id):
    """Generate shopping list for a meal plan."""
    meal_plan = MealPlan.query.get_or_404(id)

    # Aggregate ingredients across all meals
    ingredient_totals = defaultdict(lambda: {'quantity': 0, 'unit': '', 'cost': 0, 'ingredient': None})

    for planned_meal in meal_plan.meals:
        for ri in planned_meal.recipe.ingredients:
            ing = ri.ingredient
            key = ing.id
            ingredient_totals[key]['quantity'] += ri.quantity
            ingredient_totals[key]['unit'] = ing.unit
            ingredient_totals[key]['cost'] = ingredient_totals[key]['quantity'] * ing.price_per_unit
            ingredient_totals[key]['ingredient'] = ing

    # Sort by category then name
    shopping_items = sorted(
        ingredient_totals.values(),
        key=lambda x: (x['ingredient'].category, x['ingredient'].name)
    )

    total_cost = sum(item['cost'] for item in shopping_items)

    return render_template('meal_plans/shopping_list.html',
                          meal_plan=meal_plan,
                          shopping_items=shopping_items,
                          total_cost=total_cost)


# ============== API Endpoints ==============
@app.route('/api/ingredients')
def api_ingredients():
    """API endpoint for ingredients (for AJAX)."""
    ingredients = Ingredient.query.order_by(Ingredient.name).all()
    return jsonify([{
        'id': i.id,
        'name': i.name,
        'unit': i.unit,
        'price_per_unit': i.price_per_unit
    } for i in ingredients])


@app.route('/api/recipes/<int:id>/cost')
def api_recipe_cost(id):
    """API endpoint for recipe cost calculation."""
    recipe = Recipe.query.get_or_404(id)
    return jsonify({
        'total_cost': recipe.total_cost,
        'cost_per_serving': recipe.cost_per_serving,
        'servings': recipe.servings
    })


# ============== Initialize Database ==============
def init_db():
    """Initialize the database with sample data for Suriname."""
    with app.app_context():
        db.create_all()

        # Check if we already have data
        if Ingredient.query.count() > 0:
            return

        # Sample Surinamese ingredients with typical prices (in SRD)
        sample_ingredients = [
            ('Rice (local)', 'grains', 'kg', 45.00),
            ('Chicken', 'meat', 'kg', 85.00),
            ('Cassava', 'produce', 'kg', 25.00),
            ('Plantain', 'produce', 'piece', 8.00),
            ('Onion', 'produce', 'kg', 35.00),
            ('Tomato', 'produce', 'kg', 40.00),
            ('Garlic', 'produce', 'kg', 120.00),
            ('Cooking Oil', 'oils', 'liter', 55.00),
            ('Salt', 'spices', 'kg', 15.00),
            ('Black Pepper', 'spices', 'g', 0.50),
            ('Eggs', 'dairy', 'piece', 4.50),
            ('Milk', 'dairy', 'liter', 25.00),
            ('Fish (fresh)', 'seafood', 'kg', 95.00),
            ('Dried Shrimp', 'seafood', 'g', 0.80),
            ('Coconut Milk', 'canned', 'can', 18.00),
            ('Beans (dried)', 'grains', 'kg', 38.00),
        ]

        for name, category, unit, price in sample_ingredients:
            ingredient = Ingredient(name=name, category=category, unit=unit, price_per_unit=price)
            db.session.add(ingredient)
            # Add initial price history
            db.session.add(PriceHistory(ingredient=ingredient, price=price, source='Initial'))

        db.session.commit()
        print("Database initialized with sample Surinamese ingredients!")


# Initialize database on app startup (works with gunicorn too)
with app.app_context():
    db.create_all()
    # Add sample data if database is empty
    if Ingredient.query.count() == 0:
        init_db()


if __name__ == '__main__':
    app.run(debug=True)
