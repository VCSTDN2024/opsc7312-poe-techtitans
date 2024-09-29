package com.example.fusion

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class StepsFragment : Fragment() {

    // Variable to hold the steps/instructions passed to the fragment
    private var steps: String? = null

    companion object {
        // Constant key used for passing steps as an argument
        private const val ARG_STEPS = "steps"

        // Factory method to create a new instance of StepsFragment and pass the steps data
        fun newInstance(steps: String): StepsFragment {
            val fragment = StepsFragment()
            val args = Bundle()
            args.putString(ARG_STEPS, steps)  // Store the steps in the arguments
            fragment.arguments = args
            return fragment
        }
    }

    // onCreate method: retrieves the steps data from the arguments bundle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        steps = arguments?.getString(ARG_STEPS)  // Get the steps passed as an argument
    }

    // onCreateView method: inflates the layout for the fragment and sets up the TextView with steps data
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment's layout (fragment_steps.xml)
        val view = inflater.inflate(R.layout.fragment_steps, container, false)

        // Find the TextView where the steps will be displayed
        val stepsText: TextView = view.findViewById(R.id.tv_steps)

        // Set the steps text in the TextView, using Html.fromHtml to format any HTML content
        stepsText.text = Html.fromHtml(steps, Html.FROM_HTML_MODE_LEGACY)

        return view  // Return the view for the fragment
    }
}
