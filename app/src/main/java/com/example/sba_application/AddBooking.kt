//region AddBooking.kt

    //TODO: Add description of what this file does

    //region Packages

        package com.example.sba_application

    //endregion

    //region Imports

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

    //endregion

    //region Add Booking Class

        class AddBooking : ComponentActivity()
        {

            //region Variables

                //region Buttons

                    private lateinit var returnBtn: Button
                    private lateinit var btnSaveBooking: Button

                //endregion

                //region Form Controls

                    private lateinit var spinnerStudent: Spinner

                    private lateinit var etStartDate: EditText
                    private lateinit var etEndDate: EditText

                //endregion

                //region Firebase

                    private val db = FirebaseFirestore.getInstance()

                //endregion

                //region Booking Data

                    private var hostId = ""

                //endregion

            //endregion

            //region onCreate

                override fun onCreate(savedInstanceState: Bundle?)
                {
                    super.onCreate(savedInstanceState)
                    setContentView(R.layout.activity_add_booking)

                    retrieveIntentData()

                    initialiseViews()

                    setupListeners()

                    loadStudents()
                }

            //endregion

            //region Initialisation

                //region Views Initialisation

                    private fun initialiseViews()
                    {
                        initialiseButtons()

                        initialiseFormControls()
                    }

                //endregion

                //region Button Initialisation

                    private fun initialiseButtons()
                    {
                        returnBtn = findViewById(R.id.ReturnBtn)

                        btnSaveBooking = findViewById(R.id.btnSaveBooking)
                    }

                //endregion

                //region Form Initialisation

                    private fun initialiseFormControls()
                    {
                        spinnerStudent = findViewById(R.id.spinnerStudent)

                        etStartDate = findViewById(R.id.etStartDate)

                        etEndDate = findViewById(R.id.etEndDate)
                    }

                //endregion

            //endregion

            //region Intent Data

                private fun retrieveIntentData()
                {
                    val receivedHostId = intent.getStringExtra("HOST_ID")

                    if (receivedHostId != null)
                    {
                        hostId = receivedHostId
                    }
                }

            //endregion

            //region Button Setup

                //region Listener

                    private fun setupListeners()
                    {
                        setupReturnButton()

                        setupSaveButton()
                    }

                //endregion

                //region Return Button

                    private fun setupReturnButton()
                    {
                        returnBtn.setOnClickListener {

                            val intent = Intent(this, Dashboard::class.java)

                            startActivity(intent)

                            finish()
                        }
                    }

                //endregion

                //region Save Button

                    private fun setupSaveButton()
                    {
                        btnSaveBooking.setOnClickListener {

                            saveBooking()
                        }
                    }

                //endregion

            //endregion

            //region Student Handleing

                //region Loading

                    private fun loadStudents()
                    {
                        val studentIds = mutableListOf<String>()

                        db.collection("Students")
                            .get()
                            .addOnSuccessListener { result ->

                                for (document in result)
                                {
                                    studentIds.add(document.id)
                                }

                                populateStudentSpinner(studentIds)
                            }
                    }

                //endregion

                //region Populating

                    private fun populateStudentSpinner(studentIds: List<String>)
                    {
                        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, studentIds)

                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                        spinnerStudent.adapter =adapter
                    }

                //endregion

            //endregion

            //region Booking Creation

                private fun saveBooking()
                {
                    val studentId = spinnerStudent.selectedItem.toString()

                    val startDateString = etStartDate.text.toString().trim()

                    val endDateString = etEndDate.text.toString().trim()

                    if (!validateForm(startDateString, endDateString))
                    {
                        return
                    }

                    try
                    {
                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                        val startDate = formatter.parse(startDateString)

                        val endDate = formatter.parse(endDateString)

                        if (startDate == null ||endDate == null)
                        {
                            Toast.makeText(this, "Invalid date entered", Toast.LENGTH_SHORT).show()

                            return
                        }

                        val bookingData = createBookingMap(studentId, startDate, endDate)

                        createBooking(bookingData)
                    }
                    catch (e: Exception)
                    {
                        Toast.makeText(this, "Use format DD/MM/YYYY", Toast.LENGTH_SHORT).show()
                    }
                }

            //endregion

            //region Validation

                private fun validateForm(startDate: String, endDate: String): Boolean
                {
                    if (TextUtils.isEmpty(startDate))
                    {
                        etStartDate.error = "Please enter a start date"

                        return false
                    }

                    if (TextUtils.isEmpty(endDate))
                    {
                        etEndDate.error = "Please enter an end date"

                        return false
                    }

                    return true
                }

            //endregion

            //region Booking Object Creation

                private fun createBookingMap(studentId: String, startDate: java.util.Date, endDate: java.util.Date): HashMap<String, Any>
                {
                    val bookingData = HashMap<String, Any>()

                    bookingData["Student_ID"] = studentId

                    bookingData["Host_ID"] = hostId

                    bookingData["StartDate"] = Timestamp(startDate)

                    bookingData["FinishDate"] = Timestamp(endDate)

                    return bookingData
                }

            //endregion

            //region Firebase Booking Creation

                //region mMake Bookings

                private fun createBooking(bookingData: HashMap<String, Any>)
                {
                    db.collection("Bookings")
                        .get()
                        .addOnSuccessListener { result ->

                            val bookingId = generateBookingId(result)

                            saveBookingToFirestore(bookingId, bookingData)
                        }
                }

            //endregion

                //region Make Booking ID

                private fun generateBookingId(result: com.google.firebase.firestore.QuerySnapshot): String
                {
                    var highestNumber = 0

                    for (document in result)
                    {
                        val documentId =document.id

                        if (documentId.startsWith("B"))
                        {
                            val number = documentId.substring(1).toIntOrNull()

                            if (number != null)
                            {
                                if (number > highestNumber)
                                {
                                    highestNumber = number
                                }
                            }
                        }
                    }

                    val nextNumber = highestNumber + 1

                    return "B%03d".format(nextNumber)
                }

            //endregion

                //region Save Booking

                private fun saveBookingToFirestore(bookingId: String, bookingData: HashMap<String, Any>)
                {
                    db.collection("Bookings").document(bookingId).set(bookingData)

                        .addOnSuccessListener {

                            Toast.makeText(this, "Booking created: $bookingId", Toast.LENGTH_SHORT).show()

                            finish()
                        }

                        .addOnFailureListener { e ->

                            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                        }
                }

            //endregion

            //endregion
        }

    //endregion

//endregion