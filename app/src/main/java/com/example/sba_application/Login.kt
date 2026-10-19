package com.example.sba_application

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

/**
 * Login Activity
 * Entry point for authentication. Allows registered users to log into SBA.
 * Includes Firebase Authentication listeners, Google Single Sign-On (SSO), and user-selectable theme toggling.
 */
class Login : AppCompatActivity() {

    //region Member Variables
    var editTextEmail: TextInputEditText? = null
    var editTextPassword: TextInputEditText? = null
    var buttonLogin: Button? = null
    var buttonGoogleSignIn: SignInButton? = null
    var mAuth: FirebaseAuth? = null
    var progressBar: ProgressBar? = null

    private lateinit var googleSignInClient: GoogleSignInClient

    // Google Sign-In Launcher Callback for SSO
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            progressBar?.visibility = View.GONE
            Toast.makeText(this, "Google Sign-In Failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
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

        // Configure Google Sign-In options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Bind layout objects from activity_login.xml
        editTextEmail = findViewById(R.id.email)
        editTextPassword = findViewById(R.id.password)
        buttonLogin = findViewById(R.id.btn_login)
        buttonGoogleSignIn = findViewById(R.id.btnGoogleSignIn)
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

        // Set Google Sign-In SSO button click listener
        buttonGoogleSignIn?.setOnClickListener {
            progressBar?.visibility = View.VISIBLE
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }
    }

    /**
     * Authenticates Firebase session using Google Credential ID Token.
     */
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth?.signInWithCredential(credential)
            ?.addOnSuccessListener {
                progressBar?.visibility = View.GONE
                Toast.makeText(this, "Google Authentication Successful!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Dashboard::class.java)
                startActivity(intent)
                finish()
            }
            ?.addOnFailureListener { e ->
                progressBar?.visibility = View.GONE
                Toast.makeText(this, "Google Auth Failed: ${e.message}", Toast.LENGTH_SHORT).show()
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