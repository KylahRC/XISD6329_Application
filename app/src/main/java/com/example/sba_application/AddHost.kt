package com.example.sba_application

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Add Host Activity
 * Manages input collection and persistence for new host accommodation profiles into Firebase Firestore.
 */
class AddHost : AppCompatActivity() {

    //region Member Variables
    private lateinit var returnBtn: Button
    private lateinit var btnSaveHost: Button
    private lateinit var etHostName: EditText
    private lateinit var etHostPhone: EditText
    private lateinit var spinnerArea: Spinner
    private lateinit var spinnerBathroom: Spinner

    private val db = FirebaseFirestore.getInstance()
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_host)

        initialiseViews()
        setupListeners()
    }

    /**
     * Binds internal variable declarations to corresponding XML views.
     */
    private fun initialiseViews() {
        returnBtn = findViewById(R.id.ReturnBtn)
        btnSaveHost = findViewById(R.id.btnAddHostSubmit)
        etHostName = findViewById(R.id.etHostName)
        etHostPhone = findViewById(R.id.etHostPhone)
        spinnerArea = findViewById(R.id.spinnerHostArea)
        spinnerBathroom = findViewById(R.id.spinnerHostBathroom)
    }

    /**
     * Connects event handlers for navigation and form submission logic.
     */
    private fun setupListeners() {
        // Navigates back to the main control Dashboard
        returnBtn.setOnClickListener {
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
            finish()
        }

        // Saving input host profile to Firebase Firestore
        btnSaveHost.setOnClickListener {
            saveHostToFirestore()
        }
    }

    /**
     * Validates user entries and uploads a new host object to the 'Host Families' collection.
     */
    private fun saveHostToFirestore() {
        val name = etHostName.text.toString().trim()
        val phone = etHostPhone.text.toString().trim()
        val area = spinnerArea.selectedItem?.toString() ?: "All"
        val bathroom = spinnerBathroom.selectedItem?.toString() ?: "All"

        if (TextUtils.isEmpty(name)) {
            etHostName.error = "Please enter host name"
            return
        }

        if (TextUtils.isEmpty(phone)) {
            etHostPhone.error = "Please enter contact number"
            return
        }

        val hostData = hashMapOf(
            "Host_Name" to name,
            "Host_Area" to area,
            "Host_Bathroom" to bathroom,
            "Host_Phone" to phone
        )

        db.collection("Host Families")
            .add(hostData)
            .addOnSuccessListener {
                Toast.makeText(this, "Host record saved successfully!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, Dashboard::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save host: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}