package com.VCSDTN.fusion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.VCSDTN.fusion.utils.TranslationUtil
import com.VCSDTN.fusion.utils.TranslationUtil.loadLanguagePreference

class MealPlannerActivity : AppCompatActivity() {

    // Variable to store the recipe ID, initialized to -1 (default invalid value)
    private var recipeId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_planner)

        // Retrieve the recipe ID passed through the intent from another activity
        recipeId = intent.getIntExtra("RECIPE_ID", -1)

        // Set up click listeners for each day of the week, linking them to the respective CardViews
        findViewById<CardView>(R.id.cardSunday).setOnClickListener { openMealTime("Sunday") }
        findViewById<CardView>(R.id.cardMonday).setOnClickListener { openMealTime("Monday") }
        findViewById<CardView>(R.id.cardTuesday).setOnClickListener { openMealTime("Tuesday") }
        findViewById<CardView>(R.id.cardWednesday).setOnClickListener { openMealTime("Wednesday") }
        findViewById<CardView>(R.id.cardThursday).setOnClickListener { openMealTime("Thursday") }
        findViewById<CardView>(R.id.cardFriday).setOnClickListener { openMealTime("Friday") }
        findViewById<CardView>(R.id.cardSaturday).setOnClickListener { openMealTime("Saturday") }

        // Set up the "Done" button logic to check which day is selected before proceeding
        findViewById<Button>(R.id.buttonDone).setOnClickListener {
            // Check if a day has been selected
            val selectedDay = getSelectedDay()
            if (selectedDay != null) {
                // If a day is selected, open the MealTimeActivity passing the selected day
                openMealTime(selectedDay)
            } else {
                // Show a Toast message if no day has been selected
                Toast.makeText(this, "Please select a day", Toast.LENGTH_SHORT).show()
            }
        }
        applyTranslations()
    }

    private fun applyTranslations() {
        val textViewsToTranslate = listOf(
            findViewById<TextView>(R.id.titleText),
            findViewById<TextView>(R.id.Sunday),
            findViewById<TextView>(R.id.Monday),
            findViewById<TextView>(R.id.Tuesday),
            findViewById<TextView>(R.id.Wednesday),
            findViewById<TextView>(R.id.Thursday),
            findViewById<TextView>(R.id.Friday),
            findViewById<TextView>(R.id.Saturday),
        )

        val buttons = listOf(
            findViewById<Button>(R.id.buttonDone)
        )


        if (loadLanguagePreference(this) == "af") {
            // Apply translations to these text views if necessary
            TranslationUtil.translateTextViews(this, textViewsToTranslate, "af")
            TranslationUtil.translateButtons(this, buttons, "af")
        }
    }

    // Method to determine the selected day based on the checked CheckBox
    private fun getSelectedDay(): String? {
        // Reference all the CheckBoxes for each day
        val sundayCheckBox = findViewById<CheckBox>(R.id.checkBoxSunday)
        val mondayCheckBox = findViewById<CheckBox>(R.id.checkBoxMonday)
        val tuesdayCheckBox = findViewById<CheckBox>(R.id.checkBoxTuesday)
        val wednesdayCheckBox = findViewById<CheckBox>(R.id.checkBoxWednesday)
        val thursdayCheckBox = findViewById<CheckBox>(R.id.checkBoxThursday)
        val fridayCheckBox = findViewById<CheckBox>(R.id.checkBoxFriday)
        val saturdayCheckBox = findViewById<CheckBox>(R.id.checkBoxSaturday)

        // Return the day that is checked, or null if none are selected
        return when {
            sundayCheckBox.isChecked -> "Sunday"
            mondayCheckBox.isChecked -> "Monday"
            tuesdayCheckBox.isChecked -> "Tuesday"
            wednesdayCheckBox.isChecked -> "Wednesday"
            thursdayCheckBox.isChecked -> "Thursday"
            fridayCheckBox.isChecked -> "Friday"
            saturdayCheckBox.isChecked -> "Saturday"
            else -> null // No day is selected
        }
    }

    // Method to open MealTimeActivity and pass the selected day and recipe ID
    private fun openMealTime(day: String) {
        // Create an intent to start the MealTimeActivity
        val intent = Intent(this, MealTimeActivity::class.java)
        // Pass the selected day and recipe ID to the next activity
        intent.putExtra("DAY_SELECTED", day)
        intent.putExtra("RECIPE_ID", recipeId)
        // Start the new activity
        startActivity(intent)
    }
}
