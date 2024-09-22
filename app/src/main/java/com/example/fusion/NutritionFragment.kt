package com.example.fusion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fusion.model.Nutrition
import java.io.Serializable

class NutritionFragment : Fragment() {

    private var nutrition: Nutrition? = null
    private lateinit var rvNutritionCategories: RecyclerView

    companion object {
        private const val ARG_NUTRITION = "nutrition"

        fun newInstance(nutrition: Nutrition): NutritionFragment {
            val fragment = NutritionFragment()
            val args = Bundle()
            args.putSerializable(ARG_NUTRITION, nutrition as Serializable)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nutrition = arguments?.getSerializable(ARG_NUTRITION) as? Nutrition
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_nutrition, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rvNutritionCategories = view.findViewById(R.id.rv_nutrition_categories)
        nutrition?.let {
            val categorizedNutrients = it.getCategorizedNutrients()
            rvNutritionCategories.layoutManager = LinearLayoutManager(requireContext())
            rvNutritionCategories.adapter = NutrientCategoryAdapter(requireContext(), categorizedNutrients)
        } ?: run {
            // Handle case where nutrition data is not available
            Toast.makeText(requireContext(), "No nutrition information available", Toast.LENGTH_SHORT).show()
        }
    }
}
