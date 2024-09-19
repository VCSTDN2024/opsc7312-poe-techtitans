package com.example.fusion

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fusion.model.Recipe

class RecipeAdapter(
    // Context for inflating layouts
    private val context: Context,
    // The list of recipes to display
    private val recipeList: List<Recipe>
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    // Inner class for creating ViewHolder objects (one for each recipe item)
    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Reference to the TextView and ImageView in the recipe_item.xml
        val recipeTitle: TextView = itemView.findViewById(R.id.tv_recipe_title)
        val recipeImage: ImageView = itemView.findViewById(R.id.iv_recipe_image)
    }

    // Called when RecyclerView needs a new ViewHolder (new recipe card)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        // Inflate the recipe_item layout and create the ViewHolder
        val view = LayoutInflater.from(context).inflate(R.layout.recipe_item, parent, false)
        return RecipeViewHolder(view)
    }

    // Called by RecyclerView to display data at the specified position in the list
    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipeList[position]  // Get the recipe at the given position

        // Bind data to the view components (recipe title and image)
        holder.recipeTitle.text = recipe.title
        Glide.with(context).load(recipe.image).into(holder.recipeImage)
    }

    // Return the size of the recipe list (number of items to display)
    override fun getItemCount(): Int {
        return recipeList.size
    }
}
