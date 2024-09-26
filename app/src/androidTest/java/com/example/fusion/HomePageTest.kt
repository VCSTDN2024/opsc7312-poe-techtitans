package com.example.fusion

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.IdlingRegistry
import com.example.fusion.api.ApiManager
import org.junit.Before
import org.junit.After
import org.junit.Rule
import org.junit.Test

class HomePageTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(HomePage::class.java)

    @Before
    fun setUp() {
        // Register the IdlingResource before the test starts
        IdlingRegistry.getInstance().register(ApiManager.idlingResource)
    }

    @After
    fun tearDown() {
        // Unregister the IdlingResource after the test finishes
        IdlingRegistry.getInstance().unregister(ApiManager.idlingResource)
    }

    @Test
    fun testSearchAndFilterPizzaLunch() {
        // Type "pizza" in the search box
        onView(withId(R.id.et_search))
            .perform(click(), typeText("pizza"), closeSoftKeyboard())

        // Click the search button
        onView(withId(R.id.btn_search)).perform(click())

        // Open the filter section
        onView(withId(R.id.btn_show_filters)).perform(click())

        // Select the "Lunch" radio button
        onView(withId(R.id.radioLunch)).perform(click())

        // Close the filter section
        onView(withId(R.id.btn_show_filters)).perform(click())

        // Click the search button again to apply the filter
        onView(withId(R.id.btn_search)).perform(click())

        // Verify that the RecyclerView is displaying the filtered results
        onView(withId(R.id.rv_search_results))
            .check(matches(isDisplayed()))
    }
}
