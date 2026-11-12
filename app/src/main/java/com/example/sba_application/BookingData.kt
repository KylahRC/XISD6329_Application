package com.example.sba_application

import com.google.firebase.Timestamp

data class BookingData(

    var bookingId: String = "",

    val Student_ID: String = "",

    val Host_ID: String = "",

    val StartDate: Timestamp? = null,

    val FinishDate: Timestamp? = null

)