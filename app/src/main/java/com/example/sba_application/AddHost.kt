package com.example.sba_application

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class AddHost : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_host)

        val returnBtn = findViewById<Button>(R.id.ReturnBtn)

        returnBtn.setOnClickListener {

            val intent = Intent(
                this,
                Dashboard::class.java
            )

            startActivity(intent)

            //this will be for adding new host families to the database
            //May need to ask client about what info they usually record again just in case
        }
    }
}