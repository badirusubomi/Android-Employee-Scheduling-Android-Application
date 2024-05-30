package com.example.schedulerapp

import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class EmployeeAddDBTest {
    @get:Rule
    var activityRule: ActivityScenarioRule<EmployeeAddActivity> =
        ActivityScenarioRule(EmployeeAddActivity::class.java)

    @Test
    fun testEmployeeAddDB() {
        val firstName = "John"
        val lastName = "Doe"
        val phone = "17802339001"
        val email = "johndoe@gmail.com"
        val amQ = true
        val pmQ = true

        onView(withId(R.id.etFirstName)).perform(typeText(firstName))
        onView(withId(R.id.etLastName)).perform(click(), typeText(lastName))
        onView(withId(R.id.etPhone)).perform(click(), typeText(phone))
        onView(withId(R.id.etEmail)).perform(click(), typeText(email))
        onView(withId(R.id.switchAMq)).perform(click())
        onView(withId(R.id.switchPMq)).perform(click())
        onView(withId(R.id.btSubmitInfo)).perform(click())

        val employeeInfo = arrayListOf<Any>()
        val db = DatabaseScheduler(getApplicationContext()).readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Employee", null)

        while (cursor.moveToNext()) {
            employeeInfo.add(cursor.getInt(cursor.getColumnIndex("EmpId")))
            employeeInfo.add(cursor.getString(cursor.getColumnIndex("FName")) ?: "")
            employeeInfo.add(cursor.getString(cursor.getColumnIndex("LName")) ?: "")
            employeeInfo.add(cursor.getString(cursor.getColumnIndex("Phone")) ?: "")
            employeeInfo.add(cursor.getString(cursor.getColumnIndex("Email")) ?: "")
            employeeInfo.add(cursor.getInt(cursor.getColumnIndex("AMq")) == 1)
            employeeInfo.add(cursor.getInt(cursor.getColumnIndex("PMq")) == 1)
        }
        cursor.close()

        assertTrue(employeeInfo == arrayListOf<Any>(1, "John", "Doe", "17802339001", "johndoe@gmail.com", true, true))
    }
}