package com.example.sba_application

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class AddStudent: ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_student)

        val returnBtn = findViewById<Button>(R.id.ReturnBtn)

        returnBtn.setOnClickListener {

            val intent = Intent(
                this,
                Dashboard::class.java
            )

            startActivity(intent)
        }
    }
}