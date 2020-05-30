package com.android.loanppi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import android.widget.Toast


class signup : AppCompatActivity() {

    // Variables for login with google
    lateinit var gso: GoogleSignInOptions
    lateinit var mGoogleSignInClient: GoogleSignInClient
    var RC_SIGN_IN: Int = 1

    // Variables that manage of the type of user
    var type: String = ""
    lateinit var btn_worker: Button
    lateinit var btn_investor: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val btn_go = findViewById<View>(R.id.btn_s_google) as SignInButton
        val btn_fb = findViewById<View>(R.id.btn_s_facebook) as Button
        btn_worker = findViewById<View>(R.id.btn_worker) as Button
        btn_investor = findViewById<View>(R.id.btn_investor) as Button

        btn_worker.setOnClickListener { toggle_btns_type(btn_worker) }
        btn_investor.setOnClickListener { toggle_btns_type(btn_investor) }

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btn_go.setOnClickListener {
            if (type == "") {
                Toast.makeText(this, "Please select a type of user", Toast.LENGTH_LONG).show()
                findViewById<Button>(R.id.btn_worker).isFocused()
            } else {
                signup_go()
            }
        }
    }

    // Toggle buttons type
    private fun toggle_btns_type(btn_clicked: Button) {
        val btn_to_deselect: Button

        if (btn_clicked == btn_worker) {
            btn_to_deselect = btn_investor
            type = "worker"
        } else {
            btn_to_deselect = btn_worker
            type = "investor"
        }

        btn_to_deselect.setBackgroundResource(R.drawable.btn_background_white)
        btn_to_deselect.setTextColor(getColor(R.color.textPrimary))
        btn_clicked.setBackgroundResource(R.drawable.btn_background_orange)
        btn_clicked.setTextColor(getColor(R.color.white))
    }

    // Login with Google
    private fun signup_go() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        } else {
            Toast.makeText(this, "Problem in execution order :(", Toast.LENGTH_LONG).show()
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
        intent.putExtra("name", account.displayName)
        intent.putExtra("gso", gso)
        startActivity(intent)
    }

    fun onBack(view: View) {
        finish()
    }
}
