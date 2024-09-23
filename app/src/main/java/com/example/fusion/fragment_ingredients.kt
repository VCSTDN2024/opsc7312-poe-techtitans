package com.example.fusion

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fusion.api.ApiClient
import com.example.fusion.api.ApiService
import com.example.fusion.model.AddIngredientsRequest
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IngredientsFragment : Fragment() {

    private var ingredients: String? = null
    private var recipeId: Int = 0

    private lateinit var ingredientsText: TextView
    private lateinit var btnAddToShoppingList: Button

    private val apiService = ApiClient.retrofit.create(ApiService::class.java)

    companion object {
        private const val ARG_INGREDIENTS = "ingredients"
        private const val ARG_RECIPE_ID = "recipe_id"

        fun newInstance(ingredients: String, recipeId: Int): IngredientsFragment {
            val fragment = IngredientsFragment()
            val args = Bundle()
            args.putString(ARG_INGREDIENTS, ingredients)
            args.putInt(ARG_RECIPE_ID, recipeId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ingredients = arguments?.getString(ARG_INGREDIENTS)
        recipeId = arguments?.getInt(ARG_RECIPE_ID) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ingredients, container, false)
        ingredientsText = view.findViewById(R.id.tv_ingredients)
        btnAddToShoppingList = view.findViewById(R.id.btn_add_to_shopping_list)

        ingredientsText.text = ingredients

        btnAddToShoppingList.setOnClickListener {
            addIngredientsToShoppingList()
        }

        return view
    }

    private fun addIngredientsToShoppingList() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            user.getIdToken(true).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    ApiClient.authToken = task.result?.token
                    Log.d("IngredientsFragment", "ID Token: ${ApiClient.authToken}")
                    Log.d("IngredientsFragment", "recipeId: $recipeId")
                    // Proceed with API request
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val requestBody = AddIngredientsRequest(recipeId)
                            Log.d("IngredientsFragment", "Request Body: $requestBody")
                            val response = apiService.addIngredientsToShoppingList(requestBody)
                            Log.d("IngredientsFragment", "Response Code: ${response.code()}")
                            if (response.isSuccessful) {
                                val message = response.body()?.message
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                val errorBody = response.errorBody()?.string()
                                Log.e("IngredientsFragment", "Error Response: $errorBody")
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(requireContext(), "Error: $errorBody", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("IngredientsFragment", "Exception: ${e.message}", e)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), "Network Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    // Handle error: unable to get ID token
                    Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // User not logged in
            Toast.makeText(requireContext(), "Please log in to add items to your shopping list.", Toast.LENGTH_SHORT).show()
        }
    }

}
