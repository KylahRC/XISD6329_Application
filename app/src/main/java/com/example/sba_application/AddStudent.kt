//region AddStudent.kt

    /**
     * Add Student Activity
     * Manages data capture and database storage for student applicants looking for accommodation.
     */

    //region Packages

        package com.example.sba_application

    //endregion

    //region Imports

        import android.content.Intent
        import android.os.Bundle
        import android.widget.Button
        import android.widget.EditText
        import android.widget.Spinner
        import android.widget.Toast
        import androidx.appcompat.app.AppCompatActivity
        import com.google.firebase.firestore.FirebaseFirestore

    //endregion

    //region Add Student Class

        class AddStudent : AppCompatActivity()
        {

            //region Variables

                //region Buttons

                    private lateinit var returnBtn: Button
                    private lateinit var btnSaveStudent: Button

                //endregion

                //region Text Fields

                    private lateinit var etStudentName: EditText
                    private lateinit var etStudentSurname: EditText
                    private lateinit var etStudentPhone: EditText
                    private lateinit var etStudentAge: EditText
                    private lateinit var etStudentEmail: EditText

                //endregion

                //region Spinners

                    private lateinit var spinnerTypeOfStudy: Spinner
                    private lateinit var spinnerInstitution: Spinner

                //endregion

                //region Firebase

                    private val db = FirebaseFirestore.getInstance()

                //endregion

            //endregion

            //region OnCreate

                override fun onCreate(savedInstanceState: Bundle?)
                {
                    super.onCreate(savedInstanceState)
                    setContentView(R.layout.activity_add_student)

                    initialiseViews()
                    setupListeners()
                }

            //endregion

            //region Initialisation

                //region View Initialisation

                    /**
                     * Binds Kotlin object variables to XML views defined in activity_add_student.xml.
                     */
                    private fun initialiseViews()
                    {

                        initialiseButtons()

                        initialiseTextFields()

                        initialiseSpinners()
                    }

                //endregion

                //region Button Initialisation

                    private fun initialiseButtons()
                    {

                        returnBtn = findViewById(R.id.ReturnBtn)

                        btnSaveStudent = findViewById(R.id.btnAddStudentSubmit)
                    }

                //endregion

                //region Text Field Initialisation

                    private fun initialiseTextFields()
                    {

                        etStudentName = findViewById(R.id.etStudentName)

                        etStudentSurname = findViewById(R.id.etStudentSurname)

                        etStudentPhone = findViewById(R.id.etStudentPhone)

                        etStudentAge = findViewById(R.id.etStudentAge)

                        etStudentEmail = findViewById(R.id.etStudentEmail)
                    }

                //endregion

                //region Spinner Initialisation

                    private fun initialiseSpinners()
                    {

                        spinnerTypeOfStudy = findViewById(R.id.spinnerTypeOfStudy)

                        spinnerInstitution = findViewById(R.id.spinnerInstitution)
                    }

                //endregion

            //endregion

            //region Button Setup

                //region Listeners

                    /**
                     * Connects event handlers for navigation and form persistence.
                     */
                    private fun setupListeners()
                    {

                        setupReturnButton()

                        setupSaveButton()
                    }

                //endregion

                //region Return Button

                    private fun setupReturnButton()
                    {

                        // Navigates back to Dashboard
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

                        // Handles Firestore student insertion
                        btnSaveStudent.setOnClickListener {

                            saveStudentToFirestore()
                        }
                    }

                //endregion

            //endregion

            //region Student Creation

                /**
                 * Validates input fields and uploads a student record to the 'Students' collection.
                 */
                private fun saveStudentToFirestore()
                {
                    val studentData = createStudentMap()

                    createStudent(studentData)
                }

            //endregion

            //region Student Object Creation

                private fun createStudentMap():HashMap<String, String>
                {
                    val studentData =HashMap<String, String>()

                    studentData["Student_Name"] =etStudentName.text.toString()

                    studentData["Student_Surname"] = etStudentSurname.text.toString()

                    studentData["Contact_No"] = etStudentPhone.text.toString()

                    studentData["Age"] = etStudentAge.text.toString()

                    studentData["Type_Of_Study"] = spinnerTypeOfStudy.selectedItem.toString()

                    studentData["Institution"] = spinnerInstitution.selectedItem.toString()

                    return studentData
                }

            //endregion

            //region Firebase Student Creation

                //region Make Student

                        private fun createStudent(studentData: HashMap<String, String>)
                        {
                            db.collection("Students").get()
                                .addOnSuccessListener { result ->

                                    val studentId =generateStudentId(result)

                                    saveStudentToFirestore(studentId, studentData)
                                }
                        }

                //endregion

                //region Make ID

                    private fun generateStudentId(result: com.google.firebase.firestore.QuerySnapshot): String
                    {
                        var highestNumber = 0

                        for (document in result)
                        {
                            val documentId = document.id

                            if (documentId.startsWith("ST"))
                            {
                                val number = documentId.substring(2).toIntOrNull()

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

                        return "ST%03d".format(nextNumber)
                    }

                //endregion

                //region Save stufent

                    private fun saveStudentToFirestore(studentId: String, studentData: HashMap<String, String>)
                    {
                        db.collection("Students").document(studentId).set(studentData)
                            .addOnSuccessListener {

                                Toast.makeText(this, "Student saved as $studentId", Toast.LENGTH_SHORT).show()

                                startActivity(Intent(this, Dashboard::class.java))

                                finish()
                            }
                    }

                //endregion

            //endregion
        }

    //endregion

//endregion