from flask_wtf import FlaskForm
from wtforms import (StringField, FloatField, IntegerField, TextAreaField,
                     SelectField, DateField, FieldList, FormField)
from wtforms.validators import DataRequired, NumberRange, Optional


INGREDIENT_CATEGORIES = [
    ('produce', 'Produce (Fruits & Vegetables)'),
    ('meat', 'Meat & Poultry'),
    ('seafood', 'Seafood'),
    ('dairy', 'Dairy & Eggs'),
    ('grains', 'Grains & Rice'),
    ('spices', 'Spices & Seasonings'),
    ('oils', 'Oils & Fats'),
    ('canned', 'Canned & Preserved'),
    ('beverages', 'Beverages'),
    ('other', 'Other'),
]

UNIT_CHOICES = [
    ('kg', 'Kilogram (kg)'),
    ('g', 'Gram (g)'),
    ('liter', 'Liter (L)'),
    ('ml', 'Milliliter (ml)'),
    ('piece', 'Piece'),
    ('bunch', 'Bunch'),
    ('can', 'Can'),
    ('bottle', 'Bottle'),
    ('pack', 'Pack'),
    ('cup', 'Cup'),
    ('tbsp', 'Tablespoon'),
    ('tsp', 'Teaspoon'),
]

MEAL_TYPES = [
    ('breakfast', 'Breakfast'),
    ('lunch', 'Lunch'),
    ('dinner', 'Dinner'),
    ('snack', 'Snack'),
]


class IngredientForm(FlaskForm):
    name = StringField('Ingredient Name', validators=[DataRequired()])
    category = SelectField('Category', choices=INGREDIENT_CATEGORIES, validators=[DataRequired()])
    unit = SelectField('Unit', choices=UNIT_CHOICES, validators=[DataRequired()])
    price_per_unit = FloatField('Price per Unit (SRD)', validators=[DataRequired(), NumberRange(min=0)])
    notes = TextAreaField('Notes (price source, alternatives, etc.)', validators=[Optional()])


class RecipeIngredientForm(FlaskForm):
    ingredient_id = SelectField('Ingredient', coerce=int, validators=[DataRequired()])
    quantity = FloatField('Quantity', validators=[DataRequired(), NumberRange(min=0)])
    notes = StringField('Notes (e.g., chopped, optional)', validators=[Optional()])


class RecipeForm(FlaskForm):
    name = StringField('Recipe Name', validators=[DataRequired()])
    description = TextAreaField('Description', validators=[Optional()])
    servings = IntegerField('Number of Servings', validators=[DataRequired(), NumberRange(min=1)], default=4)
    prep_time_minutes = IntegerField('Prep Time (minutes)', validators=[Optional(), NumberRange(min=0)])
    cook_time_minutes = IntegerField('Cook Time (minutes)', validators=[Optional(), NumberRange(min=0)])
    instructions = TextAreaField('Instructions', validators=[Optional()])


class MealPlanForm(FlaskForm):
    name = StringField('Plan Name', validators=[DataRequired()])
    week_start = DateField('Week Starting', validators=[DataRequired()])
    budget = FloatField('Weekly Budget (SRD)', validators=[Optional(), NumberRange(min=0)])


class PlannedMealForm(FlaskForm):
    recipe_id = SelectField('Recipe', coerce=int, validators=[DataRequired()])
    day_of_week = SelectField('Day', coerce=int, choices=[
        (0, 'Monday'), (1, 'Tuesday'), (2, 'Wednesday'), (3, 'Thursday'),
        (4, 'Friday'), (5, 'Saturday'), (6, 'Sunday')
    ], validators=[DataRequired()])
    meal_type = SelectField('Meal', choices=MEAL_TYPES, validators=[DataRequired()])
