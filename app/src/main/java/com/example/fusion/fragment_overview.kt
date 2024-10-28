package com.example.fusion

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.example.fusion.utils.TranslationUtil
import com.example.fusion.utils.TranslationUtil.loadLanguagePreference

class OverviewFragment : Fragment() {

    // Variable to hold the summary text passed to the fragment
    private var summary: String? = null

    companion object {
        // Factory method to create a new instance of the fragment and pass the summary data as arguments
        fun newInstance(summary: String): OverviewFragment {
            val fragment = OverviewFragment()
            val args = Bundle()
            args.putString("summary", summary)  // Store the summary in the arguments
            fragment.arguments = args
            return fragment
        }
    }

    // onCreate method: retrieve the summary data from the arguments bundle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        summary = arguments?.getString("summary")  // Get the summary passed as an argument
    }

    // onCreateView method: inflates the layout for the fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the fragment's layout (fragment_overview.xml)
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    // onViewCreated method: called after the view is created; updates the TextView with the summary content
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Find the TextView where the summary will be displayed
        val tvOverview: TextView = view.findViewById(R.id.tv_overview)

        // Set the summary text in the TextView, using HtmlCompat to properly format any HTML content
        tvOverview.text = HtmlCompat.fromHtml(summary ?: "No summary available", HtmlCompat.FROM_HTML_MODE_LEGACY)
        applyTranslations()
    }

    private fun applyTranslations() {
        val textViewsToTranslate = listOf(
            requireView().findViewById<TextView>(R.id.tv_overview)
        )


        if(loadLanguagePreference(this@OverviewFragment.requireContext()) == "af") {
            // Apply translations to these text views if necessary
            TranslationUtil.translateTextViews(this@OverviewFragment.requireContext(), textViewsToTranslate, "af")
        }
    }
}
