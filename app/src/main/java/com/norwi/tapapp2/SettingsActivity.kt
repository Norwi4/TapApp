package com.norwi.tapapp2

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val spinner = findViewById<Spinner>(R.id.spinnerTheme)
        val themes = listOf("Светлая", "Тёмная", "Системная")
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, themes)

        val currentMode = AppCompatDelegate.getDefaultNightMode()
        spinner.setSelection(
            when (currentMode) {
                AppCompatDelegate.MODE_NIGHT_YES -> 1
                AppCompatDelegate.MODE_NIGHT_NO -> 0
                else -> 2
            }
        )

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                when (pos) {
                    0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}