package com.example.schedulerapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.sql.DriverManager.println

/**
 * This class provides managers to input employee information to be added to the employee database.
 * This then allows employees the functionality to login to the application and select their
 * availability.
 */
class EmployeeAddActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_add)

        // Values for inputs
        val employeeFirstName = findViewById<EditText>(R.id.etFirstName)
        val employeeLastName = findViewById<EditText>(R.id.etLastName)
        val employeePhone = findViewById<EditText>(R.id.etPhone)
        val employeeEmail = findViewById<EditText>(R.id.etEmail)
        val amQSwitch = findViewById<Switch>(R.id.switchAMq)
        val pmQSwitch = findViewById<Switch>(R.id.switchPMq)

        employeeFirstName.requestFocus()

        // Button to submit information
        val submitButton = findViewById<Button>(R.id.btSubmitInfo)

        // Grab info inputs to store in the database
        submitButton.setOnClickListener {
            val firstName = employeeFirstName.text.toString()
            val lastName = employeeLastName.text.toString()
            val phone = employeePhone.text.toString()
            val email = employeeEmail.text.toString()
            val amQ = amQSwitch.isChecked
            val pmQ = pmQSwitch.isChecked

            // Ensures all required input fields must exist to be registered into database
            if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                Toast.makeText( // Pop-up for missing fields
                    this@EmployeeAddActivity,
                    "Please enter all required fields",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val employee = Employee(1,firstName, lastName, phone, email, amQ, pmQ)
                val db = DatabaseScheduler(this@EmployeeAddActivity)
                val result = db.insertEmployee(employee)

                Toast.makeText( // Pop-up for successful registration
                    this@EmployeeAddActivity,
                    "$firstName $lastName added successfully",
                    Toast.LENGTH_SHORT
                ).show()

                if (result == -1L) {
                    println("Failed insertion to $TABLE_NAME table")
                } else {
                    println("Insertion to $TABLE_NAME successful")
                }

                // Clear input fields
                employeeFirstName.text.clear()
                employeeLastName.text.clear()
                employeePhone.text.clear()
                employeeEmail.text.clear()
                amQSwitch.isChecked = false
                pmQSwitch.isChecked = false

                employeeFirstName.requestFocus()
            }
        }

        /* Back button
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            // This will close the EmployeeAddActivity and go back to EmployeeInfoActivity
            finish()
        }
         */
    }
}
