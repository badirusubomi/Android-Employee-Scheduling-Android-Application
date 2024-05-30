package com.example.schedulerapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


/** This class provides the methods in which the manager can generate shift schedules. */
class ShiftScheduleActivity : AppCompatActivity() {
    private val employeeUsername = "current_user_username" // Replace with the actual username

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shift_schedule)

        val tvTitle = findViewById<TextView>(R.id.Title)
        val monthSelector = findViewById<Spinner>(R.id.monthSpinner)
        val yearSelector = findViewById<Spinner>(R.id.yearSpinner)
        val generateButton = findViewById<Button>(R.id.generateButton)

        val months = resources.getStringArray(R.array.months)
        val years = resources.getStringArray(R.array.years)

        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)

        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        //would be nice to have the default month be the next month
        monthSelector.adapter = monthAdapter
        yearSelector.adapter = yearAdapter

        generateButton.setOnClickListener {
            val selectedMonth = monthSelector.selectedItem.toString()   //convert Nov to 11
            val selectedYear = yearSelector.selectedItem.toString()

            val month = monthStringToInt(selectedMonth) // Convert month name to month number
            if (month != null) {
                createSchedule(month.toInt(), selectedYear.toInt())
            } // Call schedule generator for the selected month

            val successMessage = "Schedule created for $selectedMonth $selectedYear"
            Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show()
        }

        // Add code here to display and manage shift schedules
        // Fetch and populate the schedules
        // Implement options for adding, editing, or deleting shifts.
        // Handle user interactions and events related to shift schedules.
    }

    /** Creates the schedule database. */
    private fun createSchedule(month:Int, year:Int){
        //create shifts for that month - Int
        val new_month = String.format("%02d", month)
        val db = DatabaseScheduler(this@ShiftScheduleActivity).writableDatabase
        db?.execSQL("DELETE FROM 'Shift' WHERE strftime('%Y-%m', dateShift) = '$year-$new_month'");             //clear shift table for that month and year
        var check: Int = 1
        while (check != 0) {
            db?.execSQL("DELETE FROM 'Schedule' WHERE strftime('%Y-%m', dateShift) = '$year-$new_month'");          //clear schedule table for that month and year
            populateShiftTablePerMonth(
                getDaysInMonth(month, year),
                month,
                year
            )

            //insert all possible shifts in schedule table
            db?.execSQL(
                "INSERT INTO Schedule ('ShiftID', 'EmpID', 'dateShift')\n" +
                        "SELECT  S.ShiftID, E.EmpID, dateShift\n" +
                        "FROM Employee E " +
                        "CROSS JOIN Shift S WHERE strftime('%Y-%m', dateShift) = '$year-$new_month'"
            )

            // Delete based on number of shifts (MAX = 30)
            db?.execSQL(
                "DELETE FROM Schedule\n" +
                        "WHERE ROWID NOT IN (\n" +
                        "    SELECT ROWID\n" +
                        "    FROM Schedule AS S1\n" +
                        "    WHERE EmpID = Schedule.EmpID\n" +
                        "    ORDER BY RANDOM()\n" +
                        "    LIMIT 30\n" +
                        ");"
            )

            //delete shift if employee booked off for that day
            db?.execSQL(
                "DELETE FROM Schedule WHERE EXISTS " +
                        "(  SELECT 1  FROM BookOff as B WHERE B.dateShift = Schedule.dateShift " +
                        "and B.ShiftID = Schedule.ShiftID and B.EmpID = Schedule.EmpID);"
            )



            //specify conditions to stop generating new schedules for that month.
            // e.g: all employees have the minimum number of shifts)
            check = 0
            db?.execSQL("DELETE FROM Schedule\n" +
                    "WHERE EXISTS (\n" +
                    "    SELECT 1\n" +
                    "    FROM Employee AS E\n" +
                    "    JOIN Schedule AS S ON S.EmpID = E.EmpID\n" +
                    "    WHERE S.EmpID = Schedule.EmpID\n" +
                    "      AND S.dateShift = Schedule.dateShift\n" +
                    "      AND S.ShiftID = Schedule.ShiftID\n" +
                    "      AND ((S.ShiftID = 1 AND E.AMq = 0) OR (S.ShiftID = 2 AND E.PMq = 0)" +
                    "      OR (S.ShiftID = 3 AND E.PMq = 0))" + //" AND NOT EXISTS (select count(*) from S where S.ShiftID = 1 and EXISTS (select * from S where S.ShitID = 1 and E.AMq = 1))\n" + //add conditions AND count of people with qualification is less than 0
                    ");") //query to ensure there exists at least 1 employee with AMq

            //ensure employees are not scheduled more than once a day- works!!
            db?.execSQL("DELETE FROM Schedule\n" +
                    "WHERE EXISTS (\n" +
                    "    SELECT 1\n" +
                    "    FROM Schedule AS S2\n" +
                    "    WHERE S2.EmpID = Schedule.EmpID\n" +
                    "      AND S2.dateShift = Schedule.dateShift\n" +
                    "      AND S2.ROWID > Schedule.ROWID\n" +
                    ");")

        }
    }

    /** Returns the number of days in the given month */
    private fun getDaysInMonth(month: Int, year: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31 // Months with 31 days
            4, 6, 9, 11 -> 30           // Months with 30 days
            2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28 // February, accounting for leap years
            else -> throw IllegalArgumentException("Invalid month: $month")
        }
    }

    /** Maps months into integer values. */
    private fun monthStringToInt(month: String): Int? {
        val monthMap = mapOf(
            "January" to 1,
            "February" to 2,
            "March" to 3,
            "April" to 4,
            "May" to 5,
            "June" to 6,
            "July" to 7,
            "August" to 8,
            "September" to 9,
            "October" to 10,
            "November" to 11,
            "December" to 12
        )
        return monthMap[month]
    }

    // Implement functions for fetching, saving, and managing shift schedules
    /** Populates the shift tables for morning and afternoon for the given month. */
    private fun populateShiftTablePerMonth(monthType:Int, month:Int, year:Int) {
        val stop:Int = monthType    // Indicates number of days in the month.

        val shifttypesTime = listOf("8:00", "1:00", "5:00") //9am and 2pm
        val db = DatabaseScheduler(this@ShiftScheduleActivity)
        var day:String? = null
        val scheduleMonth = String.format("%02d", month)
        for (i in 1..stop){ //loop for dates

            for (j in 0..2){
                val day = String.format("%02d", i)

                val shift:Shift = Shift(j,"$year-$scheduleMonth-$day",shifttypesTime[j]) // fill in dateShift
                val result = db.createShift(shift)
            }
        }
    }
}
