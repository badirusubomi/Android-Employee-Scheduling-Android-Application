package com.example.schedulerapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

/**
 * This class allows employees to pick their availability within the calendar UI for availability
 * which is then updated into the existing database.
 */
class AvailableActivity : AppCompatActivity() {

    private lateinit var datePicker: DatePicker
    private lateinit var amSwitch: Switch
    private lateinit var pmSwitch: Switch
    private lateinit var pm2Switch: Switch
    private lateinit var submitButton: Button
    private lateinit var selectedDatesTextView: TextView

    private val dateSelections: MutableMap<String, MutableList<Boolean>> = mutableMapOf()
    private var employeeId: Int? = AuthenticationHelper.getEmpID()



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_availability)

        // Initialize UI elements
        datePicker = findViewById(R.id.datePicker)
        amSwitch = findViewById(R.id.switchAM)
        pmSwitch = findViewById(R.id.switchPM)
        pm2Switch = findViewById(R.id.switchPM2)
        submitButton = findViewById(R.id.submitButton)
        selectedDatesTextView = findViewById(R.id.selectedDatesTextView)

        // Set the initial date to the next month
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, 1) // This changes it to the next month
        val nextMonth = calendar.get(Calendar.MONTH)
        val nextYear = calendar.get(Calendar.YEAR)

        // Set the DatePicker to display the next month
        datePicker.init(nextYear, nextMonth, 1, null)

        //dateSelections["2023-01-01"] = mutableListOf(true,true,true)
        // Set a listener for the submit button
        submitButton.setOnClickListener {
            val selectedDate = formatDate(datePicker.year, datePicker.month, datePicker.dayOfMonth)
            val amSelected = amSwitch.isChecked
            val pmSelected = pmSwitch.isChecked
            val pm2Selected = pm2Switch.isChecked

            // Store AM and PM selections for the selected date
            //dateSelections[selectedDate] = Pair(amSelected, pmSelected, pm2Selected)
            dateSelections[selectedDate] =  mutableListOf(amSelected, pmSelected, pm2Selected) //Pair(amSelected, pmSelected)

            // Update the selectedDatesTextView
            updateSelectedDatesText()

            val db = DatabaseScheduler(this).writableDatabase
            // Over write book offs for that month
            //db.execSQL("Delete from BookOff")
            db.execSQL("DELETE FROM BookOff WHERE strftime('%Y-%m', dateShift) = '${datePicker.year}-${datePicker.month}' and EmpID = $employeeId")
            // store in database
            //printSelections()
            var x: Boolean
            for (dateDict in dateSelections){// go through all dates
                for (j in 0..<dateDict.value.size){ // go through all shifts for that date
                    //println(" shift: $j for date: ${dateDict.key} = ${dateDict.value.get(j)}")
                    if(!dateDict.value.get(j)){
                        //println("Iteration $j")
                        db.execSQL("INSERT INTO 'BookOff' (ShiftID, EmpID, dateShift) VALUES ($j, $employeeId, '${dateDict.key}');")
                    }
                // insert the book off for tht day and shift
                }


            }
            val selection = getSelections()

            // Show a toast message
            val message = "Availability for Employee $employeeId succesfully saved"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

            printSelections()

            // Clear the DatePicker selection
            datePicker.init(datePicker.year, datePicker.month, datePicker.dayOfMonth, null)
            selectedDatesTextView.text = ""
        }

        // Set a listener for the DatePicker
        datePicker.init(datePicker.year, datePicker.month, datePicker.dayOfMonth) { _, year, month, dayOfMonth ->
            val selectedDate = formatDate(year, month, dayOfMonth)

            // Update the AM and PM switches based on the stored selections
            val amPmSelection = dateSelections[selectedDate]
            amSwitch.isChecked = amPmSelection?.get(0) == true
            pmSwitch.isChecked = amPmSelection?.get(1) == true
            pm2Switch.isChecked = amPmSelection?.get(2) == true

            // Update the selectedDatesTextView
            updateSelectedDatesText()
        }

        // Set a listener for the AM switch
        amSwitch.setOnCheckedChangeListener { _, isChecked ->
            val selectedDate = formatDate(datePicker.year, datePicker.month, datePicker.dayOfMonth)

            // Update the AM selection for the selected date
            //dateSelections[selectedDate] = mutablelistof<Boolean>()
            dateSelections[selectedDate]?.set(0, isChecked)
            //= MutableList(dateSelections[selectedDate].get(0), isChecked) //Pair( dateSelections[selectedDate]?.second ?: false, isChecked)

            updateSelectedDatesText()
        }

        // Set a listener for the PM switch
        pmSwitch.setOnCheckedChangeListener { _, isChecked ->
            val selectedDate = formatDate(datePicker.year, datePicker.month, datePicker.dayOfMonth)

            // Update the PM selection for the selected date
            dateSelections[selectedDate]?.set(1, isChecked)

            updateSelectedDatesText()
        }

        // Set a listener for the PM2 switch
        pm2Switch.setOnCheckedChangeListener { _, isChecked ->
            val selectedDate = formatDate(datePicker.year, datePicker.month, datePicker.dayOfMonth)

            // Update the PM2 selection for the selected date
            dateSelections[selectedDate]?.set(2, isChecked)

            updateSelectedDatesText()
        }
    }

    /** Returns year, month, and date to follow a standard format YYYY-MM-DD. */
    private fun formatDate(year: Int, month: Int, day: Int): String {
        val formattedMonth = if (month < 9) "0${month + 1}" else "${month + 1}"
        val formattedDay = if (day < 10) "0$day" else day.toString()
        return "$year-$formattedMonth-$formattedDay"
    }

    /**
     * This function provides easy readable text for users of the application to ensure proper
     * date has been selected when inputting availability options.
     */
    private fun updateSelectedDatesText() {
        val selectedDate = formatDate(datePicker.year, datePicker.month, datePicker.dayOfMonth)
        val amSelected = amSwitch.isChecked
        val pmSelected = pmSwitch.isChecked
        val pm2Selected = pm2Switch.isChecked
        //add for evening shift.
        dateSelections[selectedDate] = mutableListOf(amSelected, pmSelected, pm2Selected)

        val message = "Employee: $employeeId Selected date: $selectedDate, Morning: ${if (amSelected) "Selected" else "Not Selected"}, Afternoon: ${if (pmSelected) "Selected" else "Not Selected"} , Evening: ${if (pm2Selected) "Selected" else "Not Selected"}"
        selectedDatesTextView.text = message
    }

    /** This function prints the selection of dates. */
    private fun printSelections() {
        for ((date, amPmSelection) in dateSelections) {
            val amSelected = if (amPmSelection[0]) "Selected" else "Not Selected"
            val pmSelected = if (amPmSelection[1]) "Selected" else "Not Selected"
            val pm2Selected = if (amPmSelection[2]) "Selected" else "Not Selected"
            println("Date: $date, AM: $amSelected, PM: $pmSelected, PM2: $pm2Selected")
        }
    }

    // This function is where i am returning the list
    private fun getSelections(): List<String> {
        val selections = mutableListOf<String>()

        for ((date, amPm) in dateSelections) {
            val (amSelected, pmSelected, pm2Selected) = amPm
            val message = "Date: $date, Morning: ${if (amSelected) "Selected" else "Not Selected"}, Afternoon: ${if (pmSelected) "Selected" else "Not Selected"} , Evening: ${if (pm2Selected) "Selected" else "Not Selected"}"
            selections.add(message)
        }
        return selections
    }

}

