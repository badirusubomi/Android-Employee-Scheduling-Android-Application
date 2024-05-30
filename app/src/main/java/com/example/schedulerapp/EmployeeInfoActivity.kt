package com.example.schedulerapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * This class provides an list activity (screen) for the manager to view of all employees registered
 * within the employee database.
 */
class EmployeeInfoActivity : AppCompatActivity() {

    private lateinit var adapter: EmployeeListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_info)

        // Initialize ListView
        val listViewEmployees = findViewById<ListView>(R.id.listViewEmployees)

        // Fetch employee data from your database using getAllEmployees
        val employeeList = getAllEmployees()

        // Create a custom adapter and set it to the ListView
        adapter = EmployeeListAdapter(this, employeeList)
        listViewEmployees.adapter = adapter

        // Floating action button to add a new employee
        val addButton = findViewById<FloatingActionButton>(R.id.addEmployee)
        addButton.setOnClickListener {
            startActivity(Intent(this, EmployeeAddActivity::class.java))
        }

        // Delete button
//        val deleteButton = findViewById<FloatingActionButton>(R.id.btnDelete)
//        deleteButton.setOnClickListener {
//            // Get the selected employees from the adapter
//            val selectedEmployees = adapter.getSelectedEmployees()
//
//            // Check if any employees are selected
//            if (selectedEmployees.isNotEmpty()) {
//                //logic to delete the selected employees
//                for (employee in selectedEmployees) {
//                    deleteEmployee(employee)
//                }
//
//                // Refresh the employee list after deletion
//                refreshEmployeeList()
//
//                // Inform the manager about successful deletion
//                Toast.makeText(
//                    this,
//                    "Selected employees deleted successfully",
//                    Toast.LENGTH_SHORT
//                ).show()
//            } else {
//                // Show a message to the user that no employees are selected for deletion
//                Toast.makeText(
//                    this,
//                    "No employees selected for deletion",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }

//        val refreshButton = findViewById<FloatingActionButton>(R.id.btnRefresh)
//        refreshButton.setOnClickListener {
//            refreshEmployeeList()
//        }
    }

    /** This function auto-refreshes the employee list. */
    override fun onResume() {
        super.onResume()
        refreshEmployeeList()
    }

    /** This function updates employee data and refreshes the adapter. */
    private fun refreshEmployeeList() {
        val employeeList = getAllEmployees()
        adapter.updateEmployeeList(employeeList)
    }

    /** Returns a list of Employee instances for registered employees within the database. */
    @SuppressLint("Range")
    private fun getAllEmployees(): List<Employee> {
        val employeeList = mutableListOf<Employee>()
        // Employee database
        val db = DatabaseScheduler(this).readableDatabase
        // Query to select all attributes of employee from database
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        // Grab employee information to insert into Employee class
        while (cursor.moveToNext()) {
            val employee = Employee(
                cursor.getInt(cursor.getColumnIndex("EmpId")),
                cursor.getString(cursor.getColumnIndex(COL_FNAME)) ?: "",
                cursor.getString(cursor.getColumnIndex(COL_LNAME)) ?: "",
                cursor.getString(cursor.getColumnIndex(COL_PHONE)) ?: "",
                cursor.getString(cursor.getColumnIndex(COL_EMAIL)) ?: "",
                cursor.getInt(cursor.getColumnIndex(COL_AMQ)) == 1,
                cursor.getInt(cursor.getColumnIndex(COL_PMQ)) == 1
            )
            employeeList.add(employee)
        }
        cursor.close()

        return employeeList
    }

    /**
     * This function allows for manager to delete an employee that no longer works at the company.
     * Note: by performing this function, an employee loses all login and shift availability
     *       privileges.
     */
    private fun deleteEmployee(employee: Employee) {
        val db = DatabaseScheduler(this).writableDatabase
        val selection = "$COL_ID = ?" // Change this later back to ID to delete employee
        val selectionArgs = arrayOf(employee.empId.toString()) // Change to ID
        
        db.delete(TABLE_NAME, selection, selectionArgs)
    }
}
