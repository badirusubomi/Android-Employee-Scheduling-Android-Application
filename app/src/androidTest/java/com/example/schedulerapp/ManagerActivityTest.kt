package com.example.schedulerapp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ManagerActivityTest {
    @get:Rule
    var activityRule: ActivityScenarioRule<ManagerActivity> =
        ActivityScenarioRule(ManagerActivity::class.java)

    @Test
    fun testEmployeeInfoButton() {
        // Launch button to new activity
        onView(withId(R.id.addCV)).perform(click())

        // Check elements are correct and displayed
        onView(withId(R.id.textViewEmployeeId)).check(matches(withText("ID")))
        onView(withId(R.id.textViewFirstName)).check(matches(withText("First Name")))
        onView(withId(R.id.textViewLastName)).check(matches(withText("Last Name")))

        onView(withId(R.id.listViewEmployees)).check(matches(isDisplayed()))
        onView(withId(R.id.addEmployee)).check(matches(isDisplayed()))
    }

    @Test
    fun testCalendarButton() {
        onView(withId(R.id.calendarCV)).perform(click())
        // Leave this for now since calendar view has been changed
    }

    @Test
    fun testShiftScheduleButton() {
        onView(withId(R.id.generateCV)).perform(click())

        onView(withId(R.id.Title)).check(matches(withText("Schedule Creator")))
        onView(withId(R.id.generateButton)).check(matches(withText("Generate Schedule")))

        onView(withId(R.id.Title)).check(matches(isDisplayed()))
        onView(withId(R.id.monthSpinner)).check(matches(isDisplayed()))
        onView(withId(R.id.yearSpinner)).check(matches(isDisplayed()))
        onView(withId(R.id.generateButton)).check(matches(isDisplayed()))
    }
}