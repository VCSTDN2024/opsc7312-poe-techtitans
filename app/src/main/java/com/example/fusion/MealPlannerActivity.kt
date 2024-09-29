package com.example.fusion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MealPlannerActivity : AppCompatActivity() {

    private var recipeId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_planner)

        // Get the recipeId from the intent
        recipeId = intent.getIntExtra("RECIPE_ID", -1)

        // Set up click listeners for each day (using CardView)
        findViewById<CardView>(R.id.cardSunday).setOnClickListener { openMealTime("Sunday") }
        findViewById<CardView>(R.id.cardMonday).setOnClickListener { openMealTime("Monday") }
        findViewById<CardView>(R.id.cardTuesday).setOnClickListener { openMealTime("Tuesday") }
        findViewById<CardView>(R.id.cardWednesday).setOnClickListener { openMealTime("Wednesday") }
        findViewById<CardView>(R.id.cardThursday).setOnClickListener { openMealTime("Thursday") }
        findViewById<CardView>(R.id.cardFriday).setOnClickListener { openMealTime("Friday") }
        findViewById<CardView>(R.id.cardSaturday).setOnClickListener { openMealTime("Saturday") }

        // Done button logic to check selected day and open MealTimeActivity
        findViewById<Button>(R.id.buttonDone).setOnClickListener {
            val selectedDay = getSelectedDay()
            if (selectedDay != null) {
                openMealTime(selectedDay) // Use the selected day for the intent
            } else {
                // Display a Toast if no day is selected
                Toast.makeText(this, "Please select a day", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Method to get the selected day based on the checked CheckBox
    private fun getSelectedDay(): String? {
       val sundayCheckBox = findViewById<CheckBox>(R.id.checkBoxSunday)
        val mondayCheckBox = findViewById<CheckBox>(R.id.checkBoxMonday)
        val tuesdayCheckBox = findViewById<CheckBox>(R.id.checkBoxTuesday)
        val wednesdayCheckBox = findViewById<CheckBox>(R.id.checkBoxWednesday)
        val thursdayCheckBox = findViewById<CheckBox>(R.id.checkBoxThursday)
        val fridayCheckBox = findViewById<CheckBox>(R.id.checkBoxFriday)
        val saturdayCheckBox = findViewById<CheckBox>(R.id.checkBoxSaturday)

        return when {
            sundayCheckBox.isChecked -> "Sunday"
            mondayCheckBox.isChecked -> "Monday"
            tuesdayCheckBox.isChecked -> "Tuesday"
            wednesdayCheckBox.isChecked -> "Wednesday"
            thursdayCheckBox.isChecked -> "Thursday"
            fridayCheckBox.isChecked -> "Friday"
            saturdayCheckBox.isChecked -> "Saturday"
            else -> null // No day selected
        }
    }

    // Open MealTimeActivity and pass the selected day and recipeId
    private fun openMealTime(day: String) {
        val intent = Intent(this, MealTimeActivity::class.java)
        intent.putExtra("DAY_SELECTED", day)
        intent.putExtra("RECIPE_ID", recipeId) // Pass the recipeId to MealTimeActivity
        startActivity(intent)
    }
}
