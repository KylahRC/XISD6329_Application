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
class AddHost : AppCompatActivity()
{

    //region Member Variables

    //region Buttons

    private lateinit var returnBtn: Button
    private lateinit var btnSaveHost: Button

    //endregion

    //region Text Fields

    private lateinit var etHostName: EditText
    private lateinit var etHostSurname: EditText
    private lateinit var etHostAddress: EditText
    private lateinit var etHostPhone: EditText
    private lateinit var etHostEmail: EditText

    //endregion

    //region Spinners

    private lateinit var spinnerArea: Spinner
    private lateinit var spinnerBathroom: Spinner

    //endregion

    //region Firebase

    private val db =
        FirebaseFirestore.getInstance()

    //endregion

    //endregion

    //region Activity Lifecycle

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_host)

        initialiseViews()
        setupListeners()
    }

    //endregion

    //region Initialisation

    /**
     * Binds internal variable declarations to corresponding XML views.
     */
    private fun initialiseViews()
    {

        initialiseButtons()

        initialiseTextFields()

        initialiseSpinners()
    }

    //region Button Initialisation

    private fun initialiseButtons()
    {

        returnBtn =
            findViewById(R.id.ReturnBtn)

        btnSaveHost =
            findViewById(R.id.btnAddHostSubmit)
    }

    //endregion

    //region Text Field Initialisation

    private fun initialiseTextFields()
    {

        etHostName =
            findViewById(R.id.etHostName)

        etHostSurname =
            findViewById(R.id.etHostSurname)

        etHostAddress =
            findViewById(R.id.etHostAddress)

        etHostPhone =
            findViewById(R.id.etHostPhone)

        etHostEmail =
            findViewById(R.id.etHostEmail)
    }

    //endregion

    //region Spinner Initialisation

    private fun initialiseSpinners()
    {

        spinnerArea =
            findViewById(R.id.spinnerHostArea)

        spinnerBathroom =
            findViewById(R.id.spinnerHostBathroom)
    }

    //endregion

    //endregion

    //region Button Setup

    /**
     * Connects event handlers for navigation and form submission logic.
     */
    private fun setupListeners()
    {

        setupReturnButton()

        setupSaveButton()
    }

    //region Return Button

    private fun setupReturnButton()
    {

        // Navigates back to the main control Dashboard
        returnBtn.setOnClickListener {

            val intent =
                Intent(
                    this,
                    Dashboard::class.java
                )

            startActivity(intent)

            finish()
        }
    }

    //endregion

    //region Save Button

    private fun setupSaveButton()
    {

        // Saving input host profile to Firebase Firestore
        btnSaveHost.setOnClickListener {

            saveHostToFirestore()
        }
    }

    //endregion

    //endregion

    //region Host Creation

    /**
     * Validates user entries and uploads a new host object to the 'Host Families' collection.
     */
    private fun saveHostToFirestore()
    {

        val name = etHostName.text.toString().trim()

        val surname = etHostSurname.text.toString().trim()

        val address = etHostAddress.text.toString().trim()

        val phone = etHostPhone.text.toString().trim()

        val email = etHostEmail.text.toString().trim()

        val area = spinnerArea.selectedItem.toString()

        val bathroom = spinnerBathroom.selectedItem.toString()

        if (!validateForm(name, phone))
        {
            return
        }

        val hostData =
            createHostMap(
                name,
                surname,
                address,
                area,
                bathroom,
                phone,
                email
            )

        createHost(hostData)
    }

    //endregion

    //region Validation

    private fun validateForm(
        name: String,
        phone: String
    ): Boolean {

        if (TextUtils.isEmpty(name))
        {

            etHostName.error ="Please enter host name"

            return false
        }

        if (TextUtils.isEmpty(phone))
        {

            etHostPhone.error = "Please enter contact number"

            return false
        }

        return true
    }

    //endregion

    //region Host Object Creation

    private fun createHostMap(
        name: String,
        surname: String,
        address: String,
        area: String,
        bathroom: String,
        phone: String,
        email: String
    ): HashMap<String, String>
    {

        val hostData = HashMap<String, String>()

        hostData["Host_Name"] = name

        hostData["Host_Surname"] = surname

        hostData["Host_Address"] = address

        hostData["Host_Area"] = area

        hostData["Host_Bathroom"] = bathroom

        hostData["Host_PhoneNumber"] = phone

        hostData["Host_Email"] = email

        return hostData
    }

    //endregion

    //region Firebase Host Creation

    private fun createHost(hostData: HashMap<String, String>)
    {

        db.collection("Host Families")
            .get()
            .addOnSuccessListener { result ->

                val hostId = generateHostId(result)

                saveHostToFirestore(
                    hostId,
                    hostData
                )
            }
    }

    private fun generateHostId(
        result: com.google.firebase.firestore.QuerySnapshot
    ): String {

        var highestNumber = 0

        for (document in result)
        {

            val documentId =
                document.id

            if (documentId.startsWith("H"))
            {

                val number =
                    documentId
                        .substring(1)
                        .toIntOrNull()

                if (number != null)
                {

                    if (number > highestNumber)
                    {

                        highestNumber =
                            number
                    }
                }
            }
        }

        val nextNumber =
            highestNumber + 1

        return "H%03d".format(nextNumber)
    }

    private fun saveHostToFirestore(
        hostId: String,
        hostData: HashMap<String, String>
    ) {

        db.collection("Host Families")
            .document(hostId)
            .set(hostData)

            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Host record saved successfully!",
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

            .addOnFailureListener { e ->

                Toast.makeText(
                    this,
                    "Failed to save host: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    //endregion
}