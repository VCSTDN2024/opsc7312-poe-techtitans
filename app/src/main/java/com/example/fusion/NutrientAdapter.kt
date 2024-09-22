package com.example.fusion

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fusion.model.Nutrient

class NutrientAdapter(
    private val context: Context,
    private val nutrients: List<Nutrient>
) : RecyclerView.Adapter<NutrientAdapter.NutrientViewHolder>() {

    inner class NutrientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNutrientName: TextView = itemView.findViewById(R.id.tv_nutrient_name)
        val tvNutrientAmount: TextView = itemView.findViewById(R.id.tv_nutrient_amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NutrientViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_nutrient, parent, false)
        return NutrientViewHolder(view)
    }

    override fun getItemCount(): Int = nutrients.size

    override fun onBindViewHolder(holder: NutrientViewHolder, position: Int) {
        val nutrient = nutrients[position]
        holder.tvNutrientName.text = nutrient.name
        holder.tvNutrientAmount.text = "${nutrient.amount}${nutrient.unit}"
    }
}
