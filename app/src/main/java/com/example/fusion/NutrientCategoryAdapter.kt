package com.example.fusion

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fusion.model.Nutrient

// Adapter class for managing categories of nutrients in a RecyclerView.
class NutrientCategoryAdapter(
    private val context: Context, // Context for accessing system resources.
    private val categorizedNutrients: Map<String, List<Nutrient>> // Map of nutrient categories to nutrient lists.
) : RecyclerView.Adapter<NutrientCategoryAdapter.CategoryViewHolder>() {

    // Extract categories from the map keys and convert to list for easy indexing.
    private val categories = categorizedNutrients.keys.toList()

    // ViewHolder class for holding category data.
    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategoryTitle: TextView =
            itemView.findViewById(R.id.tv_category_title) // TextView for displaying the category title.
        val rvNutrients: RecyclerView =
            itemView.findViewById(R.id.rv_nutrients) // RecyclerView for displaying nutrients in this category.
    }

    // Creates new ViewHolder instances for each category.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.item_nutrient_category,
            parent,
            false
        ) // Inflate layout for each category item.
        return CategoryViewHolder(view)
    }

    // Returns the number of categories.
    override fun getItemCount(): Int = categories.size

    // Binds data to each ViewHolder, setting up nested RecyclerViews for nutrients.
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position] // Get category name based on position.
        val nutrients = categorizedNutrients[category]
            ?: emptyList() // Retrieve nutrients for this category or an empty list if none.

        holder.tvCategoryTitle.text = category // Set the category title in the TextView.

        // Set up a LinearLayoutManager and adapter for the nested RecyclerView.
        holder.rvNutrients.layoutManager = LinearLayoutManager(context)
        holder.rvNutrients.adapter = NutrientAdapter(
            context,
            nutrients
        ) // Use NutrientAdapter for nutrients within the category.
    }
}
