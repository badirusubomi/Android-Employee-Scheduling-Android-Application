package com.example.schedulerapp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class EmployeeActivityTest {
    @get:Rule
    var activityRule: ActivityScenarioRule<EmployeeActivity> =
        ActivityScenarioRule(EmployeeActivity::class.java)

    @Test
    fun testAvailableButton() {
        onView(withId(R.id.shiftCV)).perform(click())

        onView(withId(R.id.textViewDate)).check(matches(withText("Select Date:")))
        onView(withId(R.id.switchAM)).check(matches(withText("AM")))
        onView(withId(R.id.switchPM)).check(matches(withText("PM")))
        onView(withId(R.id.submitButton)).check(matches(withText("Submit")))

        onView(withId(R.id.textViewDate)).check(matches(isDisplayed()))
        onView(withId(R.id.datePicker)).check(matches(isDisplayed()))
        onView(withId(R.id.switchAM)).check(matches(isDisplayed()))
        onView(withId(R.id.switchPM)).check(matches(isDisplayed()))
        onView(withId(R.id.submitButton)).check(matches(isDisplayed()))
    }
}