package com.example.fusion

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.fusion.utils.TranslationUtil

class LanguagePage : AppCompatActivity() {

    private lateinit var radioGroupLanguages: RadioGroup
    private lateinit var rbtnEng: RadioButton
    private lateinit var rbtnAfr: RadioButton
    private lateinit var btnSaveLanguage: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_language_page)


        // Back arrow functionality to go back to the previous screen
        findViewById<ImageView>(R.id.ImgBack2).setOnClickListener {
            onBackPressed() // Go back when the back arrow is clicked

        }
        // Initialize UI components
        radioGroupLanguages = findViewById(R.id.radioGroupLanguages)
        rbtnEng = findViewById(R.id.rbtnEng)
        rbtnAfr = findViewById(R.id.rBtnAfr)
        btnSaveLanguage = findViewById(R.id.btnSaveLanguage)

        // Load saved language preference and set the selected radio button
        val selectedLanguage = TranslationUtil.loadLanguagePreference(this)
        when (selectedLanguage) {
            "en" -> rbtnEng.isChecked = true
            "af" -> rbtnAfr.isChecked = true
        }

        // Handle Save button click
        btnSaveLanguage.setOnClickListener {
            saveSelectedLanguage()
        }
    }

    private fun saveSelectedLanguage() {
        val selectedRadioButtonId = radioGroupLanguages.checkedRadioButtonId
        val languageCode = when (selectedRadioButtonId) {
            R.id.rbtnEng -> "en"
            R.id.rBtnAfr -> "af"
            else -> "en" // Default to English if none selected
        }

        // Save the language preference
        TranslationUtil.saveLanguagePreference(this, languageCode)

        // If Afrikaans is selected, translate the UI
        if (languageCode == "af") {
            val textViews = listOf(
                findViewById<TextView>(R.id.textView8),
                findViewById<TextView>(R.id.btnSaveLanguage)
            )
            val RadioViews = listOf(
                findViewById<RadioButton>(R.id.rbtnEng),
                findViewById<RadioButton>(R.id.rBtnAfr)
            )
            TranslationUtil.translateTextViews(this, textViews, "af")
            TranslationUtil.translateRadioViews(this, RadioViews, "af")
        } else {
            // Revert to English or default
            recreate()  // Recreates the activity to apply changes
        }
    }
}
