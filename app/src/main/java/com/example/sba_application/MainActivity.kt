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
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    //Declaring  Firebase Authentication and current user variables.
    var auth: FirebaseAuth? = null
    var button: Button? = null
    var textViewUser: TextView? = null
    var textViewList: TextView? = null
    var user: FirebaseUser? = null

//RecyclerView and adapter references for displaying lists efficiently.
    private lateinit var recyclerView: RecyclerView

    private lateinit var adapter: CustomAdapter
//Instance of Cloud Firebase Database.
    val db = FirebaseFirestore.getInstance()



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Initialising the RecyclerView and setting its layout manager.
        // taken from https://developer.android.com/develop/ui/views/layout/recyclerview#kotlin
        recyclerView = findViewById(R.id.my_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val spinnerArea = findViewById<Spinner>(R.id.spinnerArea)
        val spinnerBathroom = findViewById<Spinner>(R.id.spinnerBathroom)
        val buttonFilter = findViewById<Button>(R.id.buttonFilter)
        auth = FirebaseAuth.getInstance()
        button = findViewById<Button>(R.id.logout)
        textViewUser = findViewById<TextView>(R.id.user_details)
        textViewList = findViewById<TextView>(R.id.hosts)
        user = auth!!.getCurrentUser()

        // Checking if no user is logged in.
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

        buttonFilter.setOnClickListener {
            val selectedArea = spinnerArea.selectedItem.toString()
            val selectedBathroom = spinnerBathroom.selectedItem.toString()

//            was there a better way? perhaps
//            did I have time to find one? no
            // This is a conditional routing (allegedly) based on what filters the user picked.
            if (selectedArea == "All" && selectedBathroom == "All")
            {
                displayRecords()
            }
            else if (selectedArea != "All" && selectedBathroom == "All")
            {
                filterArea(selectedArea)
            }
            else if (selectedArea == "All" && selectedBathroom != "All")
            {
                filterBathroom(selectedBathroom)
            }
            else if (selectedArea != "All" && selectedBathroom != "All")
            {
                filterBoth(selectedArea, selectedBathroom)
            }
        }


//        olddisplay()



    }

    fun olddisplay()
    {
//        sourced from https://medium.com/@deveshsharma7618/firebase-crud-operations-in-kotlin-for-android-cef1f74386d9#:~:text=db.collection(%22users%22)%0A%20%20%20%20.get,%22Firebase%22%2C%20it.message.toString())%0A%20%20%20%20%20%20%20%20%7D%0A%7D
        db.collection("Host Families")
            .get()
            .addOnSuccessListener { result ->
                val hostList = mutableListOf<HostData>()
                //emergency code due to time, source needed!
                val formattedList = mutableListOf<String>()
                for (document in result) {
                    val host = document.toObject(HostData::class.java)
                    hostList.add(host)

                    //emergency code due to time, source needed!
                    val formatted = host.formatWithId(document.id)
                    formattedList.add(formatted)
                }
                Log.d("Firebase", hostList.toString())
//                    this was jerryrigged from the log in code, but hey it kinda works??
                val list = hostList.toString().toString()
//                textViewList!!.setText(list)
//split isnt working :(
//                got from https://www.baeldung.com/kotlin/split-string
                val splitlist = list.split("},")
                // borrowed from https://developer.android.com/develop/ui/views/layout/recyclerview#kotlin
//                val dataset = arrayOf(hostList.toString().toString())
//                val dataset = splitlist.toTypedArray()

                //emergency code due to time, source needed!
                val dataset = formattedList.toTypedArray()
                adapter = CustomAdapter(dataset)
                recyclerView.adapter = adapter



            }
            .addOnFailureListener {
                Log.e("Firebase", it.message.toString())
            }




    }

//Helper function to process the Firebase query and bind it to the RecyclerView adapter.
    fun displayHosts(result: com.google.firebase.firestore.QuerySnapshot)
    {
//        mostly same, taken from old display
//        basicallly just formats the text
        val formattedList = mutableListOf<String>()
        for (document in result) {
            val host = document.toObject(HostData::class.java)
            val formatted = host.formatWithId(document.id)
            formattedList.add(formatted)
        }
        val dataset = formattedList.toTypedArray()
        adapter = CustomAdapter(dataset)
        recyclerView.adapter = adapter
    }

    //    Source to be attached soon
    //    Fetches all the records from host families that do not have any filters attached to them.
    fun displayRecords()
    {
        db.collection("Host Families")
            .get()
            .addOnSuccessListener { result -> displayHosts(result) }
            .addOnFailureListener { Log.e("Firebase", it.message.toString()) }
    }
    // Queries the Firestore filtered strictly by the selected area.
    fun filterArea(area: String)
    {
        db.collection("Host Families")
            .whereEqualTo("Host_Area", area)
            .get()
            .addOnSuccessListener { result -> displayHosts(result) }
            .addOnFailureListener { Log.e("Firebase", it.message.toString()) }
    }
    // Queries the Firestore filtered strictly by the selected Bathroom option.
    fun filterBathroom(bathroom: String)
    {
        db.collection("Host Families")
            .whereEqualTo("Host_Bathroom", bathroom)
            .get()
            .addOnSuccessListener { result -> displayHosts(result) }
            .addOnFailureListener { Log.e("Firebase", it.message.toString()) }
    }

    //  filters strictly by the selected area and bathroom option.
    fun filterBoth(area: String, bathroom: String)
    {
        db.collection("Host Families")
            .whereEqualTo("Host_Area", area)
            .whereEqualTo("Host_Bathroom", bathroom)
            .get()
            .addOnSuccessListener { result -> displayHosts(result) }
            .addOnFailureListener { Log.e("Firebase", it.message.toString()) }
    }




}
