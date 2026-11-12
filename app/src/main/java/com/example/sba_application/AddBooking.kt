package com.example.sba_application

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class AddBooking : ComponentActivity() {

    //region Variables

    private lateinit var returnBtn: Button
    private lateinit var btnSaveBooking: Button

    private lateinit var spinnerStudent: Spinner

    private lateinit var etStartDate: EditText
    private lateinit var etEndDate: EditText

    private val db = FirebaseFirestore.getInstance()

    private var hostId: String = ""

    //endregion

    //region Activity Lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_booking)

        hostId =
            intent.getStringExtra("HOST_ID") ?: ""

        initialiseViews()
        setupListeners()
        loadStudents()
    }

    //endregion

    //region Initialisation

    private fun initialiseViews() {

        returnBtn =
            findViewById(R.id.ReturnBtn)

        btnSaveBooking =
            findViewById(R.id.btnSaveBooking)

        spinnerStudent =
            findViewById(R.id.spinnerStudent)

        etStartDate =
            findViewById(R.id.etStartDate)

        etEndDate =
            findViewById(R.id.etEndDate)
    }

    //endregion

    //region Button Setup

    private fun setupListeners() {

        returnBtn.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    Dashboard::class.java
                )
            )

            finish()
        }

        btnSaveBooking.setOnClickListener {
            saveBooking()
        }
    }

    //endregion

    //region Student Loading

    private fun loadStudents() {

        val studentIds =
            mutableListOf<String>()

        db.collection("Students")
            .get()
            .addOnSuccessListener { result ->

                for (document in result) {
                    studentIds.add(document.id)
                }

                val adapter =
                    ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_item,
                        studentIds
                    )

                adapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
                )

                spinnerStudent.adapter =
                    adapter
            }
    }

    //endregion

    //region Booking Creation

    private fun saveBooking() {

        val studentId =
            spinnerStudent.selectedItem.toString()

        val startDateString =
            etStartDate.text.toString().trim()

        val endDateString =
            etEndDate.text.toString().trim()

        if (TextUtils.isEmpty(startDateString)) {

            etStartDate.error =
                "Please enter a start date"

            return
        }

        if (TextUtils.isEmpty(endDateString)) {

            etEndDate.error =
                "Please enter an end date"

            return
        }

        try {

            val formatter =
                SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.getDefault()
                )

            val startDate =
                formatter.parse(startDateString)

            val endDate =
                formatter.parse(endDateString)

            val bookingData = hashMapOf<String, Any>(

                "Student_ID" to studentId,

                "Host_ID" to hostId,

                "StartDate" to Timestamp(startDate),

                "FinishDate" to Timestamp(endDate)

            )

            createBooking(bookingData)

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Use format DD/MM/YYYY",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun createBooking(
        bookingData: HashMap<String, Any>
    ) {

        db.collection("Bookings")
            .get()
            .addOnSuccessListener { result ->

                var highestNumber = 0

                for (document in result) {

                    val documentId =
                        document.id

                    if (documentId.startsWith("B")) {

                        val number =
                            documentId
                                .substring(1)
                                .toIntOrNull()

                        if (
                            number != null &&
                            number > highestNumber
                        ) {
                            highestNumber =
                                number
                        }
                    }
                }

                val nextNumber =
                    highestNumber + 1

                val bookingId =
                    "B%03d".format(nextNumber)

                db.collection("Bookings")
                    .document(bookingId)
                    .set(bookingData)

                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Booking created: $bookingId",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                    }

                    .addOnFailureListener { e ->

                        Toast.makeText(
                            this,
                            e.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
    }

    //endregion
}