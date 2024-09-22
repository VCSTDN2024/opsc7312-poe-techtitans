package com.example.fusion

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fusion.model.Nutrient

class NutrientCategoryAdapter(
    private val context: Context,
    private val categorizedNutrients: Map<String, List<Nutrient>>
) : RecyclerView.Adapter<NutrientCategoryAdapter.CategoryViewHolder>() {

    private val categories = categorizedNutrients.keys.toList()

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategoryTitle: TextView = itemView.findViewById(R.id.tv_category_title)
        val rvNutrients: RecyclerView = itemView.findViewById(R.id.rv_nutrients)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_nutrient_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int = categories.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        val nutrients = categorizedNutrients[category] ?: emptyList()

        holder.tvCategoryTitle.text = category

        // Set up Nutrients RecyclerView
        holder.rvNutrients.layoutManager = LinearLayoutManager(context)
        holder.rvNutrients.adapter = NutrientAdapter(context, nutrients)
    }
}
