package com.example.fusion.utils

import android.content.Context
import android.text.Html
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fusion.BuildConfig
import com.example.fusion.api.Translator
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

object TranslationUtil {

    private val apiKey = BuildConfig.GOOGLE_API_KEY
    private val translator = Translator(apiKey)

    // Method to translate a list of TextViews to the desired language
    fun translateTextViews(context: Context, textViews: List<TextView>, targetLanguage: String) {
        textViews.forEach { textView ->
            var originalText: String
            if (textView.hint != null) {
                val hint = textView.hint.toString()
                val text = textView.text.toString()
                originalText = if (hint.isNotEmpty()) hint else text
            } else {
                val text = textView.text.toString()
                originalText = text
            }
            if (!originalText.isEmpty()) {
                translator.translateText(originalText, targetLanguage) { translatedText ->
                    (context as? AppCompatActivity)?.runOnUiThread {
                        if (translatedText != null) {
                            textView.text = Html.fromHtml(translatedText).toString()
                        } else {
                            Toast.makeText(context, "Translation failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        }
    }

    // Function to save the selected language preference
    fun saveLanguagePreference(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("selected_language", languageCode)
        editor.apply()
    }

    // Function to load the saved language preference
    fun loadLanguagePreference(context: Context): String {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return prefs.getString("selected_language", "en") ?: "en"
    }

    // Method to translate a list of TextViews to the desired language
    fun translateRadioViews(
        context: Context,
        RadioViews: List<RadioButton>,
        targetLanguage: String
    ) {
        RadioViews.forEach { RadioButton: RadioButton ->
            val originalText = RadioButton.text.toString()
            translator.translateText(originalText, targetLanguage) { translatedText ->
                (context as? AppCompatActivity)?.runOnUiThread {
                    if (translatedText != null) {
                        RadioButton.text = Html.fromHtml(translatedText).toString()
                    } else {
                        Toast.makeText(context, "Translation failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // Method to translate a list of TextViews to the desired language
    fun translateButtons(context: Context, Buttons: List<Button>, targetLanguage: String) {
        Buttons.forEach { Button: Button ->
            val originalText = Button.text.toString()
            translator.translateText(originalText, targetLanguage) { translatedText ->
                (context as? AppCompatActivity)?.runOnUiThread {
                    if (translatedText != null) {
                        Button.text = Html.fromHtml(translatedText).toString()
                    } else {
                        Toast.makeText(context, "Translation failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun traslateIncomingFromAPI(context: Context, Incoming: List<String>, targetLanguage: String) {
        Incoming.forEach { word: String ->
            var translated: String
            translator.translateText(word, targetLanguage) { translatedText ->
                (context as? AppCompatActivity)?.runOnUiThread {
                    if (translatedText != null) {
                        translated = Html.fromHtml(translatedText).toString()

                    } else {
                        Toast.makeText(context, "Translation failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun translateCheckBoxes(context: Context, checkBoxes: List<CheckBox>, targetLanguage: String) {
        checkBoxes.forEach { Button: CheckBox ->
            val originalText = Button.text.toString()
            translator.translateText(originalText, targetLanguage) { translatedText ->
                (context as? AppCompatActivity)?.runOnUiThread {
                    if (translatedText != null) {
                        Button.text = Html.fromHtml(translatedText).toString()
                    } else {
                        Toast.makeText(context, "Translation failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
