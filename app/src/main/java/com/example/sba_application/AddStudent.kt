package com.example.sba_application

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Add Student Activity
 * Manages data capture and database storage for student applicants looking for accommodation.
 */
class AddStudent : AppCompatActivity() {

    //region Member Variables
    private lateinit var returnBtn: Button
    private lateinit var btnSaveStudent: Button
    private lateinit var etStudentName: EditText
    private lateinit var etStudentEmail: EditText
    private lateinit var etStudentCampus: EditText

    private val db = FirebaseFirestore.getInstance()
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_student)

        initialiseViews()
        setupListeners()
    }

    /**
     * Binds Kotlin object variables to XML views defined in activity_add_student.xml.
     */
    private fun initialiseViews() {
        returnBtn = findViewById(R.id.ReturnBtn)
        btnSaveStudent = findViewById(R.id.btnAddStudentSubmit)
        etStudentName = findViewById(R.id.etStudentName)
        etStudentEmail = findViewById(R.id.etStudentEmail)
        etStudentCampus = findViewById(R.id.etStudentCampus)
    }

    /**
     * Connects event handlers for navigation and form persistence.
     */
    private fun setupListeners() {
        // Navigates back to Dashboard
        returnBtn.setOnClickListener {
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
            finish()
        }

        // Handles Firestore student insertion
        btnSaveStudent.setOnClickListener {
            saveStudentToFirestore()
        }
    }

    /**
     * Validates input fields and uploads a student record to the 'Students' collection.
     */
    private fun saveStudentToFirestore() {
        val name = etStudentName.text.toString().trim()
        val email = etStudentEmail.text.toString().trim()
        val campus = etStudentCampus.text.toString().trim()

        if (TextUtils.isEmpty(name)) {
            etStudentName.error = "Please enter student name"
            return
        }

        if (TextUtils.isEmpty(email)) {
            etStudentEmail.error = "Please enter student email"
            return
        }

        val studentData = hashMapOf(
            "Student_Name" to name,
            "Student_Email" to email,
            "Student_Campus" to campus
        )

        db.collection("Students")
            .add(studentData)
            .addOnSuccessListener {
                Toast.makeText(this, "Student record saved successfully!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, Dashboard::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save student: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}