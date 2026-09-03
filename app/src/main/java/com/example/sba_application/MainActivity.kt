package com.example.sba_application

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    var auth: FirebaseAuth? = null
    var button: Button? = null
    var textViewUser: TextView? = null
    var textViewList: TextView? = null
    var user: FirebaseUser? = null

    private lateinit var recyclerView: RecyclerView

    private lateinit var adapter: CustomAdapter

    val db = FirebaseFirestore.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // taken from https://developer.android.com/develop/ui/views/layout/recyclerview#kotlin
        recyclerView = findViewById(R.id.my_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        auth = FirebaseAuth.getInstance()
        button = findViewById<Button>(R.id.logout)
        textViewUser = findViewById<TextView>(R.id.user_details)
        textViewList = findViewById<TextView>(R.id.hosts)
        user = auth!!.getCurrentUser()

        if (user == null) {
            val intent = Intent(getApplicationContext(), Login::class.java)
            startActivity(intent)
            finish()
        } else {
            textViewUser!!.setText(user!!.getEmail())
        }
        button!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(getApplicationContext(), Login::class.java)
                startActivity(intent)
                finish()
            }
        })

//        sourced from https://medium.com/@deveshsharma7618/firebase-crud-operations-in-kotlin-for-android-cef1f74386d9#:~:text=db.collection(%22users%22)%0A%20%20%20%20.get,%22Firebase%22%2C%20it.message.toString())%0A%20%20%20%20%20%20%20%20%7D%0A%7D
        db.collection("Host Families")
            .get()
            .addOnSuccessListener { result ->
                val hostList = mutableListOf<HostData>()
                for (document in result) {
                    val host = document.toObject(HostData::class.java)
                    hostList.add(host)

                }
                Log.d("Firebase", hostList.toString())
//                    this was jerryrigged from the log in code, but hey it kinda works??
                val list = hostList.toString().toString()
                textViewList!!.setText(list)
                // borrowed from https://developer.android.com/develop/ui/views/layout/recyclerview#kotlin
                val dataset = arrayOf(hostList.toString().toString())
                adapter = CustomAdapter(dataset)
                recyclerView.adapter = adapter

            }
            .addOnFailureListener {
                Log.e("Firebase", it.message.toString())
            }


    }
    }
