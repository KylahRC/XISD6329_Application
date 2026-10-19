package com.example.sba_application

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Provides host searching, bathroom filtering and display using RecyclerView.
 * Extends AppCompatActivity to support dynamic theme switching (Light/Dark Mode).
 */
class MainActivity : AppCompatActivity() {

    //region Variables

    // Firebase Authentication
    var auth: FirebaseAuth? = null
    var user: FirebaseUser? = null

    // UI Elements
    var buttonLogout: Button? = null
    var textViewUser: TextView? = null
    var textViewList: TextView? = null

    // RecyclerView for displaying host list
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CustomAdapter

    // Firebase Firestore Database Instance
    private val db = FirebaseFirestore.getInstance()

    //endregion

    //region Activity Lifecycle

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialiseViews()
        checkLoggedInUser()
        setupButtons()

        // Load host family records from Firestore
        displayRecords()
    }

    //endregion

    //region Initialisation

    /**
     * Binds the  view objects to layut widgets and establishes Firebase instances.
     */
    private fun initialiseViews() {
        recyclerView = findViewById(R.id.my_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        buttonLogout = findViewById(R.id.logout)
        textViewUser = findViewById(R.id.user_details)
        textViewList = findViewById(R.id.hosts)

        auth = FirebaseAuth.getInstance()
        user = auth?.currentUser
    }

    //endregion

    //region Authentication and Session Management

    /**
     * Confirms active authentication state and redirects to login screen if null.
     */
    private fun checkLoggedInUser() {
        if (user == null) {
            startActivity(Intent(applicationContext, Login::class.java))
            finish()
        } else {
            textViewUser?.text = "Logged in: ${user?.email}"
        }
    }

    /**
     * Signs current user out of Firebase and returns to Login screen.
     */
    private fun logout() {
        auth?.signOut()
        startActivity(Intent(applicationContext, Login::class.java))
        finish()
    }

    //endregion

    //region Button & Theme Listener Setup

    /**
     * Sets up click listeners for filter actions, navigation and theme mode toggle.
     */
    private fun setupButtons() {
        val spinnerArea = findViewById<Spinner>(R.id.spinnerArea)
        val spinnerBathroom = findViewById<Spinner>(R.id.spinnerBathroom)
        val buttonFilter = findViewById<Button>(R.id.buttonFilter)
        val returnBtn = findViewById<Button>(R.id.ReturnBtn)

        buttonLogout?.setOnClickListener {
            logout()
        }

        returnBtn.setOnClickListener {
            startActivity(Intent(this, Dashboard::class.java))
        }

        buttonFilter.setOnClickListener {
            val selectedArea = spinnerArea.selectedItem.toString()
            val selectedBathroom = spinnerBathroom.selectedItem.toString()
            applyFilters(selectedArea, selectedBathroom)
        }
    }

    /**
     * Switches the app between Light Mode and Dark Mode programmatically(one of the innovative features metioned in the project plan)
     */
    fun switchThemeMode(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    //endregion

    //region Filtering Logic

    private fun applyFilters(area: String, bathroom: String) {
        if (area == "All" && bathroom == "All") {
            displayRecords()
        } else if (area != "All" && bathroom == "All") {
            filterArea(area)
        } else if (area == "All" && bathroom != "All") {
            filterBathroom(bathroom)
        } else {
            filterBoth(area, bathroom)
        }
    }

    //endregion

    //region RecyclerView Mapping

    private fun displayHosts(result: com.google.firebase.firestore.QuerySnapshot) {
        val hostList = mutableListOf<HostData>()
        for (document in result) {
            val host = document.toObject(HostData::class.java)
            host.hostId = document.id
            hostList.add(host)
        }
        adapter = CustomAdapter(hostList)
        recyclerView.adapter = adapter
    }

    //endregion

    //region Firebase Queries

    fun displayRecords() {
        db.collection("Host Families")
            .get()
            .addOnSuccessListener { result -> displayHosts(result) }
            .addOnFailureListener { Log.e("Firebase", it.message.toString()) }
    }

    fun filterArea(area: String) {
        db.collection("Host Families")
            .whereEqualTo("Host_Area", area)
            .get()
            .addOnSuccessListener { result -> displayHosts(result) }
            .addOnFailureListener { Log.e("Firebase", it.message.toString()) }
    }

    fun filterBathroom(bathroom: String) {
        db.collection("Host Families")
            .whereEqualTo("Host_Bathroom", bathroom)
            .get()
            .addOnSuccessListener { result -> displayHosts(result) }
            .addOnFailureListener { Log.e("Firebase", it.message.toString()) }
    }

    fun filterBoth(area: String, bathroom: String) {
        db.collection("Host Families")
            .whereEqualTo("Host_Area", area)
            .whereEqualTo("Host_Bathroom", bathroom)
            .get()
            .addOnSuccessListener { result -> displayHosts(result) }
            .addOnFailureListener { Log.e("Firebase", it.message.toString()) }
    }

    //endregion
}