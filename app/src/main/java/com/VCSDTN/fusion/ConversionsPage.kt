package com.VCSDTN.fusion

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.VCSDTN.fusion.utils.TranslationUtil
import com.VCSDTN.fusion.utils.TranslationUtil.loadLanguagePreference

class ConversionsPage : AppCompatActivity() {

    private var isMetricSelected = true

    private lateinit var txtGrams: EditText
    private lateinit var txtOunces: EditText
    private lateinit var txtKilos: EditText
    private lateinit var txtPounds: EditText
    private lateinit var txtMil: EditText
    private lateinit var txtLiters: EditText
    private lateinit var txtQuart: EditText
    private lateinit var txtGallon: EditText
    private lateinit var txtFOunces: EditText
    private lateinit var btnMetric: Button
    private lateinit var btnImperial: Button
    private lateinit var btnApply: Button
    private lateinit var btnBack: ImageView  // Added this line

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversions_page)

        // Initialize views
        txtGrams = findViewById(R.id.txtGrams)
        txtOunces = findViewById(R.id.txtOunces)
        txtKilos = findViewById(R.id.txtKilos)
        txtPounds = findViewById(R.id.txtPounds)
        txtMil = findViewById(R.id.txtMil)
        txtLiters = findViewById(R.id.txtLiters)
        txtQuart = findViewById(R.id.txtQuart)
        txtGallon = findViewById(R.id.txtGallon)
        txtFOunces = findViewById(R.id.txtFOunces)

        btnMetric = findViewById(R.id.btnMetric)
        btnImperial = findViewById(R.id.btnImperial)
        btnApply = findViewById(R.id.btnApply)
        btnBack = findViewById(R.id.btnBack)  // Initialize btnBack

        // Set up listeners
        btnMetric.setOnClickListener {
            isMetricSelected = true
            updateInputFields()
        }

        btnImperial.setOnClickListener {
            isMetricSelected = false
            updateInputFields()
        }

        btnApply.setOnClickListener {
            performConversions()
        }

        // Back button listener
        btnBack.setOnClickListener {
            finish()  // Closes the current activity and returns to the previous one
        }

        // Initialize input fields
        updateInputFields()

        applyTranslations()
    }

    private fun applyTranslations() {
        val textViewsToTranslate = listOf(
            findViewById<TextView>(R.id.textView12),
            findViewById<TextView>(R.id.textView50),
            findViewById<TextView>(R.id.textView51),
            findViewById<TextView>(R.id.textView52),
            findViewById<TextView>(R.id.textView53),
            findViewById<TextView>(R.id.textView54),
            findViewById<TextView>(R.id.textView55),
            findViewById<TextView>(R.id.textView58),
            findViewById<TextView>(R.id.textView56),
            findViewById<TextView>(R.id.textView57),
            findViewById<TextView>(R.id.textView47),
            findViewById(R.id.txtGrams),
            findViewById(R.id.txtOunces),
            findViewById(R.id.txtKilos),
            findViewById(R.id.txtPounds),
            findViewById(R.id.txtMil),
            findViewById(R.id.txtLiters),
            findViewById(R.id.txtQuart),
            findViewById(R.id.txtGallon),
            findViewById(R.id.txtFOunces)
        )

        val buttons = listOf(
            findViewById<Button>(R.id.btnApply),
            findViewById<Button>(R.id.btnMetric),
            findViewById<Button>(R.id.btnImperial)
        )

        if (loadLanguagePreference(this) == "af") {
            // Apply translations to these text views if necessary
            TranslationUtil.translateTextViews(this, textViewsToTranslate, "af")
            TranslationUtil.translateButtons(this, buttons, "af")
        }
    }

    private fun updateInputFields() {
        if (isMetricSelected) {
            // Enable metric input fields
            enableFields(
                metricFields = true,
                imperialFields = false
            )
            Toast.makeText(this, "Metric system selected", Toast.LENGTH_SHORT).show()
        } else {
            // Enable imperial input fields
            enableFields(
                metricFields = false,
                imperialFields = true
            )
            Toast.makeText(this, "Imperial system selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun enableFields(metricFields: Boolean, imperialFields: Boolean) {
        // Metric fields
        txtGrams.isEnabled = metricFields
        txtGrams.isFocusableInTouchMode = metricFields
        txtGrams.isClickable = metricFields

        txtKilos.isEnabled = metricFields
        txtKilos.isFocusableInTouchMode = metricFields
        txtKilos.isClickable = metricFields

        txtMil.isEnabled = metricFields
        txtMil.isFocusableInTouchMode = metricFields
        txtMil.isClickable = metricFields

        txtLiters.isEnabled = metricFields
        txtLiters.isFocusableInTouchMode = metricFields
        txtLiters.isClickable = metricFields

        if (!metricFields) {
            txtGrams.text.clear()
            txtKilos.text.clear()
            txtMil.text.clear()
            txtLiters.text.clear()
        }

        // Imperial fields
        txtOunces.isEnabled = imperialFields
        txtOunces.isFocusableInTouchMode = imperialFields
        txtOunces.isClickable = imperialFields

        txtPounds.isEnabled = imperialFields
        txtPounds.isFocusableInTouchMode = imperialFields
        txtPounds.isClickable = imperialFields

        txtFOunces.isEnabled = imperialFields
        txtFOunces.isFocusableInTouchMode = imperialFields
        txtFOunces.isClickable = imperialFields

        txtQuart.isEnabled = imperialFields
        txtQuart.isFocusableInTouchMode = imperialFields
        txtQuart.isClickable = imperialFields

        txtGallon.isEnabled = imperialFields
        txtGallon.isFocusableInTouchMode = imperialFields
        txtGallon.isClickable = imperialFields

        if (!imperialFields) {
            txtOunces.text.clear()
            txtPounds.text.clear()
            txtFOunces.text.clear()
            txtQuart.text.clear()
            txtGallon.text.clear()
        }
    }

    private fun performConversions() {
        if (isMetricSelected) {
            // Convert from Metric to Imperial
            convertMetricToImperial()
        } else {
            // Convert from Imperial to Metric
            convertImperialToMetric()
        }
    }

    private fun convertMetricToImperial() {
        // Grams to Ounces
        val gramsText = txtGrams.text.toString()
        if (gramsText.isNotEmpty()) {
            try {
                val grams = gramsText.toDouble()
                val ounces = grams * 0.03527396195
                txtOunces.setText(String.format("%.2f", ounces))
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Invalid input for Grams", Toast.LENGTH_SHORT).show()
            }
        }

        // Kilograms to Pounds
        val kilosText = txtKilos.text.toString()
        if (kilosText.isNotEmpty()) {
            try {
                val kilos = kilosText.toDouble()
                val pounds = kilos * 2.2046226218
                txtPounds.setText(String.format("%.2f", pounds))
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Invalid input for Kilograms", Toast.LENGTH_SHORT).show()
            }
        }

        // Milliliters to Fluid Ounces
        val milText = txtMil.text.toString()
        if (milText.isNotEmpty()) {
            try {
                val mil = milText.toDouble()
                val fluidOunces = mil * 0.0338140227
                txtFOunces.setText(String.format("%.2f", fluidOunces))
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Invalid input for Milliliters", Toast.LENGTH_SHORT).show()
            }
        }

        // Liters to Quarts and Gallons
        val litersText = txtLiters.text.toString()
        if (litersText.isNotEmpty()) {
            try {
                val liters = litersText.toDouble()
                val quarts = liters * 1.05668821
                val gallons = liters * 0.2641720524
                txtQuart.setText(String.format("%.2f", quarts))
                txtGallon.setText(String.format("%.2f", gallons))
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Invalid input for Liters", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun convertImperialToMetric() {
        // Ounces to Grams
        val ouncesText = txtOunces.text.toString()
        if (ouncesText.isNotEmpty()) {
            try {
                val ounces = ouncesText.toDouble()
                val grams = ounces * 28.349523125
                txtGrams.setText(String.format("%.2f", grams))
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Invalid input for Ounces", Toast.LENGTH_SHORT).show()
            }
        }

        // Pounds to Kilograms
        val poundsText = txtPounds.text.toString()
        if (poundsText.isNotEmpty()) {
            try {
                val pounds = poundsText.toDouble()
                val kilos = pounds * 0.45359237
                txtKilos.setText(String.format("%.2f", kilos))
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Invalid input for Pounds", Toast.LENGTH_SHORT).show()
            }
        }

        // Fluid Ounces to Milliliters
        val fluidOuncesText = txtFOunces.text.toString()
        if (fluidOuncesText.isNotEmpty()) {
            try {
                val fluidOunces = fluidOuncesText.toDouble()
                val mil = fluidOunces * 29.5735295625
                txtMil.setText(String.format("%.2f", mil))
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Invalid input for Fluid Ounces", Toast.LENGTH_SHORT).show()
            }
        }

        // Quarts to Liters
        val quartsText = txtQuart.text.toString()
        if (quartsText.isNotEmpty()) {
            try {
                val quarts = quartsText.toDouble()
                val liters = quarts * 0.946352946
                txtLiters.setText(String.format("%.2f", liters))
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Invalid input for Quarts", Toast.LENGTH_SHORT).show()
            }
        }

        // Gallons to Liters
        val gallonsText = txtGallon.text.toString()
        if (gallonsText.isNotEmpty()) {
            try {
                val gallons = gallonsText.toDouble()
                val liters = gallons * 3.785411784
                txtLiters.setText(String.format("%.2f", liters))
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Invalid input for Gallons", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
