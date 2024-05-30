package com.example.schedulerapp

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * This class provides the calendar activity to be viewed by the manager to see generated shifts
 * for employees on a particular day.
 */
class CalendarActivity : AppCompatActivity() {
    // Constant code for runtime permissions
    private val PERMISSION_REQUEST_CODE = 200
    // So can access them throughout (pdf export)
    private var selectedMonth = 0
    private var selectedYear = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        val datePicker = findViewById<DatePicker>(R.id.datePicker)

        datePicker.init(
            datePicker.year, datePicker.month, datePicker.dayOfMonth
        ) { _, year, month, day ->
            val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse("$year-${month + 1}-$day")
            )
            val shifts = fetchShiftsForDate(selectedDate)
            showCustomPopup(selectedDate, shifts)
        }
        // Floating action button for exporting schedule
        val addButton = findViewById<FloatingActionButton>(R.id.export)
        addButton.setOnClickListener {
            //confirm or cancel export
            showConfirmationDialog()
        }
    }

    /** Returns list of employee's available for shift work in the morning/afternoon. */
    private fun fetchShiftsForDate(selectedDate: String): List<String> {

        val scheduleList: List<Schedule> = findScheduleByDate(selectedDate)
        val schedule_ShiftForm: Map<Int, List<Schedule>> = getScheduleByShift(scheduleList) // call functions to get schedules then group by shift
        println(schedule_ShiftForm.size)
        val shiftTimes = mutableListOf<String>("Employee IDs for Shift 1: 8:00 AM - 1:00 PM", "Employee IDs for Shift 2: 1:00 PM - 5:00 PM", "Employee IDs for Shift 3: 5:00 PM - 10:00 PM")

        val shifts = mutableListOf<String>()//("Shift 1: 9:00 AM - 5:00 PM", "Shift 2: 2:00 PM - 10:00 PM")
        // now shifts_ShiftForm is a list of Schedules in form EmpID, ShiftID, dateShift
        var i:Int = 0
        for ((shifttype, schedules) in schedule_ShiftForm){
            if (schedules.size == 0){    // no shifts to display

                println("No Shifts to display for this shift")
                shifts.add("No Shifts to display")
                println("Schedules list size = ${schedules.size}")
                continue
            }
            //println("Schedules list size = ${schedules.size}")
            shifts.add(shiftTimes[i])
            for (schedule in schedules){
                shifts.add("ID: ${schedule.employeeID}")
            }
            i++
        }
        return shifts
    }

    /** Creates a pop-up window when selecting a date within calendar for viewing ease. */
    @SuppressLint("MissingInflatedId")
    private fun showCustomPopup(selectedDate: String, shifts: List<String>) {
        val popupView = layoutInflater.inflate(R.layout.activity_manager_calendar_popup, null)

        val dateTextView = popupView.findViewById<TextView>(R.id.selectedDateTextView)
        val shiftsTextView = popupView.findViewById<TextView>(R.id.shiftsTextView)

        dateTextView.text = selectedDate
        shiftsTextView.text = "Shifts for this date:\n${shifts.joinToString("\n")}"

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("            Shifts for Selected Date")
            .setView(popupView)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()

        dialog.show()
    }

    /** Returns a list of employees available for a certain day in the calendar. */
    @SuppressLint("Range")
    private fun findScheduleByDate(selectedDate:String): List<Schedule>{
        val scheduleList = mutableListOf<Schedule>()

        val db = DatabaseScheduler(this).readableDatabase

        // Query to filter out schedule for selected date
        val queryString = ("SELECT * FROM Schedule as S WHERE S.dateShift = '$selectedDate'")
        val cursor = db.rawQuery(queryString, null) // "SELECT * FROM Schedule as S WHERE S.dateShift = '$selectedDate'", null)
        val index = cursor.getColumnIndex("dateShift")

        // Grabs values to be inputted to Schedule class
        while (cursor.moveToNext()) {
            val schedule = Schedule(
                cursor.getInt(cursor.getColumnIndex("ShiftID")),
                cursor.getString(cursor.getColumnIndex("dateShift")) ?: "",
                cursor.getInt(cursor.getColumnIndex("EmpID")),
            )
            scheduleList.add(schedule) // Add employee information into schedule generator
        }
        cursor.close()
        println(scheduleList)

        return scheduleList
    }

    /** Returns a schedule list grouped by shiftID (either morning or afternoon). */
    private fun getScheduleByShift(scheduleList: List<Schedule>): Map<Int, List<Schedule>> {
        val listOfSchedule = ArrayList<Schedule>()

        println(listOfSchedule.groupBy { it.shiftID }.map { it.value })

        return scheduleList.groupBy { it.shiftID }
        //return listOfSchedule.groupBy { it.ShiftID }.map { it.value }
    }

    /** Creates pop-up window prompting user to confirm download or cancel action. */
    @SuppressLint("MissingInflatedId")
    private fun showConfirmationDialog(){
        val popupView = layoutInflater.inflate(R.layout.activity_export_popup, null)

        val datePicker = findViewById<DatePicker>(R.id.datePicker)
        selectedMonth = datePicker.month //current selected month
        selectedYear = datePicker.year //current selected year

        val selectedMonthTextView = popupView.findViewById<TextView>(R.id.selectedMonthTextView)

        // Get the month name
        val monthName = DateFormatSymbols(Locale.getDefault()).months[selectedMonth]
        selectedMonthTextView.text = "Export Schedule for: $monthName"

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Confirm Export")
            .setView(popupView)
            //confirm click will export the current month schedule to a table pdf
            .setPositiveButton("Confirm") { dialog, _ ->
                if (checkPermission()){
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                }else{
                    requestPermission()
                }
                exportToPDF(selectedMonth, selectedYear)
                dialog.dismiss() }
                //cancel click will just close the dialog and do nothing
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()
        dialog.show()
    }

    /** Validates permission to read/write to external storage. */
    private fun checkPermission(): Boolean {
        val permission1 = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
        val permission2 = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED
    }

    /** Requests permission if not provided. */
    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
    }

    /** Creates a pop-up for user to show that permission has been granted or denied. */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED}){
                // Permission granted export pdf
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                exportToPDF(selectedMonth, selectedYear)
            } else {
                // Permission denied, show message
                // Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show() //permission is always denied but it exports ???
            }
        }
    }

    /** Creates the PDF file with required information (schedule) to be given to employees. */
    private fun exportToPDF(selectedMonth: Int, selectedYear: Int) {
        // Declare margin width and height of pdf file
        val pageMargin = 55
        val pageHeight = 1120
        val pageWidth = 792
        // Track which page on
        var currentPage = 1
        // Height of content per page
        val contentHeight = pageHeight - pageMargin

        val pdfDocument = PdfDocument()

        // Title for text and paint for shapes
        val title = Paint()
        val paint = Paint() // Could draw table?

        // Initialize first page
        var myPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPage).create()
        var myPage = pdfDocument.startPage(myPageInfo)
        var canvas = myPage.canvas

        title.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        title.textSize = 15f

        val calendar = Calendar.getInstance()
        // Set to the month chosen
        calendar.set(Calendar.MONTH, selectedMonth)
        // Set to the first day of the month
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        // Get the length of the month
        val numDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val monthName = DateFormatSymbols(Locale.getDefault()).months[selectedMonth]

        // Write the title
        canvas.drawText("$monthName Schedule", 75f, 30f, title)

        // Adjust to account for this page having a title
        var currentYPos = pageMargin.toFloat() + 30

        // Loop through the days and get shifts to add to pdf
        for ( dayOfMonth in 1..numDays){
           val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
               SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse("$selectedYear-${selectedMonth + 1}-$dayOfMonth")
           )
            // Use function already created to pull from DB
            val shifts = fetchShiftsForDate(selectedDate)

            // Print day/month to pdf
            canvas.drawText("$dayOfMonth $monthName", 209f, currentYPos, title)
            shifts.forEachIndexed { index, shift ->
                var textYPos = currentYPos + (index + 1) * 20
                // Check if content of page is full
                if (textYPos + 10 > contentHeight) {
                    // If full finish page, add to current page, create new page with same info but next page
                    pdfDocument.finishPage(myPage)
                    currentPage++
                    myPageInfo =
                        PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPage).create()
                    myPage = pdfDocument.startPage(myPageInfo)
                    canvas = myPage.canvas

                    // Initialize y positions
                    currentYPos = pageMargin.toFloat()
                    textYPos = currentYPos + (index + 1) * 20
                }
                // Write line to page, if new page started yPos will be at the top now otherwise will continue from last
                canvas.drawText(shift, 300f, textYPos, title)
            }
            val totalShiftsHeight = (shifts.size + 1) * 20 // 1 for the date
            currentYPos += totalShiftsHeight

        }
        // Close page
        pdfDocument.finishPage(myPage)

        // Set the name of PDF file and path
        val fileName = "$selectedMonth-$selectedYear-Schedule.pdf"
        val fileDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(fileDir, fileName)

        // Check if the file exists
        if (file.exists()) {
            // If the file exists, delete it before writing the new PDF
            file.delete() // Doesn't actually delete idk... run it twice and it overwrites?
        }
        try {
            // After creating a file name, write PDF file to that location
            pdfDocument.writeTo(FileOutputStream(file))
            // Print success or fail of export
            Toast.makeText(this@CalendarActivity, "Schedule exported successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // Close pdf
        pdfDocument.close()
    }
}
