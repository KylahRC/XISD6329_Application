//region HostData.kt

    //region Packages

        package com.example.sba_application

    //endregion

    //region Host data class

        data class HostData
        (   var hostId: String = "",
            val Host_Name: String = "",
            val Host_Surname: String = "",
            val Host_Address: String = "",
            val Host_Area: String = "",
            val Host_Bathroom: String = "",
            val Host_PhoneNumber: String = "",
            val Host_Email: String = ""
                //Made the same way as last semesters projects, and how we learnt in OPSC.
        )
        {
            fun formatWithId(id: String): String
            {
                return """
                    Host: $id
                    Name: $Host_Name
                    Surname: $Host_Surname
                    Address: $Host_Address
                    Area: $Host_Area
                    Bathroom: $Host_Bathroom
                    Phone: $Host_PhoneNumber
                    Phone: $Host_Email
                    
                    
                """.trimIndent()
            }
        }

    //endregion

//endregion