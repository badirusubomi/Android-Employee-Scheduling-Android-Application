package com.example.schedulerapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

/**
 * This class creates an activity for the employee's welcoming page provided they exist within the
 * employee database and login is granted.
 */
class EmployeeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee)

        // Navigate to main screen
        val empLogoutCV = findViewById<CardView>(R.id.employeeLogoutCV)
        empLogoutCV.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // Navigate to available screen
        val availableCV = findViewById<CardView>(R.id.shiftCV)
        availableCV.setOnClickListener {
            startActivity(Intent(this, AvailableActivity::class.java))
        }

        // Get the username from the intent
        val username = intent.getStringExtra("USERNAME") ?: ""

        val databaseScheduler = DatabaseScheduler(this)
        val firstName = databaseScheduler.getFirstNameForUsername(username)

        // Displays a welcoming message with employee's first name
        val welcomeMessage = "Welcome, $firstName" // If first name too long, doesn't wrap
        val welcomeTextView = findViewById<TextView>(R.id.tvWelcomeEmployee)
        welcomeTextView.text = welcomeMessage
    }

    fun navigateToAvailableActivity(view: View) {
        val intent = Intent(this, AvailableActivity::class.java)
        startActivity(intent)
    }
}
