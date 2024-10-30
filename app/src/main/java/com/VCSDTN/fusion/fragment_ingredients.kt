package com.VCSDTN.fusion

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.VCSDTN.fusion.api.ApiClient
import com.VCSDTN.fusion.api.ApiService
import com.VCSDTN.fusion.model.AddIngredientsRequest
import com.VCSDTN.fusion.utils.TranslationUtil
import com.VCSDTN.fusion.utils.TranslationUtil.loadLanguagePreference
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IngredientsFragment : Fragment() {

    // Variables to store ingredient details and recipe ID
    private var ingredients: String? = null
    private var recipeId: Int = 0

    // UI components for displaying ingredients and the add-to-shopping-list button
    private lateinit var ingredientsText: TextView
    private lateinit var btnAddToShoppingList: Button

    // Initialize the API service using Retrofit
    private val apiService = ApiClient.retrofit.create(ApiService::class.java)

    companion object {
        // Argument keys for passing data to the fragment
        private const val ARG_INGREDIENTS = "ingredients"
        private const val ARG_RECIPE_ID = "recipe_id"

        // Factory method to create a new instance of the fragment with the provided arguments
        fun newInstance(ingredients: String, recipeId: Int): IngredientsFragment {
            val fragment = IngredientsFragment()
            val args = Bundle()
            args.putString(ARG_INGREDIENTS, ingredients)  // Pass ingredients string
            args.putInt(ARG_RECIPE_ID, recipeId)  // Pass recipe ID
            fragment.arguments = args
            return fragment
        }
    }

    // onCreate method: initializes the fragment's data from the passed arguments
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ingredients = arguments?.getString(ARG_INGREDIENTS)  // Get ingredients from arguments
        recipeId = arguments?.getInt(ARG_RECIPE_ID) ?: 0  // Get recipe ID from arguments
    }

    // onCreateView method: inflates the layout and sets up UI components
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ingredients, container, false)



        return view
    }

    private fun applyTranslations() {
        val textViewsToTranslate = listOf(
            requireView().findViewById<TextView>(R.id.tv_ingredients)
        )

        val buttonToTranslate =
            listOf(requireView().findViewById<Button>(R.id.btn_add_to_shopping_list))


        if (loadLanguagePreference(this@IngredientsFragment.requireContext()) == "af") {
            // Apply translations to these text views if necessary
            TranslationUtil.translateTextViews(
                this@IngredientsFragment.requireContext(),
                textViewsToTranslate,
                "af"
            )
            TranslationUtil.translateButtons(
                this@IngredientsFragment.requireContext(),
                buttonToTranslate,
                "af"
            )
        } else {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Find and assign the TextView and Button from the layout
        ingredientsText = view.findViewById(R.id.tv_ingredients)
        btnAddToShoppingList = view.findViewById(R.id.btn_add_to_shopping_list)

        // Display the ingredients text
        ingredientsText.text = ingredients

        // Set up button click listener to add ingredients to the shopping list
        btnAddToShoppingList.setOnClickListener {
            addIngredientsToShoppingList()
        }
        applyTranslations()
    }

    // Function to add ingredients to the user's shopping list
    private fun addIngredientsToShoppingList() {
        // Get the current user from Firebase authentication
        val user = FirebaseAuth.getInstance().currentUser
        var userID = FirebaseAuth.getInstance().currentUser?.uid
        var userToken = FirebaseAuth.getInstance().currentUser?.getIdToken(true)
        Log.d("IngredientsFragment", "User ID: $userToken")

        if (user != null) {
            // Fetch the user's ID token to authenticate the API request
            user.getIdToken(true).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Set the token for the API client
                    ApiClient.authToken = task.result?.token
                    Log.d("IngredientsFragment", "ID Token: $userID")
                    Log.d("IngredientsFragment", "recipeId: $recipeId")

                    // Make the API request to add ingredients
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            // Create the request body with user ID and recipe ID
                            val requestBody = AddIngredientsRequest(userID.toString(), recipeId)
                            Log.d("IngredientsFragment", "Request Body: $requestBody")

                            // Make the API call to add ingredients to the shopping list
                            val response = apiService.addIngredientsToShoppingList(requestBody)
                            Log.d("IngredientsFragment", "Response Code: ${response.code()}")

                            if (response.isSuccessful) {
                                // Display success message to the user
                                val message = response.body()?.message
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            } else {
                                // Handle the error response
                                val errorBody = response.errorBody()?.string()
                                Log.e("IngredientsFragment", "Error Response: $errorBody")
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Error: $errorBody",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } catch (e: Exception) {
                            // Handle any network or API exceptions
                            Log.e("IngredientsFragment", "Exception: ${e.message}", e)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    requireContext(),
                                    "Network Error: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } else {
                    // Handle error: unable to get the ID token
                    Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else {
            // If the user is not logged in, display a message
            Toast.makeText(
                requireContext(),
                "Please log in to add items to your shopping list.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
