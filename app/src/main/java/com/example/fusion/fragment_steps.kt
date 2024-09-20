package com.example.fusion

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class StepsFragment : Fragment() {

    private var steps: String? = null

    companion object {
        private const val ARG_STEPS = "steps"

        fun newInstance(steps: String): StepsFragment {
            val fragment = StepsFragment()
            val args = Bundle()
            args.putString(ARG_STEPS, steps)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        steps = arguments?.getString(ARG_STEPS)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_steps, container, false)
        val stepsText: TextView = view.findViewById(R.id.tv_steps)
        // If the instructions contain HTML tags, use Html.fromHtml
        stepsText.text = Html.fromHtml(steps, Html.FROM_HTML_MODE_LEGACY)
        return view
    }
}
