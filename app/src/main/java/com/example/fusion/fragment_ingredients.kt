package com.example.fusion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class IngredientsFragment : Fragment() {

    private var ingredients: String? = null

    companion object {
        private const val ARG_INGREDIENTS = "ingredients"

        fun newInstance(ingredients: String): IngredientsFragment {
            val fragment = IngredientsFragment()
            val args = Bundle()
            args.putString(ARG_INGREDIENTS, ingredients)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ingredients = arguments?.getString(ARG_INGREDIENTS)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ingredients, container, false)
        val ingredientsText: TextView = view.findViewById(R.id.tv_ingredients)
        ingredientsText.text = ingredients
        return view
    }
}
