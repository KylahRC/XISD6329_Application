package com.example.sba_application

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class DetailedHost : ComponentActivity()
{

    //region Variables

    // Firebase
    private val db = FirebaseFirestore.getInstance()

    // Host Information Views
    private lateinit var txtName: TextView
    private lateinit var txtArea: TextView
    private lateinit var txtBathroom: TextView
    private lateinit var txtAddress: TextView
    private lateinit var txtPhone: TextView
    private lateinit var txtEmail: TextView

    // Booking Information Views
    private lateinit var txtBookings: TextView

    // Date Formatting
    private val formatter =
        SimpleDateFormat(
            "dd MMM yyyy",
            Locale.getDefault()
        )

    //endregion

    //region Activity Lifecycle

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host_details)

        initialiseViews()

        val hostId = intent.getStringExtra("HOST_ID")

        if (hostId != null)
        {

            loadHost(hostId)

            loadBookings(hostId)
        }
    }

    //endregion

    //region Initialisation

    private fun initialiseViews()
    {

        txtName =
            findViewById(R.id.txtName)

        txtArea =
            findViewById(R.id.txtArea)

        txtBathroom =
            findViewById(R.id.txtBathroom)

        txtAddress =
            findViewById(R.id.txtAddress)

        txtPhone =
            findViewById(R.id.txtPhone)

        txtEmail =
            findViewById(R.id.txtEmail)

        txtBookings =
            findViewById(R.id.txtBookings)
    }

    //endregion

    //region Host Data

    private fun loadHost(hostId: String)
    {

        db.collection("Host Families")
            .document(hostId)
            .get()
            .addOnSuccessListener { document ->

                val host =
                    document.toObject(
                        HostData::class.java
                    )

                host?.let {

                    txtName.text =
                        "${it.Host_Name} ${it.Host_Surname}"

                    txtArea.text =
                        it.Host_Area

                    txtBathroom.text =
                        it.Host_Bathroom

                    txtAddress.text =
                        it.Host_Address

                    txtPhone.text =
                        it.Host_PhoneNumber

                    txtEmail.text =
                        it.Host_Email
                }
            }
    }

    //endregion

    //region Booking Data

    private fun loadBookings(hostId: String)
    {

        db.collection("Bookings")
            .whereEqualTo(
                "Host_ID",
                hostId
            )
            .get()
            .addOnSuccessListener { result ->

                var bookingText = ""

                for (document in result)
                {

                    val booking =
                        document.toObject(
                            BookingData::class.java
                        )

                    val startDate =
                        booking.StartDate?.toDate()

                    val finishDate =
                        booking.FinishDate?.toDate()

                    val formattedStart =
                        startDate?.let {
                            formatter.format(it)
                        }

                    val formattedFinish =
                        finishDate?.let {
                            formatter.format(it)
                        }

                    bookingText +=
                        "Student: ${booking.Student_ID}\n" +
                                "Start: $formattedStart\n" +
                                "Finish: $formattedFinish\n\n"
                }

                if (bookingText.isEmpty())
                {

                    bookingText =
                        "No bookings found"
                }

                txtBookings.text =
                    bookingText
            }
    }

    //endregion

}