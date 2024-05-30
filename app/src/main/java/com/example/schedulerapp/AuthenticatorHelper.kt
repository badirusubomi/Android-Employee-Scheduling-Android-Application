package com.example.schedulerapp

import android.annotation.SuppressLint
import android.content.Context

public var logIn: Int = 0

/**
 * This object authenticates employee login to application based on employee's:
 *      username: employeeID
 *      password: employee last name
 */
object AuthenticationHelper {
    /** Returns password of employee based on their ID. */
    @SuppressLint("Range")
    fun getPasswordForUsername(context: Context, username: String): String? {
        // Read in employee database
        val dbHelper = DatabaseScheduler(context)
        val db = dbHelper.readableDatabase

        // Grabs last name (password) for employee ID
        val query = "SELECT $COL_LNAME FROM $TABLE_NAME WHERE $COL_ID = ?"
        val selectionArgs = arrayOf(username)
        val cursor = db.rawQuery(query, selectionArgs)

        // Returns password if ID found
        return if (cursor.moveToFirst()) {
            val password = cursor.getString(cursor.getColumnIndex(COL_LNAME))
            cursor.close()
            db.close()
            password
        } else {
            cursor.close()
            db.close()
            null
        }
    }

    /** Returns validation of user login password. */
    fun isValidLogin(context: Context, username: String, password: String): Boolean {
        val storedPassword = getPasswordForUsername(context, username)
        logIn = username.toInt()
        return password == storedPassword

    }

    /** Returns validation of login belonging to existing employee. */
    fun isEmployee(context: Context, username: String): Boolean {
        val dbHelper = DatabaseScheduler(context)
        val employeeIds = dbHelper.getEmployeeIds()
        return employeeIds.contains(username)
    }

    /** Returns a validation that user is a manager. */
    fun isManager(context: Context, username: String): Boolean {
        return username == "manager"
    }

    fun getEmpID(): Int? {
        return logIn
    }

}

