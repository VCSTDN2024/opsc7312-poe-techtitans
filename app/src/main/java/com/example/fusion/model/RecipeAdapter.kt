package com.example.fusion

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fusion.model.Recipe

class RecipeAdapter(
    private val context: Context,
    private var recipeList: List<Recipe> ) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    fun updateData(newRecipes: List<Recipe>) {
        recipeList = newRecipes
        notifyDataSetChanged()
    }

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recipeTitle: TextView = itemView.findViewById(R.id.tv_recipe_title)
        val recipeImage: ImageView = itemView.findViewById(R.id.iv_recipe_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recipe_item, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipeList[position]

        holder.recipeTitle.text = recipe.title
        Glide.with(context).load(recipe.image).into(holder.recipeImage)

        // Set OnClickListener
        holder.itemView.setOnClickListener {
            val intent = Intent(context, RecipeDetailsActivity::class.java)
            intent.putExtra("RECIPE_ID", recipe.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }
}
