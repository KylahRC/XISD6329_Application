package com.example.sba_application

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity()
{

    //region Variables

    // Firebase Authentication
    var auth: FirebaseAuth? = null
    var user: FirebaseUser? = null

    // UI Elements
    var button: Button? = null
    var textViewUser: TextView? = null
    var textViewList: TextView? = null

    // RecyclerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CustomAdapter

    // Firebase Database
    private val db = FirebaseFirestore.getInstance()

    //endregion

    //region Activity Lifecycle

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialiseViews()
        checkLoggedInUser()
        setupButtons()

        // Load all hosts when screen opens
        displayRecords()
    }

    //endregion

    //region Initialisation

    private fun initialiseViews()
    {

        recyclerView = findViewById(R.id.my_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        button = findViewById(R.id.logout)
        textViewUser = findViewById(R.id.user_details)
        textViewList = findViewById(R.id.hosts)
        auth = FirebaseAuth.getInstance()
        user = auth?.currentUser
    }

    //endregion

    //region Authentication

    private fun checkLoggedInUser()
    {

        if (user == null)
        {

            startActivity(
                Intent(
                    applicationContext,
                    Login::class.java
                )
            )

            finish()
        }
        else
        {
            textViewUser?.text =
                user?.email
        }
    }

    private fun logout()
    {

        FirebaseAuth.getInstance().signOut()

        startActivity(
            Intent(
                applicationContext,
                Login::class.java
            )
        )

        finish()
    }

    //endregion

    //region Button Setup

    private fun setupButtons()
    {

        val spinnerArea = findViewById<Spinner>(R.id.spinnerArea)

        val spinnerBathroom = findViewById<Spinner>(R.id.spinnerBathroom)

        val buttonFilter = findViewById<Button>(R.id.buttonFilter)

        val returnBtn = findViewById<Button>(R.id.ReturnBtn)

        button?.setOnClickListener {
            logout()
        }

        returnBtn.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    Dashboard::class.java
                )
            )
        }

        buttonFilter.setOnClickListener {

            val selectedArea =
                spinnerArea.selectedItem.toString()

            val selectedBathroom =
                spinnerBathroom.selectedItem.toString()

            applyFilters(
                selectedArea,
                selectedBathroom
            )
        }
    }

    //endregion

    //region Filtering Logic

    private fun applyFilters(area: String,bathroom: String)
    {

        if (area == "All" && bathroom == "All")
        {

            displayRecords()
        }
        else if (area != "All" && bathroom == "All")
        {

            filterArea(area)
        }
        else if (area == "All" && bathroom != "All")
        {

            filterBathroom(bathroom)
        }
        else
        {

            filterBoth(area,bathroom)
        }
    }

    //endregion

    //region RecyclerView

    private fun displayHosts(result: com.google.firebase.firestore.QuerySnapshot)
    {

        val hostList =
            mutableListOf<HostData>()

        for (document in result)
        {

            val host = document.toObject(HostData::class.java)

            host.hostId = document.id

            hostList.add(host)
        }

        adapter = CustomAdapter(hostList)

        recyclerView.adapter = adapter
    }

    //endregion

    //region Firebase Queries

    fun displayRecords()
    {

        db.collection("Host Families")
            .get()
            .addOnSuccessListener {
                    result ->
                displayHosts(result)
            }
            .addOnFailureListener {
                Log.e(
                    "Firebase",
                    it.message.toString()
                )
            }
    }

    fun filterArea(area: String)
    {

        db.collection("Host Families")
            .whereEqualTo(
                "Host_Area",
                area
            )
            .get()
            .addOnSuccessListener {
                    result ->
                displayHosts(result)
            }
            .addOnFailureListener {
                Log.e(
                    "Firebase",
                    it.message.toString()
                )
            }
    }

    fun filterBathroom(bathroom: String)
    {

        db.collection("Host Families")
            .whereEqualTo(
                "Host_Bathroom",
                bathroom
            )
            .get()
            .addOnSuccessListener {
                    result ->
                displayHosts(result)
            }
            .addOnFailureListener {
                Log.e(
                    "Firebase",
                    it.message.toString()
                )
            }
    }

    fun filterBoth(area: String,bathroom: String)
    {

        db.collection("Host Families")
            .whereEqualTo(
                "Host_Area",
                area
            )
            .whereEqualTo(
                "Host_Bathroom",
                bathroom
            )
            .get()
            .addOnSuccessListener {
                    result ->
                displayHosts(result)
            }
            .addOnFailureListener {
                Log.e(
                    "Firebase",
                    it.message.toString()
                )
            }
    }

    //endregion
}
