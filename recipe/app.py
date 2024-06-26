from flask import Flask, request, jsonify, render_template
import pandas as pd
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.metrics.pairwise import cosine_similarity

app = Flask(__name__)

# Load the dataset
df = pd.read_csv('recipes.csv')

# Convert ingredients to a bag-of-words representation
vectorizer = CountVectorizer(tokenizer=lambda x: x.split(', '))
ingredient_matrix = vectorizer.fit_transform(df['ingredients'])

# Calculate the cosine similarity matrix
cosine_sim = cosine_similarity(ingredient_matrix)

def recommend_recipes(ingredients, df, vectorizer, cosine_sim):
    # Convert input ingredients to the same bag-of-words representation
    ingredients_str = ', '.join(ingredients)
    ingredients_vec = vectorizer.transform([ingredients_str])
    
    # Calculate the cosine similarity between the input ingredients and all recipes
    sim_scores = cosine_similarity(ingredients_vec, ingredient_matrix).flatten()
    
    # Get the indices of the top 5 most similar recipes
    sim_indices = sim_scores.argsort()[-6:-1][::-1]
    
    # Return the top 5 most similar recipes with instructions
    return df.iloc[sim_indices][['recipe_name', 'ingredients', 'instructions']].to_dict(orient='records')

@app.route('/')
def home():
    return render_template('index.html')

@app.route('/recommend', methods=['POST'])
def recommend():
    data = request.json
    ingredients = data.get('ingredients')
    if not ingredients:
        return jsonify({'error': 'Please provide a list of ingredients'}), 400
    
    recommendations = recommend_recipes(ingredients, df, vectorizer, cosine_sim)
    return jsonify({'recommendations': recommendations})

if __name__ == '__main__':
    app.run(debug=True)
