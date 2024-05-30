package com.example.schedulerapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/** This class provides the employee to input their login fields to allow to the app. */
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameEditText = findViewById<EditText>(R.id.usernameEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)

        // Grabs employee login information
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Verify username and password, and determine user type (employee or manager)
            if (AuthenticationHelper.isValidLogin(this, username, password)) {
                if (AuthenticationHelper.isEmployee(this, username)) {
                    // Start the EmployeeActivity with the "USERNAME" extra
                    val intent = Intent(this, EmployeeActivity::class.java)
                    intent.putExtra("USERNAME", username) // Pass the username as "USERNAME"
                    startActivity(intent)
                } else if (AuthenticationHelper.isManager(this, username)) {
                    // Start the ManagerActivity
                    startActivity(Intent(this, ManagerActivity::class.java))
                }
            } else {
                // Show an error message for invalid login
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }
        // Maybe add a way to clear the fields on resume
    }
}
