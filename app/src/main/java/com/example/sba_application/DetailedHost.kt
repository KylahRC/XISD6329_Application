package com.example.sba_application

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Detailed Host Activity in correspondence to active_host_details.xml
 * Displays extensive host accommodation parameters and associated student booking dates.
 * Enables phone dialing, email dispatching and booking creation.
 */
//References to come soon.
// reminder: the blue colour is still to change.
//This is the last file edited for phase 1.
//Im also just testing out to see if adding a null safe data display causes any crashes.
class DetailedHost : AppCompatActivity() {

    //region Variables
    private val db = FirebaseFirestore.getInstance()

    // Host Information Views
    private lateinit var txtName: TextView
    private lateinit var txtArea: TextView
    private lateinit var txtBathroom: TextView
    private lateinit var txtAddress: TextView
    private lateinit var txtPhone: TextView
    private lateinit var txtEmail: TextView

    // Booking information views
    private lateinit var txtBookings: TextView

    // Navigation
    private lateinit var btnReturnDashboard: Button
    private lateinit var btnAddBooking: Button
    private lateinit var btnCallHost: Button
    private lateinit var btnEmailHost: Button

    private var currentHostPhone: String = ""
    private var currentHostEmail: String = ""

    private val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host_details)

        initialiseViews()
        setupListeners()

        val hostId = intent.getStringExtra("HOST_ID")
        if (hostId != null) {
            loadHost(hostId)
            loadBookings(hostId)
        }
    }

    private fun initialiseViews() {
        txtName = findViewById(R.id.txtName)
        txtArea = findViewById(R.id.txtArea)
        txtBathroom = findViewById(R.id.txtBathroom)
        txtAddress = findViewById(R.id.txtAddress)
        txtPhone = findViewById(R.id.txtPhone)
        txtEmail = findViewById(R.id.txtEmail)
        txtBookings = findViewById(R.id.txtBookings)

        btnReturnDashboard = findViewById(R.id.btnReturnDashboard)
        btnAddBooking = findViewById(R.id.btnAddBooking)
        btnCallHost = findViewById(R.id.btnCallHost)
        btnEmailHost = findViewById(R.id.btnEmailHost)
    }

    private fun setupListeners() {
        btnReturnDashboard.setOnClickListener {
            startActivity(Intent(this, Dashboard::class.java))
            finish()
        }

        btnCallHost.setOnClickListener {
            if (currentHostPhone.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$currentHostPhone"))
                startActivity(intent)
            } else {
                Toast.makeText(this, "No contact number available", Toast.LENGTH_SHORT).show()
            }
        }

        btnEmailHost.setOnClickListener {
            if (currentHostEmail.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:$currentHostEmail")
                    putExtra(Intent.EXTRA_SUBJECT, "SBA Accommodation Inquiry")
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "No email address available", Toast.LENGTH_SHORT).show()
            }
        }

        btnAddBooking.setOnClickListener {
            Toast.makeText(this, "Opening booking registration...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadHost(hostId: String) {
        db.collection("Host Families")
            .document(hostId)
            .get()
            .addOnSuccessListener { document ->
                val host = document.toObject(HostData::class.java)
                host?.let {
                    val fullName = "${it.Host_Name ?: ""} ${it.Host_Surname ?: ""}".trim()
                    txtName.text = if (fullName.isNotEmpty()) fullName else "Host Family Profile"
                    txtArea.text = "Area: ${it.Host_Area ?: "N/A"}"
                    txtBathroom.text = "Bathroom: ${it.Host_Bathroom ?: "N/A"}"
                    txtAddress.text = "Address: ${it.Host_Address ?: "N/A"}"

                    currentHostPhone = it.Host_PhoneNumber ?: ""
                    txtPhone.text = "Phone: ${if (currentHostPhone.isNotEmpty()) currentHostPhone else "N/A"}"

                    currentHostEmail = it.Host_Email ?: ""
                    txtEmail.text = "Email: ${if (currentHostEmail.isNotEmpty()) currentHostEmail else "N/A"}"
                }
            }
    }

    private fun loadBookings(hostId: String) {
        db.collection("Bookings")
            .whereEqualTo("Host_ID", hostId)
            .get()
            .addOnSuccessListener { result ->
                var bookingText = ""
                for (document in result) {
                    val booking = document.toObject(BookingData::class.java)
                    val startDate = booking.StartDate?.toDate()
                    val finishDate = booking.FinishDate?.toDate()

                    val formattedStart = startDate?.let { formatter.format(it) } ?: "N/A"
                    val formattedFinish = finishDate?.let { formatter.format(it) } ?: "N/A"

                    bookingText += "Student ID: ${booking.Student_ID}\nStart: $formattedStart | Finish: $formattedFinish\n\n"
                }

                if (bookingText.isEmpty()) {
                    bookingText = "No active bookings found for this host."
                }

                txtBookings.text = bookingText.trim()
            }
    }
}