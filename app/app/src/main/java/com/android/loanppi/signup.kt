package com.android.loanppi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import android.widget.Toast
import androidx.core.view.isVisible
import java.security.KeyStore


class signup : AppCompatActivity() {

    // Variables for login with google
    lateinit var gso: GoogleSignInOptions
    lateinit var mGoogleSignInClient: GoogleSignInClient
    var RC_SIGN_IN: Int = 1

    // Variables that handles type of user
    var type: String = ""
    lateinit var btn_worker: Button
    lateinit var btn_investor: Button
    lateinit var btn_google: SignInButton
    lateinit var btn_facebook: Button
    lateinit var btn_rappi: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        btn_google = findViewById<View>(R.id.btn_s_google) as SignInButton
        btn_facebook = findViewById<View>(R.id.btn_s_facebook) as Button
        btn_rappi = findViewById<View>(R.id.btn_s_rappi) as Button
        btn_worker = findViewById<View>(R.id.btn_worker) as Button
        btn_investor = findViewById<View>(R.id.btn_investor) as Button

        btn_worker.setOnClickListener { toggle_btns_type(btn_worker) }
        btn_investor.setOnClickListener { toggle_btns_type(btn_investor) }

        // Login with Google
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btn_google.setOnClickListener {
            if (type == "") {
                Toast.makeText(this, "Por favor seleccione un tipo de usuario", Toast.LENGTH_LONG).show()
            } else {
                signup_goo()
            }
        }

        // Login with Facebook

    }

    // Toggle buttons type
    private fun toggle_btns_type(btn_clicked: Button) {
        val btn_to_deselect: Button

        findViewById<TextView>(R.id.txt_register_with).isVisible = true
        if (btn_clicked == btn_worker) {
            btn_to_deselect = btn_investor
            type = "worker"
            btn_facebook.isVisible = false
            btn_google.isVisible = false
            btn_rappi.isVisible = true
        } else {
            btn_to_deselect = btn_worker
            type = "investor"
            btn_facebook.isVisible = true
            btn_google.isVisible = true
            btn_rappi.isVisible = false
        }

        btn_to_deselect.setBackgroundResource(R.drawable.btn_background_white)
        btn_to_deselect.setTextColor(getColor(R.color.textPrimary))
        btn_clicked.setBackgroundResource(R.drawable.btn_background_orange)
        btn_clicked.setTextColor(getColor(R.color.white))
    }

    // Login with Google
    private fun signup_goo() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        } else {
            Toast.makeText(this, "Error al ejecutar la orden :(", Toast.LENGTH_LONG).show()
        }
    }

    private fun handleResult (completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                updateUI(account)
            }
        } catch (e: ApiException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun updateUI (account: GoogleSignInAccount) {
        val intent = Intent(this, dashboard::class.java)
        intent.putExtra("type", type)
        intent.putExtra("account", account)
        intent.putExtra("gso", gso)
        startActivity(intent)
    }

    // Login with Facebook


    fun onBack(view: View) {
        finish()
    }
}
