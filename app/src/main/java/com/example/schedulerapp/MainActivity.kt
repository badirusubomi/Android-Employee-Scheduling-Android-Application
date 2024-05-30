package com.example.schedulerapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.material.switchmaterial.SwitchMaterial

class MainActivity : AppCompatActivity() {

    private lateinit var switchDarkMode: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)

        // Note: To delete the existing database, uncomment this code and run once then close the application and comment the below code and run again.
        //val context = this
        //context.deleteDatabase(DATABASE_NAME)

        // Initialize the switch
        switchDarkMode = findViewById(R.id.switchDarkMode)

        // Set the switch state based on the current night mode
        switchDarkMode.isChecked = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        // Set a listener for the switch
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            val newMode = if (isChecked)
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO

            AppCompatDelegate.setDefaultNightMode(newMode)
            recreate() // Recreate the activity to apply the new mode
        }

        // Navigate to employee screen
        val employeeActivityCV = findViewById<CardView>(R.id.employeeCV)
        employeeActivityCV.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Navigate to manager screen
        val managerActivityCV = findViewById<CardView>(R.id.managerCV)
        managerActivityCV.setOnClickListener {
            startActivity(Intent(this, ManagerActivity::class.java))
        }
    }

    // This method is called when the switch is clicked
    fun toggleMode(view: View) {
        if (view is SwitchMaterial) {
            val isChecked = view.isChecked
            val newMode = if (isChecked)
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO

            AppCompatDelegate.setDefaultNightMode(newMode)
            recreate() // Recreate the activity to apply the new mode
        }
    }

}
