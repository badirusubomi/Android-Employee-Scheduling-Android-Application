package com.example.schedulerapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.HashSet

/** This class provides methods to interact with list of Employee instances. */
class EmployeeListAdapter(private val context: Context, private var employeeList: List<Employee>) :
    BaseAdapter() {

    private val selectedEmployees = HashSet<Int>()

    /** Returns number of employees within the list (database). */
    override fun getCount(): Int {
        return employeeList.size
    }

    /** Returns employee's position within the list. */
    override fun getItem(position: Int): Any {
        return employeeList[position]
    }

    /** Returns employee ID. */
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    /** Returns employee list as ListView layout property. */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val holder: ViewHolder

        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.activity_employee_info, null)
            holder = ViewHolder()
            holder.employeeIdTextView = view.findViewById(R.id.textViewEmployeeId)
            holder.firstNameTextView = view.findViewById(R.id.textViewFirstName)
            holder.lastNameTextView = view.findViewById(R.id.textViewLastName)
            //holder.phoneTextView = view.findViewById(R.id.textViewPhone)
            //holder.emailTextView = view.findViewById(R.id.textViewEmail)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        // Makes buttons not visible in ListView
        // val refreshButton = view?.findViewById<FloatingActionButton>(R.id.btnRefresh)
        val deleteButton = view?.findViewById<FloatingActionButton>(R.id.btnDelete)
        val addButton = view?.findViewById<FloatingActionButton>(R.id.addEmployee)

//        if (refreshButton != null) {
//            refreshButton.isVisible = false
//        }

        if (deleteButton != null) {
            deleteButton.isVisible = false
        }

        if (addButton != null) {
            addButton.isVisible = false
        }

        // Get the employee object for this position
        val employee = getItem(position) as Employee

        // Set the employee data to the views
        holder.employeeIdTextView.text = "${employee.empId}" // Display the employee ID
        holder.firstNameTextView.text = employee.fName
        holder.lastNameTextView.text = employee.lName
        //holder.phoneTextView.text = employee.phone
        //holder.emailTextView.text = employee.email

        // Handle row click to toggle employee selection
//        view?.setOnClickListener {
//            toggleEmployeeSelection(position)
//        }

        // Handle row click to open EmployeeDetailsActivity
        view?.setOnClickListener {
            val intent = Intent(context, EmployeeDetailsActivity::class.java)
            intent.putExtra("employee", employee)
            context.startActivity(intent)
        }
        // Highlight the selected employee, if applicable
        if (selectedEmployees.contains(position)) {
            // You can customize the appearance of the selected employee here
            view?.setBackgroundResource(R.color.selectedEmployeeBackground)
        } else {
            // Reset the background color for unselected employees
            view?.setBackgroundResource(0)
        }
        return view!!
    }


    // Toggle employee selection based on position
//    private fun toggleEmployeeSelection(position: Int) {
//        if (selectedEmployees.contains(position)) {
//            selectedEmployees.remove(position)
//        } else {
//            selectedEmployees.add(position)
//        }
//        notifyDataSetChanged()
//    }
//
//    // Return a list of selected employees
//    fun getSelectedEmployees(): List<Employee> {
//        return selectedEmployees.map { employeeList[it] }
//    }

    fun updateEmployeeList(newEmployeeList: List<Employee>) {
        // Update the employee list and notify the adapter of changes
        employeeList = newEmployeeList
        notifyDataSetChanged()
    }

    private class ViewHolder {
        lateinit var employeeIdTextView: TextView
        lateinit var firstNameTextView: TextView
        lateinit var lastNameTextView: TextView
//        lateinit var phoneTextView: TextView
//        lateinit var emailTextView: TextView
    }
}
