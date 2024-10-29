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

// Fragment class to display nutrition details.
class NutritionFragment : Fragment() {

    private var nutrition: Nutrition? = null
    private lateinit var rvNutritionCategories: RecyclerView

    // Companion object to hold constants and provide a method to create a new fragment instance.
    companion object {
        private const val ARG_NUTRITION = "nutrition"

        // Factory method to create a new instance of NutritionFragment with nutrition data.
        fun newInstance(nutrition: Nutrition): NutritionFragment {
            val fragment = NutritionFragment()
            val args = Bundle()
            args.putSerializable(ARG_NUTRITION, nutrition as Serializable)
            fragment.arguments = args
            return fragment
        }
    }

    // Retrieves nutrition data from fragment arguments.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nutrition = arguments?.getSerializable(ARG_NUTRITION) as? Nutrition
    }

    // Inflates the fragment's layout.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_nutrition, container, false)
    }

    // Setup RecyclerView and adapter to display nutrition categories.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rvNutritionCategories = view.findViewById(R.id.rv_nutrition_categories)
        nutrition?.let {
            val categorizedNutrients = it.getCategorizedNutrients()
            rvNutritionCategories.layoutManager = LinearLayoutManager(requireContext())
            rvNutritionCategories.adapter =
                NutrientCategoryAdapter(requireContext(), categorizedNutrients)
        } ?: run {
            // Display a toast message if no nutrition data is available.
            Toast.makeText(
                requireContext(),
                "No nutrition information available",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
