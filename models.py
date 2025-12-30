from datetime import datetime, date
from flask_sqlalchemy import SQLAlchemy

db = SQLAlchemy()


class Ingredient(db.Model):
    """Ingredient with pricing information for Suriname market."""
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False, unique=True)
    category = db.Column(db.String(50), nullable=False)  # e.g., 'produce', 'meat', 'dairy'
    unit = db.Column(db.String(20), nullable=False)  # e.g., 'kg', 'liter', 'piece'
    price_per_unit = db.Column(db.Float, nullable=False)  # Price in SRD
    last_updated = db.Column(db.DateTime, default=datetime.utcnow)
    notes = db.Column(db.Text)  # For noting price fluctuations, sources, etc.

    # Track price history for fluctuation awareness
    price_history = db.relationship('PriceHistory', backref='ingredient', lazy='dynamic',
                                    cascade='all, delete-orphan')

    def __repr__(self):
        return f'<Ingredient {self.name}>'


class PriceHistory(db.Model):
    """Track ingredient price changes over time."""
    id = db.Column(db.Integer, primary_key=True)
    ingredient_id = db.Column(db.Integer, db.ForeignKey('ingredient.id'), nullable=False)
    price = db.Column(db.Float, nullable=False)
    recorded_at = db.Column(db.DateTime, default=datetime.utcnow)
    source = db.Column(db.String(100))  # Where the price was found


class Recipe(db.Model):
    """Recipe with cost calculation."""
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(200), nullable=False)
    description = db.Column(db.Text)
    servings = db.Column(db.Integer, nullable=False, default=4)
    prep_time_minutes = db.Column(db.Integer)
    cook_time_minutes = db.Column(db.Integer)
    instructions = db.Column(db.Text)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    # Ingredients for this recipe
    ingredients = db.relationship('RecipeIngredient', backref='recipe', lazy='dynamic',
                                  cascade='all, delete-orphan')

    @property
    def total_cost(self):
        """Calculate total recipe cost based on current ingredient prices."""
        total = 0
        for ri in self.ingredients:
            if ri.ingredient:
                total += ri.quantity * ri.ingredient.price_per_unit
        return total

    @property
    def cost_per_serving(self):
        """Calculate cost per serving."""
        if self.servings > 0:
            return self.total_cost / self.servings
        return 0

    def __repr__(self):
        return f'<Recipe {self.name}>'


class RecipeIngredient(db.Model):
    """Association between Recipe and Ingredient with quantity."""
    id = db.Column(db.Integer, primary_key=True)
    recipe_id = db.Column(db.Integer, db.ForeignKey('recipe.id'), nullable=False)
    ingredient_id = db.Column(db.Integer, db.ForeignKey('ingredient.id'), nullable=False)
    quantity = db.Column(db.Float, nullable=False)  # In the ingredient's unit
    notes = db.Column(db.String(100))  # e.g., "chopped", "optional"

    ingredient = db.relationship('Ingredient')


class MealPlan(db.Model):
    """Weekly meal plan with budget tracking."""
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    week_start = db.Column(db.Date, nullable=False)
    budget = db.Column(db.Float)  # Weekly budget in SRD
    created_at = db.Column(db.DateTime, default=datetime.utcnow)

    # Planned meals
    meals = db.relationship('PlannedMeal', backref='meal_plan', lazy='dynamic',
                           cascade='all, delete-orphan')

    @property
    def total_cost(self):
        """Calculate total cost of all planned meals."""
        return sum(pm.recipe.total_cost for pm in self.meals if pm.recipe)

    @property
    def remaining_budget(self):
        """Calculate remaining budget."""
        if self.budget:
            return self.budget - self.total_cost
        return None

    def __repr__(self):
        return f'<MealPlan {self.name}>'


class PlannedMeal(db.Model):
    """A meal planned for a specific day and meal type."""
    id = db.Column(db.Integer, primary_key=True)
    meal_plan_id = db.Column(db.Integer, db.ForeignKey('meal_plan.id'), nullable=False)
    recipe_id = db.Column(db.Integer, db.ForeignKey('recipe.id'), nullable=False)
    day_of_week = db.Column(db.Integer, nullable=False)  # 0=Monday, 6=Sunday
    meal_type = db.Column(db.String(20), nullable=False)  # 'breakfast', 'lunch', 'dinner'

    recipe = db.relationship('Recipe')

    DAY_NAMES = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday']

    @property
    def day_name(self):
        return self.DAY_NAMES[self.day_of_week] if 0 <= self.day_of_week <= 6 else 'Unknown'
