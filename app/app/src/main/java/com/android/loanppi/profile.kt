package com.android.loanppi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 * Use the [profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class profile(bundle: Bundle?) : Fragment() {
    private val loginInfo : Bundle? = bundle
    private lateinit var account: GoogleSignInAccount

    // Profile fields
    private lateinit var editFirstName: EditText
    private lateinit var editSecondName: EditText
    private lateinit var editFirstLastName: EditText
    private lateinit var editSecondLastName: EditText
    private lateinit var editEmailAdress: EditText
    private lateinit var imgUserPhoto: ImageView

    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        editFirstName = view.findViewById(R.id.edit_first_name)
        editSecondName = view.findViewById(R.id.edit_second_name)
        editFirstLastName = view.findViewById(R.id.edit_first_last_name)
        editSecondLastName = view.findViewById(R.id.edit_second_last_name)
        editEmailAdress = view.findViewById(R.id.edit_email_adress)
        imgUserPhoto = view.findViewById(R.id.img_user_photo)
        btnSave = view.findViewById(R.id.btn_save)

        if (loginInfo?.get("loginMethod") == "google") {
            loadGoogleInfo()
        } else {
            loadFacebookInfo()
        }

        btnSave.setOnClickListener(View.OnClickListener {
            if (editFirstName.text.toString() == "") {
                Toast.makeText(context, "El campo nombre no puede estar vacío.", Toast.LENGTH_LONG)
                    .show()
                editFirstName.requestFocus()
            }

            sendPost()
        })

        return view
    }

    fun sendPost() {
        val url = "http://loanppi.kevingiraldo.tech/app/api/v1/prueba/"
        val user = JSONObject()

        user.put("firstName", editFirstName.text.toString())
        user.put("secondName", editSecondName.text.toString())
        user.put("firstLastName", editFirstLastName.text.toString())
        user.put("secondLastName", editSecondLastName.text.toString())

        val queue = Volley.newRequestQueue(context)
        val request = JsonObjectRequest(Request.Method.POST,url,user,
            Response.Listener {
                    response ->
                println("Request succesfull")
            }, Response.ErrorListener { error: VolleyError ->
                println("Error $error.message")
            }
        )
        queue.add(request)

        /*val queue = Volley.newRequestQueue(context)

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url_,
            Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.
                editSecondLastName.setText("Response is: ${response.substring(0, 10)}")
            },
            Response.ErrorListener { editSecondLastName.setText("That didn't work!") })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)*/
    }

    fun loadGoogleInfo() {
        account = loginInfo?.get("account") as GoogleSignInAccount
        val email = account.email
        val names = account.givenName?.split(" ")
        val surnames = account.familyName?.split(" ")

        val firstName = names?.get(0)
        var secondName = ""
        if (names?.size == 2) { secondName = names.get(1) }

        val firstLastName = surnames?.get(0)
        var secondLastName = ""
        if (surnames?.size == 2) { secondLastName = surnames.get(1) }

        editFirstName.setText(firstName)
        editSecondName.setText(secondName)
        editFirstLastName.setText(firstLastName)
        editSecondLastName.setText(secondLastName)
        editEmailAdress.setText(email)
    }

    fun loadFacebookInfo(){
        if (AccessToken.getCurrentAccessToken() != null) {
            val request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken()) { `object`, response ->
                try {
                    val email = `object`.getString("email")
                    val fullName = `object`.getString("name").split(" ")
                    val id = `object`.getString("id")
                    val urlUserPhoto = "https://graph.facebook.com/" + id + "/picture?type=normal"

                    editFirstName.setText(fullName[0])
                    editFirstLastName.setText(fullName[1])
                    editEmailAdress.setText(email)

                    Glide.with(this).load(urlUserPhoto).into(imgUserPhoto)

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


}
