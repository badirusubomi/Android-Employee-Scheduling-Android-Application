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
class EmployeeInfoActivityTest {
    @get:Rule
    var activityRule: ActivityScenarioRule<EmployeeInfoActivity> =
        ActivityScenarioRule(EmployeeInfoActivity::class.java)

    @Test
    fun testAddButton() {
        onView(withId(R.id.addEmployee)).perform(click())

        onView(withId(R.id.btSubmitInfo)).check(matches(withText("Submit")))
        onView(withId(R.id.tvEmployeeInfoTitle)).check(matches(withText("Input Employee Information")))
        onView(withId(R.id.switchAMq)).check(matches(withText("AM Qualified")))
        onView(withId(R.id.switchPMq)).check(matches(withText("PM Qualified")))

        onView(withId(R.id.etFirstName)).check(matches(isDisplayed()))
        onView(withId(R.id.etLastName)).check(matches(isDisplayed()))
        onView(withId(R.id.etEmail)).check(matches(isDisplayed()))
        onView(withId(R.id.etPhone)).check(matches(isDisplayed()))
        onView(withId(R.id.btSubmitInfo)).check(matches(isDisplayed()))
        onView(withId(R.id.tvEmployeeInfoTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.switchAMq)).check(matches(isDisplayed()))
        onView(withId(R.id.switchPMq)).check(matches(isDisplayed()))
    }
    // Implement test for field additions
}
