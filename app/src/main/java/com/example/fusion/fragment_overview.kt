package com.example.fusion

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class OverviewFragment : Fragment() {

    private var summary: String? = null

    companion object {
        private const val ARG_SUMMARY = "summary"

        fun newInstance(summary: String): OverviewFragment {
            val fragment = OverviewFragment()
            val args = Bundle()
            args.putString(ARG_SUMMARY, summary)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        summary = arguments?.getString(ARG_SUMMARY)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_overview, container, false)
        val overviewText: TextView = view.findViewById(R.id.tv_overview)
        // If the summary contains HTML tags, use Html.fromHtml
        overviewText.text = Html.fromHtml(summary, Html.FROM_HTML_MODE_LEGACY)
        return view
    }
}
