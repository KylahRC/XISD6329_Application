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

    private lateinit var etHostSurname: EditText
    private lateinit var etHostAddress: EditText
    private lateinit var etHostEmail: EditText


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
        etHostSurname = findViewById(R.id.etHostSurname)
        etHostAddress = findViewById(R.id.etHostAddress)
        etHostEmail = findViewById(R.id.etHostEmail)
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
        val surname = etHostSurname.text.toString().trim()
        val address = etHostAddress.text.toString().trim()
        val email = etHostEmail.text.toString().trim()

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
            "Host_Surname" to surname,
            "Host_Address" to address,

            "Host_Area" to area,
            "Host_Bathroom" to bathroom,

            "Host_PhoneNumber" to phone,
            "Host_Email" to email

        )

        createHost(hostData)

    }

    private fun createHost(
        hostData: HashMap<String, String>
    )
    {
        db.collection("Host Families")
            .get()
            .addOnSuccessListener { result ->

                var highestNumber = 0

                for (document in result) {

                    val documentId = document.id

                    if (
                        documentId.startsWith("H")
                    ) {

                        val number =
                            documentId
                                .substring(1)
                                .toIntOrNull()

                        if (
                            number != null &&
                            number > highestNumber
                        ) {
                            highestNumber = number
                        }
                    }
                }

                val nextNumber =
                    highestNumber + 1

                val newHostId =
                    "H%03d".format(nextNumber)

                db.collection("Host Families")
                    .document(newHostId)
                    .set(hostData)
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
}