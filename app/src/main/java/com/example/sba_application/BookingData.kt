package com.example.sba_application

import com.google.firebase.Timestamp

data class BookingData(

    val Host_ID: String = "",
    val Student_ID: String = "",
    val StartDate: Timestamp? = null,
    val FinishDate: Timestamp? = null

)