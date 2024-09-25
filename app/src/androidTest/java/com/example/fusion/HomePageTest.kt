package com.example.fusion

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test
import org.junit.Before
import org.junit.After
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed

class HomePageTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(HomePage::class.java)

    @Before
    fun setUp() {
        // Set the system property to bypass login
        System.setProperty("IS_TESTING", "true")
    }

    @After
    fun tearDown() {
        // Clear the system property after tests
        System.clearProperty("IS_TESTING")
    }

    @Test
    fun testSearchAndFilter() {
        // Type "pizza" in the search box
        onView(withId(R.id.et_search))
            .perform(click(), typeText("pizza"))

        // Click the search button
        onView(withId(R.id.btn_search)).perform(click())

        // Wait for the results to load (consider replacing with IdlingResource)
        Thread.sleep(3000)

        // Open the filter section
        onView(withId(R.id.btn_show_filters)).perform(click())

        // Select the "Lunch" radio button
        onView(withId(R.id.radioLunch)).perform(click())

        // Close the filter section
        onView(withId(R.id.btn_show_filters)).perform(click())

        // Click the search button again
        onView(withId(R.id.btn_search)).perform(click())

        // Wait for the filtered results to load
        Thread.sleep(3000)

        // Verify that the RecyclerView is displaying results
        onView(withId(R.id.rv_search_results))
            .check(matches(isDisplayed()))
    }
}
