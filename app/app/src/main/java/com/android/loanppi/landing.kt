package com.android.loanppi

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import java.util.*

class landing : AppCompatActivity() {

    // Variables for signup with google
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var btn_google: Button
    lateinit var gso: GoogleSignInOptions
    var RC_SIGN_IN: Int = 1

    // Variables for signup with Facebook
    private lateinit var callbackManager: CallbackManager
    private lateinit var btn_facebook: Button

    // Variables for signup with SoyRappi
    private lateinit var btn_rappi: Button

    private var loginMethod: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        checkFacebookLogin()
    }

    fun onSignUp(view: View) {
        val intent = Intent(this, signup::class.java)
        startActivity(intent)
    }

    @SuppressLint("ResourceType")
    fun onLogin(view: View) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_login, null)

        btn_facebook = dialogLayout.findViewById(R.id.btn_l_facebook)
        btn_google = dialogLayout.findViewById(R.id.btn_l_google)

        builder.setView(dialogLayout).setNegativeButton(R.string.cancel) { dialog, which ->
            Toast.makeText(applicationContext, R.string.cancel, Toast.LENGTH_SHORT).show()
        }
        builder.show()

        // Login with Google
        btn_google.setOnClickListener {
            loginMethod = "google"

            gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            val signInIntent: Intent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        // Login with Facebook
        btn_facebook.setOnClickListener(View.OnClickListener {
            loginMethod = "facebook"

            callbackManager = CallbackManager.Factory.create()

            LoginManager.getInstance()
                .logInWithReadPermissions(this, Arrays.asList("email, public_profile"))
            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult?) {
                        updateUIF(AccessToken.getCurrentAccessToken())
                    }
                    override fun onCancel() { Log.d("Acción cancelada", "Login") }
                    override fun onError(error: FacebookException?) {
                        Log.d("Error en el login", error.toString())
                    }
                }
            )
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (loginMethod == "google" && requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        } else if (loginMethod == "facebook") {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        } else {
            Toast.makeText(this, "Error al ejecutar la orden :(", Toast.LENGTH_LONG).show()
        }
    }

    // Login with Google
    private fun handleResult (completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                /*val user = getUser(account.email.toString())
                if (user != null)) {

                }*/
                updateUIG(account)
            }
        } catch (e: ApiException) {
            Toast.makeText(this, "Error: " + e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun getUser(email: String) {
        val url = "http://loanppi.kevingiraldo.tech/app/api/v1/user?email="

        val queue = Volley.newRequestQueue(this)

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.
                Toast.makeText(this, response.toString().substring(0, 20), Toast.LENGTH_LONG).show()
            },
            Response.ErrorListener {
                Toast.makeText(this, "Error al intentar iniciar sesión", Toast.LENGTH_LONG).show()
            })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    private fun updateUIG (account: GoogleSignInAccount) {
        val intent = Intent(this, dashboard::class.java)
        intent.putExtra("userType", "investor")
        intent.putExtra("signupMethod", "")
        intent.putExtra("loginMethod", "google")
        intent.putExtra("account", account)
        intent.putExtra("gso", gso)
        startActivity(intent)
    }

    // Login with Facebook
    private fun updateUIF (token: AccessToken) {
        val intent = Intent(this, dashboard::class.java)
        intent.putExtra("userType", "investor")
        intent.putExtra("signupMethod", "")
        intent.putExtra("loginMethod", "facebook")
        intent.putExtra("token", token)
        startActivity(intent)
    }

    fun checkFacebookLogin() {
        if (AccessToken.getCurrentAccessToken() != null) {
            updateUIF(AccessToken.getCurrentAccessToken())
        } else {
            println("El token está nulo")
        }
    }
}

