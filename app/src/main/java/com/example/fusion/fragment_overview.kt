package com.example.fusion

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment

class OverviewFragment : Fragment() {

    private var summary: String? = null

    companion object {
        fun newInstance(summary: String): OverviewFragment {
            val fragment = OverviewFragment()
            val args = Bundle()
            args.putString("summary", summary)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        summary = arguments?.getString("summary")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tvOverview: TextView = view.findViewById(R.id.tv_overview)
        tvOverview.text = HtmlCompat.fromHtml(summary ?: "No summary available", HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}

