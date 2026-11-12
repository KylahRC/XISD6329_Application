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


    private lateinit var etStudentSurname: EditText
    private lateinit var etStudentPhone: EditText
    private lateinit var etStudentAge: EditText
    private lateinit var spinnerTypeOfStudy: Spinner
    private lateinit var spinnerInstitution: Spinner


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
        etStudentSurname = findViewById(R.id.etStudentSurname)
        etStudentPhone = findViewById(R.id.etStudentPhone)
        etStudentAge = findViewById(R.id.etStudentAge)
        spinnerTypeOfStudy = findViewById(R.id.spinnerTypeOfStudy)
        spinnerInstitution = findViewById(R.id.spinnerInstitution)
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
    private fun saveStudentToFirestore()
    {
        val studentData = hashMapOf(

            "Student_Name" to
                    etStudentName.text.toString(),

            "Student_Surname" to
                    etStudentSurname.text.toString(),

            "Contact_No" to
                    etStudentPhone.text.toString(),

            "Age" to
                    etStudentAge.text.toString(),

            "Type_Of_Study" to
                    spinnerTypeOfStudy.selectedItem.toString(),

            "Institution" to
                    spinnerInstitution.selectedItem.toString()

        )

        createStudent(studentData)

    }

    private fun createStudent(
        studentData: HashMap<String, String>
    )
    {
        db.collection("Students")
            .get()
            .addOnSuccessListener { result ->

                var highestNumber = 0

                for(document in result)
                {
                    val documentId = document.id

                    if(documentId.startsWith("ST"))
                    {
                        val number =
                            documentId
                                .substring(2)
                                .toIntOrNull()

                        if(number != null &&
                            number > highestNumber)
                        {
                            highestNumber = number
                        }
                    }
                }

                val nextNumber =
                    highestNumber + 1

                val studentId =
                    "ST%03d".format(nextNumber)

                db.collection("Students")
                    .document(studentId)
                    .set(studentData)

                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Student saved as $studentId",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(
                            Intent(
                                this,
                                Dashboard::class.java
                            )
                        )

                        finish()
                    }
            }
    }
}