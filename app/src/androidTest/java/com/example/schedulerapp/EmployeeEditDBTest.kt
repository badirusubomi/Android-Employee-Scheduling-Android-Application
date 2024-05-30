package com.example.schedulerapp


import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.pressBack
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.anything
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class EmployeeEditDBTest {
    val firstName = "John"
    val lastName = "Doe"
    val phone = "17802339001"
    val email = "johndoe@gmail.com"
    val amQ = true
    val pmQ = true
    val employee = Employee(1,firstName, lastName, phone, email, amQ, pmQ)
    val db1 = DatabaseScheduler(getApplicationContext())
    val result = db1.insertEmployee(employee)

    @get:Rule
    var activityRule: ActivityScenarioRule<EmployeeInfoActivity> =
        ActivityScenarioRule(EmployeeInfoActivity::class.java)

    @Test
    fun testEmployeeEditDB() {
        val newFirstName = "Aiden"
        val newLastName = "Lumley"
        val newPhone = "17802029999"
        val newEmail = "aidenl@gmail.com"
        val newAmQ = false
        val newPmQ = false

        onView(withText("John")).perform(click())
        onView(withId(R.id.editFirstName)).perform(clearText(), typeText(newFirstName))
        onView(withId(R.id.editLastName)).perform(click(), clearText(), typeText(newLastName))
        onView(withId(R.id.editPhone)).perform(click(), clearText(), typeText(newPhone))
        onView(withId(R.id.editEmail)).perform(click(), clearText(), typeText(newEmail), closeSoftKeyboard())
        onView(withId(R.id.switchAMq)).perform(click())
        onView(withId(R.id.switchPMq)).perform(click())
        onView(withId(R.id.btnSave)).perform(click())

        val employeeInfo = arrayListOf<Any>()
        val db2 = DatabaseScheduler(getApplicationContext()).readableDatabase
        val cursor = db2.rawQuery("SELECT * FROM Employee", null)

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
        println(employeeInfo)
        assertTrue(employeeInfo == arrayListOf<Any>(1, "Aiden", "Lumley", "17802029999", "aidenl@gmail.com", false, false))
    }
}