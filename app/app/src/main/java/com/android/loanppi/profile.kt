package com.android.loanppi

import android.os.Bundle
import android.text.Layout
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.Constraints
import androidx.core.view.isVisible
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import org.json.JSONObject
import com.android.loanppi.fieldsValidator as fieldsValidator

/**
 * A simple [Fragment] subclass.
 * Use the [profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class profile(bundle: Bundle?) : Fragment() {
    // User account information
    private var accessInfo: Bundle? = Bundle()
    private var bundle: Bundle? = bundle

    // Profile fields
    private lateinit var editFirstName: EditText
    private lateinit var editSecondName: EditText
    private lateinit var editFirstLastName: EditText
    private lateinit var editSecondLastName: EditText
    private lateinit var editEmailAddress: EditText
    private lateinit var imgUserPhoto: ImageView
    private lateinit var editIdDocument: EditText
    private lateinit var editHomeAddress: EditText
    private lateinit var editPhoneNumber: EditText

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

        // Get edittexts from fragment
        editFirstName = view.findViewById(R.id.edit_first_name)
        editSecondName = view.findViewById(R.id.edit_second_name)
        editFirstLastName = view.findViewById(R.id.edit_first_last_name)
        editSecondLastName = view.findViewById(R.id.edit_second_last_name)
        editEmailAddress = view.findViewById(R.id.edit_email_address)
        imgUserPhoto = view.findViewById(R.id.img_user_photo)
        editIdDocument = view.findViewById(R.id.edit_id_document)
        editHomeAddress = view.findViewById(R.id.edit_home_address)
        editPhoneNumber = view.findViewById(R.id.edit_phone_number)

        btnSave = view.findViewById(R.id.btn_save)

        accessInfo = bundle?.getBundle("accessInfo") as Bundle

        // Depending on whether the user is registering or logging in, it calls up the
        // respective functions.
        if (accessInfo?.get("accessFrom") == "signup") {
            if (accessInfo?.get("accessWith") == "google") {
                loadGoogleInfo()
            } else {
                loadFacebookInfo()
            }
        } else {
            loadAccountInfo()
        }

        // Listen when you press the button to create a new user, check the fields and then call
        // the function in charge of registering the new user
        btnSave.setOnClickListener(View.OnClickListener {
            if (validateFields() && accessInfo?.get("accessFrom") == "signup") {
                createUser()
            }
        })

        return view
    }

    // Function that makes the request to the server to register a new user
    fun createUser() {
        val url = "http://loanppi.kevingiraldo.tech/app/api/v1/new_user/"
        val user = JSONObject()

        user.put("firstName", editFirstName.text.toString())
        user.put("secondName", editSecondName.text.toString())
        user.put("firstLastName", editFirstLastName.text.toString())
        user.put("secondLastName", editSecondLastName.text.toString())
        user.put("emailAddress", editEmailAddress.text.toString())
        user.put("idDocument", editIdDocument.text.toString())
        user.put("phoneNumber", editPhoneNumber.text.toString())
        user.put("homeAddress", editHomeAddress.text.toString())
        user.put("userType", accessInfo?.get("userType"))

        // Create the request. In case of success close the current session and takes the
        // user to the login. In case the user exists or cannot be registered, it shows the
        // respective messages.
        val queue = Volley.newRequestQueue(context)
        val request = JsonObjectRequest(Request.Method.POST, url, user,
            Response.Listener { response ->
                if (response.get("status") == "ok") {
                    Toast.makeText(context, "Usuario registrado con éxito", Toast.LENGTH_LONG)
                        .show()
                    if (accessInfo?.get("accessWith") == "google") {
                        val gso = accessInfo?.get("gso") as GoogleSignInOptions
                        val mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
                        mGoogleSignInClient.signOut()
                    } else if (AccessToken.getCurrentAccessToken() != null) {
                        GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/",
                            null, HttpMethod.DELETE, GraphRequest.Callback {
                                AccessToken.setCurrentAccessToken(null)
                                LoginManager.getInstance().logOut()
                            }).executeAsync()
                    }
                    requireActivity().finish()
                } else if (response.get("status") == "exists") {
                    Toast.makeText(context, "El usuario ya existe.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context,"El usuario no pudo ser registrado.",
                        Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error: VolleyError ->
                Toast.makeText(context,"Error en la conexión con el servidor.",
                    Toast.LENGTH_SHORT).show()
                println("Error al guardar el usuario ${error.message}")
            }
        )
        queue.add(request)
    }

    // Gets the user information from the Google account package and adds it to the fields
    // in the fragment
    fun loadGoogleInfo() {
        val account = accessInfo?.get("googleAccount") as GoogleSignInAccount
        val email = account.email
        val names = account.givenName?.split(" ")
        val surnames = account.familyName?.split(" ")
        val urlUserPhoto = account.photoUrl.toString()

        val firstName = names?.get(0)
        var secondName = ""
        if (names?.size == 2) {
            secondName = names.get(1)
        }

        val firstLastName = surnames?.get(0)
        var secondLastName = ""
        if (surnames?.size == 2) {
            secondLastName = surnames.get(1)
        }

        editFirstName.setText(firstName)
        editSecondName.setText(secondName)
        editFirstLastName.setText(firstLastName)
        editSecondLastName.setText(secondLastName)
        editEmailAddress.setText(email)

        Glide.with(this).load(urlUserPhoto).into(imgUserPhoto)
    }

    // Gets the user information from the Facebook account package and adds it to the fields
    // in the fragment
    fun loadFacebookInfo() {
        val account = accessInfo?.get("facebookAccount") as Bundle
        val emailAddress = account.getString("emailAddress")
        val fullName = account.getString("fullName")?.split(" ")
        val urlUserPhoto = account.getString("urlUserPhoto")

        editFirstName.setText(fullName?.get(0))
        editFirstLastName.setText(fullName?.get(1))
        editEmailAddress.setText(emailAddress)
        Glide.with(this).load(urlUserPhoto).into(imgUserPhoto)
    }

    // Gets the user information from the account package and adds it to the fields in the snippet
    fun loadAccountInfo() {
        val account = bundle?.getBundle("account") as Bundle
        editFirstName.setText(account.get("firstName").toString())
        editSecondName.setText(account.get("secondName").toString())
        editFirstLastName.setText(account.get("firstLastName").toString())
        editSecondLastName.setText(account.get("secondLastName").toString())
        editIdDocument.setText(account.get("idDocument").toString())
        editIdDocument.isEnabled = false
        editPhoneNumber.setText(account.get("phoneNumber").toString())
        editEmailAddress.setText(account.get("emailAddress").toString())
        editHomeAddress.setText(account.get("homeAddress").toString())
        var urlPhoto: String? = ""
        if (accessInfo?.get("accessWith") == "facebook") {
            val facebookAccount = accessInfo?.get("facebookAccount") as Bundle
            urlPhoto = facebookAccount.get("urlUserPhoto") as String
        } else {
            val googleAccount = accessInfo?.get("googleAccount") as GoogleSignInAccount
            urlPhoto = googleAccount.photoUrl.toString()
        }
        Glide.with(this).load(urlPhoto).into(imgUserPhoto)
    }

    // Function for validate the fields of profile form
    fun validateFields(): Boolean {
        return (
            fieldsValidator(context, editFirstName, "onlyLetters", 2, 14, true) &&
            fieldsValidator(context, editSecondName, "onlyLetters", 2, 14, false) &&
            fieldsValidator(context, editFirstLastName, "onlyLetters", 2, 14, true) &&
            fieldsValidator(context, editSecondLastName, "onlyLetters", 2, 14, true) &&
            fieldsValidator(context, editIdDocument, "onlyNumbers", 5, 12, true) &&
            fieldsValidator(context, editPhoneNumber, "onlyNumbers", 7, 10, true) &&
            fieldsValidator(context, editHomeAddress, "homeAddress", 8, 30, true))
    }
}
