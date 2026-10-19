package com.example.sba_application

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

/**
 * Dashboard Activity
 *
 * Central navigation screen for the Study Better Agency application.
 *
 * Manages UI user transitions between AddHost, AddStudent, SearchHosts, MainActivity (Manage Bookings),
 *
 * and performs session termination (Logout) using Firebase Auth.
 */
class Dashboard : AppCompatActivity() {

    //region Variables

    // Navigation and Action Button UI Declarations
    private lateinit var btnAddHost: Button
    private lateinit var btnAddStudent: Button
    private lateinit var btnSearchHosts: Button
    private lateinit var btnManageBookings: Button
    private lateinit var btnLogout: Button

    // Firebase Authentication Instance for handling user logout
    private lateinit var auth: FirebaseAuth

    //endregion

    //region Activity Lifecycle

    /**
     * Called when the activity is first created. Initializes screen components,
     *
     * connects view bindings, and establishes click listener event handling.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Initialize Firebase Authentication Instance
        auth = FirebaseAuth.getInstance()

        // Bind layout views and assign action logic
        initialiseViews()
        setupButtons()
    }

    //endregion

    //region Initialisation

    /**
     * Binds internal layout variables to their respective XML views defined in activity_dashboard.xml.
     */
    private fun initialiseViews() {
        btnAddHost = findViewById(R.id.btnAddHost)
        btnAddStudent = findViewById(R.id.btnAddStudent)
        btnSearchHosts = findViewById(R.id.btnSearchHosts)
        btnManageBookings = findViewById(R.id.btnManageBookings)
        btnLogout = findViewById(R.id.btnLogout)
    }

    //endregion

    //region Button Navigation

    /**
     * Assigns event click listeners to dashboard buttons to navigate across application modules
     *
     * or log out of the current session cleanly.
     */
    private fun setupButtons() {

        // Navigates to the AddHost Activity form
        btnAddHost.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AddHost::class.java
                )
            )
        }

        // Navigates to the AddStudent Activity form
        btnAddStudent.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AddStudent::class.java
                )
            )
        }

        // Navigates to MainActivity to filter and view host family listings
        btnSearchHosts.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                )
            )
        }

        // Handles booking management overview
        //linked this button to earch and manage hosts to avoid having a button on the dashboard that is not functional.
        btnManageBookings.setOnClickListener {
            Toast.makeText(this, "Opening active bookings overview...", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("FILTER_MODE", "BOOKINGS")
            startActivity(intent)
        }

        // Signs out the user from Firebase and returns them to the Login screen
        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, Login::class.java)
            // Clear activity stack so user cannot return to Dashboard with back button
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    //endregion
}