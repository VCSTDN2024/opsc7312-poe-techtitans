package com.example.fusion

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fusion.model.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// Adapter class for displaying a list of recipes in a RecyclerView
class RecipeAdapter(
    private val context: Context, // Context for accessing resources and starting activities
    private var recipeList: List<Recipe> // List of recipes to display
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    // Map to keep track of saved recipes by their IDs
    private var savedRecipes = mutableMapOf<String, Boolean>()
    private val databaseReference = FirebaseDatabase.getInstance().getReference("users")

    // Method to update the data in the adapter and refresh the RecyclerView
    fun updateData(newRecipes: List<Recipe>) {
        recipeList = newRecipes
        notifyDataSetChanged()
    }

    // ViewHolder class to represent each item view in the RecyclerView
    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recipeTitle: TextView = itemView.findViewById(R.id.tv_recipe_title) // Recipe title TextView
        private val recipeImage: ImageView = itemView.findViewById(R.id.iv_recipe_image) // Recipe image ImageView
        private val saveIcon: ImageView = itemView.findViewById(R.id.iv_save_recipe) // Save icon ImageView

        // Method to bind recipe data to the views
        fun bind(recipe: Recipe) {
            recipeTitle.text = recipe.title // Set the recipe title
            Glide.with(context).load(recipe.image).into(recipeImage) // Load the recipe image using Glide

            val userId = FirebaseAuth.getInstance().currentUser?.uid // Get the current user ID

            // Set the save icon to a default state
            saveIcon.setImageResource(R.drawable.image_group1)

            // Check if the recipe is saved in the database and update the save icon accordingly
            userId?.let { id ->
                databaseReference.child(id).child("favorites").child(recipe.id.toString())
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            // Update saved state and save icon based on database
                            val isSaved = snapshot.exists()
                            savedRecipes[recipe.id.toString()] = isSaved
                            saveIcon.setImageResource(if (isSaved) R.drawable.favoritewhite else R.drawable.image_group1)

                            // Handle save icon click event
                            handleSaveIconClick(userId, recipe, isSaved)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Display error message if database check fails
                            Toast.makeText(context, "Failed to check recipe status: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }

        // Method to handle clicking the save icon
        private fun handleSaveIconClick(userId: String, recipe: Recipe, currentSavedState: Boolean) {
            saveIcon.setOnClickListener {
                val currentSavedState = savedRecipes[recipe.id.toString()] ?: false
                val newSavedState = !currentSavedState // Toggle the saved state
                savedRecipes[recipe.id.toString()] = newSavedState
                saveIcon.setImageResource(if (newSavedState) R.drawable.favoritewhite else R.drawable.image_group1)

                if (newSavedState) {
                    // Save recipe to Firebase
                    userId.let { id ->
                        databaseReference.child(id).child("favorites").child(recipe.id.toString()).setValue(true)
                            .addOnSuccessListener {
                                // Display success message and navigate to recipe details
                                Toast.makeText(context, "Recipe saved", Toast.LENGTH_SHORT).show()
                                val intent = Intent(context, RecipeDetailsActivity::class.java)
                                intent.putExtra("RECIPE_ID", recipe.id)
                                context.startActivity(intent)
                            }
                            .addOnFailureListener { e ->
                                // Display error message if saving fails
                                Toast.makeText(context, "Failed to save recipe: ${e.message}", Toast.LENGTH_SHORT).show()
                                savedRecipes[recipe.id.toString()] = currentSavedState
                                saveIcon.setImageResource(if (currentSavedState) R.drawable.favoritewhite else R.drawable.image_group1)
                            }
                    }
                } else {
                    // Remove recipe from Firebase
                    userId.let { id ->
                        databaseReference.child(id).child("favorites").child(recipe.id.toString()).removeValue()
                            .addOnSuccessListener {
                                // Display success message for removal and remove recipe details
                                Toast.makeText(context, "Recipe removed from favorites", Toast.LENGTH_SHORT).show()
                                removeRecipeData(id, recipe.id.toString())
                            }
                            .addOnFailureListener { e ->
                                // Display error message if removal fails
                                Toast.makeText(context, "Failed to remove recipe: ${e.message}", Toast.LENGTH_SHORT).show()
                                savedRecipes[recipe.id.toString()] = currentSavedState
                                saveIcon.setImageResource(if (currentSavedState) R.drawable.favoritewhite else R.drawable.image_group1)
                            }
                    }
                }
            }
        }

        // Method to remove recipe details from Firebase
        fun removeRecipeData(userId: String, recipeId: String) {
            databaseReference.child(userId).child("recipes").child(recipeId).removeValue()
                .addOnSuccessListener {
                    // Display success message when recipe details are removed
                    Toast.makeText(context, "Recipe details also removed from database.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    // Display error message if removal of recipe details fails
                    Toast.makeText(context, "Failed to remove recipe details: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Method to create a new ViewHolder for a recipe item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recipe_item, parent, false)
        return RecipeViewHolder(view)
    }

    // Method to bind data to the ViewHolder at a specific position
    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipeList[position]
        holder.bind(recipe)

        // Set OnClickListener to navigate to recipe details when the item is clicked
        holder.itemView.setOnClickListener {
            val intent = Intent(context, RecipeDetailsActivity::class.java)
            intent.putExtra("RECIPE_ID", recipe.id)
            context.startActivity(intent)
        }
    }

    // Method to get the current list of recipes
    fun getRecipeList(): List<Recipe> {
        return recipeList
    }

    // Method to get the number of items in the recipe list
    override fun getItemCount(): Int {
        return recipeList.size
    }
}