package com.example.sba_application

data class HostData
    (
        val Host_Name: String = "",
        val Host_Surname: String = "",
        val Host_Address: String = "",
        val Host_Area: String = "",
        val Host_Bathroom: String = "",
        val Host_PhoneNumber: String = ""
    )
//Made the same way as last semesters projects, and how we learnt in OPSC
{
    //emergency code due to time, source needed!
    fun formatWithId(id: String): String {
        return """
            Host: $id
            Name: $Host_Name
            Surname: $Host_Surname
            Address: $Host_Address
            Area: $Host_Area
            Bathroom: $Host_Bathroom
            Phone: $Host_PhoneNumber
            
            
        """.trimIndent()
    }
}