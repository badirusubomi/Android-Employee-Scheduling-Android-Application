package com.example.schedulerapp

import android.content.ContentValues
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.TextView
import android.widget.EditText
import android.widget.Toast
import android.text.Editable
import android.text.TextWatcher
import android.widget.Switch

class EmployeeDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_details)

        // Receive the employee details from the previous activity
        val employee = intent.getSerializableExtra("employee") as Employee

        // Initialize TextViews and Switches to display employee details
        val idTextView: TextView = findViewById(R.id.textEmployeeId)
        val firstNameTextView: EditText = findViewById(R.id.editFirstName)
        val lastNameTextView: EditText = findViewById(R.id.editLastName)
        val phoneTextView: EditText = findViewById(R.id.editPhone)
        val emailTextView: EditText = findViewById(R.id.editEmail)
        val amQSwitch: Switch = findViewById(R.id.switchAMq)
        val pmQSwitch: Switch = findViewById(R.id.switchPMq)

        // Set the employee details in TextViews and Switches
        idTextView.text = "Employee ID: ${employee.empId}"
        firstNameTextView.setText(employee.fName)
        lastNameTextView.setText(employee.lName)
        phoneTextView.setText(employee.phone)
        emailTextView.setText(employee.email)
        amQSwitch.isChecked = employee.amQ
        pmQSwitch.isChecked = employee.pmQ

        // Get references to the "Go Back" and "Delete" buttons
        // val goBackButton: FloatingActionButton = findViewById(R.id.btnGoBack)
        val deleteButton: FloatingActionButton = findViewById(R.id.btnDelete)

        /* Set a click listener for the "Go Back" button
        goBackButton.setOnClickListener {
            finish() // Close this activity and go back
        }
         */

        // Set a click listener for the "Delete" button
        deleteButton.setOnClickListener {
            // Logic to delete the employee here
            val db = DatabaseScheduler(this).writableDatabase

            val selection = "$COL_ID = ?"
            val selectionArgs = arrayOf(employee.empId.toString())

            val deletedRows = db.delete(TABLE_NAME, selection, selectionArgs)

            if (deletedRows > 0) {
                // Deletion was successful
                Toast.makeText(this, "Employee deleted successfully", Toast.LENGTH_SHORT).show()
                finish() // Close this activity after deletion
            } else {
                // Deletion failed
                Toast.makeText(this, "Failed to delete employee", Toast.LENGTH_SHORT).show()
            }
        }

        // Save button
        val btnSave: FloatingActionButton = findViewById(R.id.btnSave)

        btnSave.setOnClickListener {
            // UpdateEmployeeInDatabase when saving changes
            val rowsUpdated = updateEmployeeInDatabase(employee)
            if (rowsUpdated > 0) {
                // Update was successful, show the message
                Toast.makeText(this, "Employee updated successfully", Toast.LENGTH_SHORT).show()
            } else {
                // Update failed
                Toast.makeText(this, "Failed to update employee", Toast.LENGTH_SHORT).show()
            }
        }

        // First name EditText
        val editFirstName = findViewById<EditText>(R.id.editFirstName)
        val initialFirstName = employee.fName // Store the initial value

        editFirstName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val newFirstName = s.toString()

                // Checking if the new value is different from the initial value
                if (newFirstName != initialFirstName) {
                    employee.fName = newFirstName
                    updateEmployeeInDatabase(employee)
                }
            }
        })

        // Last name EditText
        val editLastName = findViewById<EditText>(R.id.editLastName)
        val initialLastName = employee.lName

        editLastName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val newLastName = s.toString()

                if (newLastName != initialLastName) {
                    employee.lName = newLastName
                    updateEmployeeInDatabase(employee)
                }
            }
        })

        // Phone EditText
        val editPhone = findViewById<EditText>(R.id.editPhone)
        val initialPhone = employee.phone

        editPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val newPhone = s.toString()

                if (newPhone != initialPhone) {
                    employee.phone = newPhone
                    updateEmployeeInDatabase(employee)
                }
            }
        })

        // Email EditText
        val editEmail = findViewById<EditText>(R.id.editEmail)
        val initialEmail = employee.email

        editEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val newEmail = s.toString()

                if (newEmail != initialEmail) {
                    employee.email = newEmail
                    updateEmployeeInDatabase(employee)
                }
            }
        })

        // AM Qualifications Switch
        amQSwitch.setOnClickListener {
            employee.amQ = !employee.amQ
            updateEmployeeInDatabase(employee)
        }

        // PM Qualifications Switch
        pmQSwitch.setOnClickListener {
            employee.pmQ = !employee.pmQ
            updateEmployeeInDatabase(employee)
        }
    }

    /**
     * This function allows manager to update any of the employees information, this includes all
     * of an employees first name, last name, phone, email, and morning/afternoon qualification.
     * @returns employee database with updated changes to any employee information fields.
     */
    private fun updateEmployeeInDatabase(employee: Employee): Int {
        // Logic to update the employee in the database
        val db = DatabaseScheduler(this).writableDatabase

        val values = ContentValues()
        values.put(COL_FNAME, employee.fName)
        values.put(COL_LNAME, employee.lName)
        values.put(COL_PHONE, employee.phone)
        values.put(COL_EMAIL, employee.email)
        values.put(COL_AMQ, employee.amQ)
        values.put(COL_PMQ, employee.pmQ)

        val selection = "$COL_ID = ?"
        val selectionArgs = arrayOf(employee.empId.toString())

        return db.update(TABLE_NAME, values, selection, selectionArgs)
    }

}
