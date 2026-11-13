//region BookingData.kt

    //TODO: write description of what data calss is for and what it do

    //region Packages

        package com.example.sba_application

    //endregion

    //region Imports

        import com.google.firebase.Timestamp

    //endregion

    //region Booking Data Class

        data class BookingData(

            //region Variables

                var bookingId: String = "",

                val Student_ID: String = "",

                val Host_ID: String = "",

                val StartDate: Timestamp? = null,

                val FinishDate: Timestamp? = null

            //endregion

        )

    //endregion

//endregion