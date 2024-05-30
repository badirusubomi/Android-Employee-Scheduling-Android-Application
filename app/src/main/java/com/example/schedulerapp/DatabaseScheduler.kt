package com.example.schedulerapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

val DATABASE_NAME = "SchedDB"
val TABLE_NAME = "Employee"
val COL_ID = "EmpId"
val COL_FNAME = "FName"
val COL_LNAME = "LName"
val COL_PHONE = "Phone"
val COL_EMAIL = "Email"
val COL_AMQ = "AMq"
val COL_PMQ = "PMq"

class DatabaseScheduler(var context:Context): SQLiteOpenHelper(context,DATABASE_NAME, null,2) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createEmployeeTable = ("CREATE TABLE $TABLE_NAME (" +
                "  $COL_ID integer primary key autoincrement," +
                "  $COL_FNAME varchar(255)," +
                "  $COL_LNAME varchar(255)," +
                "  $COL_PHONE varchar(255)," +
                "  $COL_EMAIL varchar(255)," +
                "  $COL_AMQ bool," +
                "  $COL_PMQ bool)")

        val createShiftTable = (
                "CREATE TABLE 'Shift' (" +
                        "`ShiftID` integer," +
                        "`dateShift` date," +   // Format YYYY-MM-DD
                        "`timeShift` time," +
                        " PRIMARY KEY ('ShiftID', 'dateShift'))")

        val createScheduleTable = (
                "CREATE TABLE `Schedule` (" +
                        "  `ShiftID` integer," +
                        "  `EmpID` integer," +
                        "  `dateShift` date," +  //format YYYY-MM-DD
                        " PRIMARY KEY ('ShiftID', 'EmpID', 'dateShift'));"
                )

        val createBookOffTable = (
                "CREATE TABLE `BookOff` (" +
                        "  `ShiftID` integer," +
                        "  `EmpID` integer," +
                        "  `dateShift` date," +  //format YYYY-MM-DD
                        " PRIMARY KEY ('ShiftID', 'EmpID', 'dateShift'));"
                )

        val dependenciesSQL = (
                "ALTER TABLE `$TABLE_NAME` ADD FOREIGN KEY (`EmpID`) REFERENCES `Availability` (`EmpId`);\n" +
                        "ALTER TABLE `Employee` ADD FOREIGN KEY (`EmpID`) REFERENCES `Schedule` (`EmpID`);\n" +
                        "ALTER TABLE `Shift` ADD FOREIGN KEY (`ShiftID`) REFERENCES `Schedule` (`ShiftID`);\n" //+
                //"ALTER TABLE `Schedule` ADD FOREIGN KEY (`ShiftID`) REFERENCES `Shift` (`ShiftID`);\n" +
                //"ALTER TABLE `Schedule` ADD FOREIGN KEY (`EmpID`) REFERENCES `Employee` (`EmpID`);"
                )
//        db?.execSQL("DROP TABLE 'Employee'");
//        db?.execSQL("DROP TABLE 'Shift'");
//        db?.execSQL("DROP TABLE 'Schedule'");
//        db?.execSQL("DROP TABLE 'Availability'");

        db?.execSQL(createEmployeeTable)
        db?.execSQL(createShiftTable)
        db?.execSQL(createScheduleTable)
        db?.execSQL(createBookOffTable)

        //db?.execSQL(dependenciesSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        // Handle database schema upgrades here.
        //db?.execSQL("DROP TABLE IF EXISTS 'Shift'");
//        db?.execSQL("DROP TABLE 'Employee'");
//        db?.execSQL("DROP TABLE 'Shift'");
//        db?.execSQL("DROP TABLE 'Schedule'");
//        db?.execSQL("DROP TABLE 'Availability'");
//        println("Dropped Tables")
//        onCreate(db);
    }

    fun insertEmployee(employee: Employee): Long{
        val db = this.writableDatabase
        var cv = ContentValues()
        //cv.put(COL_ID, employee.EmpId)
        cv.put(COL_FNAME, employee.fName)
        cv.put(COL_LNAME, employee.lName)
        cv.put(COL_EMAIL, employee.email)
        cv.put(COL_PHONE, employee.phone)
        cv.put(COL_AMQ, employee.amQ)
        cv.put(COL_PMQ, employee.pmQ)
        var result = db.insert(TABLE_NAME, null,cv) //TableName == Employee table
                                                                // All other tables should be typed

        if (result == (-1).toLong()) {
            println("Failed insertion to $TABLE_NAME table")
        } else {
            println("Insertion to $TABLE_NAME succesfull")
        }
        return result
    }

    fun createShift(shift: Shift): Long{
        val db = this.writableDatabase
        var cv = ContentValues()

        cv.put("ShiftID", shift.shiftID)
        cv.put("dateShift",shift.dateShift)
        cv.put("timeShift", shift.timeShift)

        var result = db.insert("Shift", null,cv)

        if (result == (-1).toLong()) {
            println("Failed insertion to Shift table")
        } else {
            println("Insertion to Shift table succesfull")
        }
        return result
    }

    fun createSchedule(schedule: Schedule): Long {
        //manually insert an employee into the schedule. Manager modifications
        val db = this.writableDatabase
        var cv = ContentValues()

        cv.put("ShiftID", schedule.shiftID)
        cv.put("dateShift",schedule.dateShift)
        cv.put("EmpID", schedule.employeeID)

        var result = db.insert("Schedule", null, cv)

        if (result == (-1).toLong()) {
            println("Failed insertion to Schedule table")
        } else {
            println("Insertion to Schedule table succesfull")
        }
        return result
    }

//    fun createAvailability(Schedule: Schedule): Long {
//        //manually insert an avilability into the schedule. Manager modifications
//        val db = this.writableDatabase
//        var cv = ContentValues()
//
//        cv.put("ShiftID", Schedule.ShiftID)
//        cv.put("dateShift",Schedule.DateShift)
//        cv.put("EmpID", Schedule.EmployeeID)
//
//        var result = db.insert("Schedule", null, cv)
//
//        if (result == -1.toLong())
//
//            println("Failed insertion to Schedule table")
//
//        else
//            println("Insertion to Schedule table succesfull")
//
//        return result
//    }

    @SuppressLint("Range")
    fun getEmployeeIds(): List<String> {
        val db = this.readableDatabase
        val employeeIds = ArrayList<String>()
        val query = "SELECT $COL_ID FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val empId = cursor.getString(cursor.getColumnIndex(COL_ID))
                employeeIds.add(empId)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return employeeIds
    }


    @SuppressLint("Range")
    fun getFirstNameForUsername(username: String): String {
        val db = this.readableDatabase
        var firstName = ""
        val query = "SELECT $COL_FNAME FROM $TABLE_NAME WHERE $COL_ID = ?"
        val selectionArgs = arrayOf(username)
        val cursor = db.rawQuery(query, selectionArgs)

        if (cursor.moveToFirst()) {
            firstName = cursor.getString(cursor.getColumnIndex(COL_FNAME))
        }

        cursor.close()
        db.close()

        return firstName
    }
}

