# Recipe Cost Calculator & Meal Planner

A Flask-based web application designed for Suriname, helping you track ingredient costs, calculate recipe expenses, and plan weekly meals within your budget.

## Features

- **Ingredient Management**: Track local ingredient prices in Surinamese Dollar (SRD) with price history
- **Recipe Cost Calculator**: Automatically calculate total cost and cost-per-serving for recipes
- **Weekly Meal Planner**: Plan your weekly meals with budget tracking
- **Shopping List Generator**: Generate consolidated shopping lists from meal plans
- **Cost-Effective Tracking**: See which recipes give you the best value per serving

## Installation

1. Create a virtual environment:
   ```bash
   python -m venv venv
   source venv/bin/activate  # On Windows: venv\Scripts\activate
   ```

2. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```

3. Run the application:
   ```bash
   python app.py
   ```

4. Open http://localhost:5000 in your browser

## Usage

1. **Add Ingredients**: Start by adding ingredients with their current local prices
2. **Create Recipes**: Add recipes and assign ingredients with quantities
3. **Plan Meals**: Create weekly meal plans and add recipes to each day
4. **Generate Shopping Lists**: View consolidated shopping lists for your meal plans

## Sample Data

The application comes pre-loaded with common Surinamese ingredients and their approximate prices to help you get started.

## Tech Stack

- Flask 3.0
- Flask-SQLAlchemy (SQLite database)
- Flask-WTF (Form handling)
- Vanilla HTML/CSS (No JavaScript framework required)

## License

MIT
