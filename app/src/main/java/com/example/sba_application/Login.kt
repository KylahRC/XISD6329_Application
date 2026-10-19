package com.example.sba_application

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

/**
 * Login Activity
 * Entry point for authentication. Allows registered users to log into SBA.
 * Includes Firebase Authentication listeners and user-selectable theme toggling.
 */
class Login : AppCompatActivity() {

    //region Member Variables
    var editTextEmail: TextInputEditText? = null
    var editTextPassword: TextInputEditText? = null
    var buttonLogin: Button? = null
    var mAuth: FirebaseAuth? = null
    var progressBar: ProgressBar? = null
    //endregion

    override fun onStart() {
        super.onStart()
        // Automatically transitions user to Dashboard if active Firebase session exists
        val currentUser = mAuth?.currentUser
        if (currentUser != null) {
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth reference
        mAuth = FirebaseAuth.getInstance()

        // Bind layout objects from activity_login.xml
        editTextEmail = findViewById(R.id.email)
        editTextPassword = findViewById(R.id.password)
        buttonLogin = findViewById(R.id.btn_login)
        progressBar = findViewById(R.id.progressBar)

        // Set login button listener to handle credentials submission
        buttonLogin?.setOnClickListener {
            progressBar?.visibility = View.VISIBLE
            val email = editTextEmail?.text.toString().trim()
            val password = editTextPassword?.text.toString().trim()

            // Input validation checks
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this@Login, "Enter email", Toast.LENGTH_SHORT).show()
                progressBar?.visibility = View.GONE
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this@Login, "Enter Password", Toast.LENGTH_SHORT).show()
                progressBar?.visibility = View.GONE
                return@setOnClickListener
            }

            // Authenticate user via Firebase Auth
            mAuth?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener { task ->
                    progressBar?.visibility = View.GONE
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "Login Successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(applicationContext, Dashboard::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@Login, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    /**
     * Helper method to toggle between Dark and Light mode programmatically.
     * Can be linked to a UI Switch or Button in settings/dashboard.
     */
    fun toggleAppTheme(enableDarkMode: Boolean) {
        if (enableDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}