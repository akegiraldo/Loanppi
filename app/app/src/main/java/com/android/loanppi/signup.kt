package com.android.loanppi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import android.widget.Toast
import androidx.core.view.isVisible
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import java.util.*


class signup : AppCompatActivity() {

    // Variables for signup with google
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var btn_google: Button
    lateinit var gso: GoogleSignInOptions
    var RC_SIGN_IN: Int = 1

    // Variables for signup with Facebook
    private lateinit var callbackManager: CallbackManager
    private lateinit var btn_facebook: Button

    // Variables that handles type of signup method and user type
    private lateinit var btn_worker: Button
    private lateinit var btn_investor: Button
    private var accessWith: String = ""
    private var accessFrom: String = ""
    private var userType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        btn_google = findViewById(R.id.btn_s_google)
        btn_facebook = findViewById<LoginButton>(R.id.btn_s_facebook)
        btn_worker = findViewById<View>(R.id.btn_worker) as Button
        btn_investor = findViewById<View>(R.id.btn_investor) as Button

        btn_worker.setOnClickListener { toggleUserTypeButtons(btn_worker) }
        btn_investor.setOnClickListener { toggleUserTypeButtons(btn_investor) }

        // Signup with Google
        btn_google.setOnClickListener {
            accessWith = "google"

            gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            val signInIntent: Intent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        // Signup with Facebook
        btn_facebook.setOnClickListener(View.OnClickListener {
            accessWith = "facebook"

            callbackManager = CallbackManager.Factory.create()

            LoginManager.getInstance()
                .logInWithReadPermissions(this, Arrays.asList("email, public_profile"))
            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult?) {
                        getFacebokAccount()
                    }
                    override fun onCancel() { Log.d("Acción cancelada", "Login") }
                    override fun onError(error: FacebookException?) {
                        Log.d("Error en el login", error.toString())
                    }
                }
            )
        })
    }

    // Toggle buttons type
    private fun toggleUserTypeButtons(btn_clicked: Button) {
        val btn_to_deselect: Button

        findViewById<TextView>(R.id.txt_register_with).isVisible = true
        if (btn_clicked == btn_worker) {
            btn_to_deselect = btn_investor
            userType = "worker"
        } else {
            btn_to_deselect = btn_worker
            userType = "investor"
        }

        btn_facebook.isVisible = true
        btn_google.isVisible = true

        btn_to_deselect.setBackgroundResource(R.drawable.btn_background_white)
        btn_to_deselect.setTextColor(getColor(R.color.textPrimary))
        btn_clicked.setBackgroundResource(R.drawable.btn_background_orange)
        btn_clicked.setTextColor(getColor(R.color.white))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (accessWith == "google" && requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            getGoogleAccount(task)
        } else if (accessWith == "facebook") {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        } else {
            Toast.makeText(this, "Error al ejecutar la orden :(", Toast.LENGTH_SHORT).show()
        }
    }

    // Signup with Google
    private fun getGoogleAccount (completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                if (userType == "worker") {
                    checkStatusG(account)
                } else {
                    updateUIG(account)
                }
            }
        } catch (e: ApiException) {
            Toast.makeText(this, "Error: no pudimos obtener los datos del usuario.",
                Toast.LENGTH_SHORT).show()
        }
    }

    // Check if user is or not a rappitendero
    private fun checkStatusG(account: GoogleSignInAccount){
        val url = "http://microservices.dev.rappi.com/api/rt-auth-helper/user/type?email=" + account.email
        val queue = Volley.newRequestQueue(this)

        // Request a JSON response from the provided URL.
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
            if (response.get("user_type") != "none") {
                updateUIG(account)
            } else {
                mGoogleSignInClient.signOut()
                Toast.makeText(this, "El usuario no se encuentra asociado a Rappi",
                    Toast.LENGTH_LONG).show()
            }
        },
        // In case there is an error in the connection with Rappi server, the session with Google
        // is closed and the user is shown the error
        Response.ErrorListener {
            mGoogleSignInClient.signOut()
            Toast.makeText(this, "Error en la conexión con el servidor de Rappi.",
                Toast.LENGTH_SHORT).show()
        })

        // Add the request to the RequestQueue.
        queue.add(request)
    }

    private fun updateUIG (googleAccount: GoogleSignInAccount) {
        val intent = Intent(this, dashboard::class.java)
        val accessInfo = Bundle()
        accessInfo.putString("userType", userType)
        accessInfo.putString("accessWith", "google")
        accessInfo.putString("accessFrom", "signup")
        accessInfo.putParcelable("googleAccount", googleAccount)
        accessInfo.putParcelable("gso", gso)
        intent.putExtra("accessInfo", accessInfo)
        finish()
        startActivity(intent)

    }

    // Signup with Facebook
    private fun getFacebokAccount () {
        if (AccessToken.getCurrentAccessToken() != null) {
            val request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken()) { `object`, response ->
                try {
                    val emailAddress = `object`.getString("email")
                    val fullName = `object`.getString("name")
                    val facebookId = `object`.getString("id")
                    val urlUserPhoto = "https://graph.facebook.com/" + facebookId + "/picture?type=normal"

                    val account = Bundle()
                    account.putString("emailAddress", emailAddress)
                    account.putString("fullName", fullName)
                    account.putString("urlUserPhoto", urlUserPhoto)

                    if (userType == "worker") {
                        checkStatusF(account)
                    } else {
                        updateUIF(account)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            val parameters = Bundle()
            parameters.putString("fields", "name,email,id")
            request.parameters = parameters
            request.executeAsync()
        }
    }

    // Check if user is or not a rappitendero
    private fun checkStatusF(account: Bundle){
        val email = account.get("emailAddress")
        val url = "http://microservices.dev.rappi.com/api/rt-auth-helper/user/type?email=" + email
        val queue = Volley.newRequestQueue(this)

        // Request a JSON response from the provided URL.
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                if (response.get("user_type") != "none") {
                    updateUIF(account)
                } else {
                    GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/",
                        null, HttpMethod.DELETE, GraphRequest.Callback {
                            AccessToken.setCurrentAccessToken(null)
                            LoginManager.getInstance().logOut()
                        }).executeAsync()
                    Toast.makeText(this, "El usuario no se encuentra asociado a Rappi",
                        Toast.LENGTH_LONG).show()
                }
            },
            // In case there is an error in the connection with Rappi server, the session with Google
            // is closed and the user is shown the error
            Response.ErrorListener {
                GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/",
                    null, HttpMethod.DELETE, GraphRequest.Callback {
                        AccessToken.setCurrentAccessToken(null)
                        LoginManager.getInstance().logOut()
                    }).executeAsync()
                Toast.makeText(this, "Error en la conexión con el servidor de Rappi.",
                    Toast.LENGTH_SHORT).show()
            })

        // Add the request to the RequestQueue.
        queue.add(request)
    }

    private fun updateUIF (facebookAccount: Bundle) {
        val intent = Intent(this, dashboard::class.java)
        val accessInfo = Bundle()
        accessInfo.putString("userType", userType)
        accessInfo.putString("accessWith", "facebook")
        accessInfo.putString("accessFrom", "signup")
        accessInfo.putBundle("facebookAccount", facebookAccount)
        intent.putExtra("accessInfo", accessInfo)
        finish()
        startActivity(intent)
    }

    // Back to landing
    fun onBack(view: View) {
        finish()
    }
}
