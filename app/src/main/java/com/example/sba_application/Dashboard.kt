package com.example.sba_application

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class Dashboard : ComponentActivity()
{

    //region Variables

    private lateinit var btnAddHost: Button
    private lateinit var btnAddStudent: Button
    private lateinit var btnManageBookings: Button

    //endregion

    //region Activity Lifecycle

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        initialiseViews()
        setupButtons()
    }

    //endregion

    //region Initialisation

    private fun initialiseViews()
    {

        btnAddHost = findViewById(R.id.btnAddHost)

        btnAddStudent = findViewById(R.id.btnAddStudent)

        btnManageBookings = findViewById(R.id.btnManageBookings)
    }

    //endregion

    //region Button Navigation

    private fun setupButtons()
    {

        btnAddHost.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    AddHost::class.java
                )
            )
        }

        btnAddStudent.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    AddStudent::class.java
                )
            )
        }

        btnManageBookings.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                )
            )
        }
    }

    //endregion
}
