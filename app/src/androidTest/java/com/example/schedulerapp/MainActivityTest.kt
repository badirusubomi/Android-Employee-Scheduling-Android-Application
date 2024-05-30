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
class MainActivityTest {
    @get:Rule
    // Launches MainActivity
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testManagerButton() {
        // Performs button click
        onView(withId(R.id.managerCV)).perform(click())
        // Match text to Welcome Manager screen
        onView(withId(R.id.welcomeTitle)).check(matches(withText("Manager")))
        onView(withId(R.id.selectTasktv)).check(matches(withText("Select task:")))
        onView(withId(R.id.addEmployeetv)).check(matches(withText("Add Employee")))
        onView(withId(R.id.viewCalendartv)).check(matches(withText("View Calendar")))
        onView(withId(R.id.generateShiftstv)).check(matches(withText("Generate Shifts")))
        onView(withId(R.id.mainMenutv)).check(matches(withText("Main Menu")))

        onView(withId(R.id.welcomeTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.selectTasktv)).check(matches(isDisplayed()))
        onView(withId(R.id.addEmployeetv)).check(matches(isDisplayed()))
        onView(withId(R.id.viewCalendartv)).check(matches(isDisplayed()))
        onView(withId(R.id.generateShiftstv)).check(matches(isDisplayed()))
        onView(withId(R.id.mainMenutv)).check(matches(isDisplayed()))
        onView(withId(R.id.addEmployeeiv)).check(matches(isDisplayed()))
        onView(withId(R.id.viewCalendariv)).check(matches(isDisplayed()))
        onView(withId(R.id.generateShiftsiv)).check(matches(isDisplayed()))
        onView(withId(R.id.mainMenuiv)).check(matches(isDisplayed()))
        onView(withId(R.id.manageriv)).check(matches(isDisplayed()))
    }

    @Test
    fun testEmployeeButton() {
        onView(withId(R.id.employeeCV)).perform(click())

        onView(withId(R.id.loginButton)).check(matches(withText("Login")))

        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
        onView(withId(R.id.usernameEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.passwordEditText)).check(matches(isDisplayed()))
    }
}