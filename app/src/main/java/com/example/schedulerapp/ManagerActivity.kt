package com.example.schedulerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.sax.StartElementListener
import android.widget.Button
import androidx.cardview.widget.CardView

/**
 * This class provides an activity (screen) for manager(s) to view employee list, view the calendar
 * or generate the shifts from available employees for a particular month.
 */
class ManagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager)

        // Navigate to add employee screen
        val employeeInfoCV = findViewById<CardView>(R.id.addCV)
        employeeInfoCV.setOnClickListener {
            startActivity(Intent(this, EmployeeInfoActivity::class.java))
        }

        // Navigate to calendar view screen
        val calendarCV = findViewById<CardView>(R.id.calendarCV)
        calendarCV.setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
        }

        // Navigate to generate shift screen
        val generateShiftCV = findViewById<CardView>(R.id.generateCV)
        generateShiftCV.setOnClickListener {
            startActivity(Intent(this, ShiftScheduleActivity::class.java))
        }

        // Navigate to main screen
        val logoutCV = findViewById<CardView>(R.id.menuCV)
        logoutCV.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}