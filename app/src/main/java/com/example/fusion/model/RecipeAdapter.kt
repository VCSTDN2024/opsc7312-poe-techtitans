
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

class RecipeAdapter(
    private val context: Context,
    private var recipeList: List<Recipe> ) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {
    // Map to track saved recipes by ID
    private var savedRecipes = mutableMapOf<String, Boolean>()
    private val databaseReference = FirebaseDatabase.getInstance().getReference("users")


    fun updateData(newRecipes: List<Recipe>) {
        recipeList = newRecipes
        notifyDataSetChanged()
    }

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recipeTitle: TextView = itemView.findViewById(R.id.tv_recipe_title)
        private val recipeImage: ImageView = itemView.findViewById(R.id.iv_recipe_image)
        private val saveIcon: ImageView = itemView.findViewById(R.id.iv_save_recipe) // Ensure this ID matches your layout

        fun bind(recipe: Recipe) {
            recipeTitle.text = recipe.title
            Glide.with(context).load(recipe.image).into(recipeImage)

            val userId = FirebaseAuth.getInstance().currentUser?.uid

            // Initialize the save icon to a default state before checking the database
            saveIcon.setImageResource(R.drawable.image_group1)

            // Check if the recipe is saved in Firebase and update the icon
            userId?.let { id ->
                databaseReference.child(id).child("favorites").child(recipe.id.toString()).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // Update local map and icon based on Firebase state
                        val isSaved = snapshot.exists()
                        savedRecipes[recipe.id.toString()] = isSaved
                        saveIcon.setImageResource(if (isSaved) R.drawable.favoritewhite else R.drawable.image_group1)

                        handleSaveIconClick(userId, recipe, isSaved)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Failed to check recipe status: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        private fun handleSaveIconClick(userId: String, recipe: Recipe, currentSavedState: Boolean) {
            saveIcon.setOnClickListener {
                val currentSavedState = savedRecipes[recipe.id.toString()] ?: false
                val newSavedState = !currentSavedState
                savedRecipes[recipe.id.toString()] = newSavedState
                saveIcon.setImageResource(if (newSavedState) R.drawable.favoritewhite else R.drawable.image_group1)

                if (newSavedState) {
                    // Save to Firebase
                    userId?.let { id ->
                        databaseReference.child(id).child("favorites").child(recipe.id.toString()).setValue(true)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Recipe saved", Toast.LENGTH_SHORT).show()
                                val intent = Intent(context, RecipeDetailsActivity::class.java)
                                intent.putExtra("RECIPE_ID", recipe.id)
                                context.startActivity(intent)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Failed to save recipe: ${e.message}", Toast.LENGTH_SHORT).show()
                                savedRecipes[recipe.id.toString()] = currentSavedState
                                saveIcon.setImageResource(if (currentSavedState) R.drawable.favoritewhite else R.drawable.image_group1)
                            }
                    }
                } else {
                    // Remove from Firebase
                    userId?.let { id ->
                        databaseReference.child(id).child("favorites").child(recipe.id.toString()).removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(context, "Recipe removed from favorites", Toast.LENGTH_SHORT).show()
                                removeRecipeData(id, recipe.id.toString())
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Failed to remove recipe: ${e.message}", Toast.LENGTH_SHORT).show()
                                savedRecipes[recipe.id.toString()] = currentSavedState
                                saveIcon.setImageResource(if (currentSavedState) R.drawable.favoritewhite else R.drawable.image_group1)
                            }
                    }
                }
            }
        }
        fun removeRecipeData(userId: String, recipeId: String) {
            // Remove the recipe details from the 'recipes' node
            databaseReference.child(userId).child("recipes").child(recipeId).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(context, "Recipe details also removed from database.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to remove recipe details: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recipe_item, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipeList[position]
        holder.bind(recipe)
        // Set OnClickListener
        holder.itemView.setOnClickListener {
            val intent = Intent(context, RecipeDetailsActivity::class.java)
            intent.putExtra("RECIPE_ID", recipe.id)
            context.startActivity(intent)
        }
    }
    fun getRecipeList(): List<Recipe> {
        return recipeList
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }
}
