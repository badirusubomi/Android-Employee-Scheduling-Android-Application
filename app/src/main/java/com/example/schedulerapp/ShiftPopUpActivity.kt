package com.example.schedulerapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * This class is used to create a pop-up dialog for managers to view in their calendar activity
 * for available employees on a particular date.
 */
class ShiftPopUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shift_popup)

        // Retrieve selected date
        val selectedDate = "Date: YYYY-MM-DD"

        val shiftContainer = findViewById<LinearLayout>(R.id.shiftContainer)

        val tvSelectedDate = findViewById<TextView>(R.id.tvSelectedDate)
        tvSelectedDate.text = "Selected Date: $selectedDate"

        // Find schedule with that day in database
        val scheduleList: List<Schedule> = findScheduleByDate(selectedDate)

        // This produces the schedule for the selected day grouped by shifts
        val schedulesGroupedByShift: List<List<Schedule>> = getScheduleByShift(scheduleList)
    }

    /** Returns schedule list of employees on a particular date. */
    @SuppressLint("Range")
    private fun findScheduleByDate(selectedDate: String): List<Schedule>{
        val scheduleList = mutableListOf<Schedule>()
        val db = DatabaseScheduler(this).readableDatabase
        val cursor = db.rawQuery("SELECT * FROM 'Schedule' WHERE 'Schedule'.dateShift = $selectedDate", null)

        while (cursor.moveToNext()) {
            val schedule = Schedule(
                cursor.getInt(cursor.getColumnIndex("ShiftId")),
                cursor.getString(cursor.getColumnIndex("dateShift")) ?: "",
                cursor.getInt(cursor.getColumnIndex("EmployeeID")),
            )

            scheduleList.add(schedule)
        }
        cursor.close()

        return scheduleList
    }

    /** Returns schedule list grouped by morning/afternoon shifts. */
    private fun getScheduleByShift(scheduleList: List<Schedule>): List<List<Schedule>>{
        //Pass array of schedules and group them into smaller arrays grouped by shiftID
        val listOfSchedule = ArrayList<Schedule>()
        return listOfSchedule.groupBy { it.shiftID }.map { it.value }
    }
}
