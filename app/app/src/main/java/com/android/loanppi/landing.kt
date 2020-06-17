package com.android.loanppi

import android.Manifest
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import org.json.JSONObject
import java.util.*


class landing : AppCompatActivity() {

    // Variables for signup with google
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var gso: GoogleSignInOptions
    private lateinit var btn_google: Button
    var RC_SIGN_IN: Int = 1

    // Variables for signup with Facebook
    private lateinit var callbackManager: CallbackManager
    private lateinit var btn_facebook: Button

    // Variables for signup with SoyRappi
    private lateinit var btn_rappi: Button

    private var accessWith: String = ""
    private val INTERNET_REQUEST_CODE = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkFacebookLogin()

        setContentView(R.layout.activity_landing)

        checkInternetPermission()
    }

    private fun checkInternetPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
            != PackageManager.PERMISSION_GRANTED) {
            //El permiso no está aceptado.
            requestInternetPermission()
        } else {
            //El permiso está aceptado.
            Toast.makeText(this, "Permiso aceptado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestInternetPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.INTERNET)) {
            //El usuario ya ha rechazado el permiso anteriormente, debemos informarle que vaya a ajustes.
            Toast.makeText(this, "Manual", Toast.LENGTH_SHORT).show()
        } else {
            //El usuario nunca ha aceptado ni rechazado, así que le pedimos que acepte el permiso.
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.INTERNET),
                INTERNET_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            INTERNET_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //El usuario ha aceptado el permiso, no tiene porqué darle de nuevo al botón, podemos lanzar la funcionalidad desde aquí.
                } else {
                    //El usuario ha rechazado el permiso, podemos desactivar la funcionalidad o mostrar una vista/diálogo.
                }
                return
            }
            else -> {
                // Este else lo dejamos por si sale un permiso que no teníamos controlado.
            }
        }
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

        builder.setView(dialogLayout).setNegativeButton(R.string.cancel) { dialog, which -> }
        builder.show()

        // Login with Google
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

        // Login with Facebook
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (accessWith == "google" && requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            getGoogleAccount(task)
        } else if (accessWith == "facebook") {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        } else {
            Toast.makeText(this, "Error al ejecutar la orden :(", Toast.LENGTH_LONG).show()
        }
    }

    // Login with Google
    private fun getGoogleAccount (completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                getUser(account, null)
            }
        } catch (e: ApiException) {
            Toast.makeText(this, "Error: " + e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun updateUIG (googleAccount: GoogleSignInAccount, account: Bundle) {
        val intent = Intent(this, dashboard::class.java)
        val accessInfo = Bundle()
        accessInfo.putString("accessWith", "google")
        accessInfo.putString("accessFrom", "login")
        accessInfo.putParcelable("googleAccount", googleAccount)
        accessInfo.putParcelable("gso", gso)
        intent.putExtra("accessInfo", accessInfo)
        intent.putExtra("account", account)
        startActivity(intent)
    }

    // Login with Facebook
    private fun getFacebokAccount () {
        if (AccessToken.getCurrentAccessToken() != null) {
            val request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken()) { `object`, response ->
                try {
                    val emailAddress = `object`.getString("email")
                    val facebookId = `object`.getString("id")
                    val urlUserPhoto = "https://graph.facebook.com/" + facebookId + "/picture?type=normal"

                    val account = Bundle()
                    account.putString("emailAddress", emailAddress)
                    account.putString("urlUserPhoto", urlUserPhoto)

                    getUser(null, account)

                    if (!`object`.has("id")) {
                        Log.d("FBLOGIN_FAILED", `object`.toString())
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

    private fun updateUIF (facebookAccount: Bundle, account: Bundle) {
        val intent = Intent(this, dashboard::class.java)
        val accessInfo = Bundle()
        accessInfo.putString("accessWith", "facebook")
        accessInfo.putString("accessFrom", "login")
        accessInfo.putBundle("facebookAccount", facebookAccount)
        intent.putExtra("accessInfo", accessInfo)
        intent.putExtra("account", account)
        startActivity(intent)
    }

    // Function that check if exists a current session with facebook
    fun checkFacebookLogin() {
        if (AccessToken.getCurrentAccessToken() != null) {
            accessWith = "facebook"
            getFacebokAccount()
        } else {
            println("El token está nulo")
        }
    }

    // Get user from database
    private fun getUser(googleAccount: GoogleSignInAccount?, facebookAccount: Bundle?) {
        var email = ""
        if (accessWith == "google") {
            email = googleAccount?.email.toString()
        } else {
            email = facebookAccount?.get("emailAddress").toString()
        }
        val url = "http://loanppi.kevingiraldo.tech/app/api/v1/user?email=" + email
        val queue = Volley.newRequestQueue(this)

        // Request a JSON response from the provided URL.
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                Toast.makeText(this, response.get("status").toString(), Toast.LENGTH_LONG).show()
                if (response.get("status") == "exists") {
                    val account = Bundle()
                    account.putString("firstName", response.get("firstName").toString())
                    account.putString("secondName", response.get("secondName").toString())
                    account.putString("firstLastName", response.get("firstLastName").toString())
                    account.putString("secondLastName", response.get("secondLastName").toString())
                    account.putString("idDocument", response.get("idDocument").toString())
                    account.putString("phoneNumber", response.get("phoneNumber").toString())
                    account.putString("emailAddress", response.get("emailAddress").toString())
                    account.putString("homeAddress", response.get("homeAddress").toString())
                    account.putString("userType", response.get("userType").toString())
                    if (response.get("userType").toString() == "investors") {
                        account.putString("userId", response.get("idInvestor").toString())
                        account.putString("investStack", response.get("investStack").toString())
                    } else {
                        account.putString("userId", response.get("idWorker").toString())

                    }
                    if (accessWith == "google")
                        googleAccount?.let { updateUIG(it, account) }
                    else
                        facebookAccount?.let { updateUIF(it, account) }
                } else {
                    if (accessWith == "google") {
                        mGoogleSignInClient.signOut()
                    } else {
                        GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/",
                            null, HttpMethod.DELETE, GraphRequest.Callback {
                                AccessToken.setCurrentAccessToken(null)
                                LoginManager.getInstance().logOut()
                            }).executeAsync()
                    }
                    Toast.makeText(this, "El usuario no existe. Regístrate por favor.",
                        Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener {
                if (accessWith == "google") {
                    mGoogleSignInClient.signOut()
                } else {
                    GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/",
                        null, HttpMethod.DELETE, GraphRequest.Callback {
                            AccessToken.setCurrentAccessToken(null)
                            LoginManager.getInstance().logOut()
                        }).executeAsync()
                }
                Toast.makeText(this, "Error al intentar iniciar sesión", Toast.LENGTH_LONG).show()
            })

        // Add the request to the RequestQueue.
        queue.add(request)
    }
}

