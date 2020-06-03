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
    private val accessInfo : Bundle? = bundle
    private lateinit var account: GoogleSignInAccount

    // Profile fields
    private lateinit var editFirstName: EditText
    private lateinit var editSecondName: EditText
    private lateinit var editFirstLastName: EditText
    private lateinit var editSecondLastName: EditText
    private lateinit var editEmailAdress: EditText
    private lateinit var imgUserPhoto: ImageView
    private lateinit var editIdDocument: EditText
    private lateinit var editHomeAdress: EditText
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

        editFirstName = view.findViewById(R.id.edit_first_name)
        editSecondName = view.findViewById(R.id.edit_second_name)
        editFirstLastName = view.findViewById(R.id.edit_first_last_name)
        editSecondLastName = view.findViewById(R.id.edit_second_last_name)
        editEmailAdress = view.findViewById(R.id.edit_email_adress)
        imgUserPhoto = view.findViewById(R.id.img_user_photo)
        editIdDocument = view.findViewById(R.id.edit_id_document)
        editHomeAdress = view.findViewById(R.id.edit_home_adress)
        editPhoneNumber = view.findViewById(R.id.edit_phone_number)
        
        btnSave = view.findViewById(R.id.btn_save)

        if (accessInfo?.get("signupMethod") == "google" || accessInfo?.get("loginMethod") == "google") {
            loadGoogleInfo()
        } else if (accessInfo?.get("signupMethod") == "facebook" || accessInfo?.get("loginMethod") == "facebook") {
            loadFacebookInfo()
        }

        btnSave.setOnClickListener(View.OnClickListener {
            //validateFields(editFirstLastName, "onlyLetters")
            sendPost()
        })

        return view
    }

    fun sendPost() {
        val url = "http://loanppi.kevingiraldo.tech/app/api/v1/new_user/"
        val user = JSONObject()

        user.put("firstName", editFirstName.text.toString())
        user.put("secondName", editSecondName.text.toString())
        user.put("firstLastName", editFirstLastName.text.toString())
        user.put("secondLastName", editSecondLastName.text.toString())
        user.put("emailAdress", editEmailAdress.text.toString())
        user.put("idDocument", editIdDocument.text.toString())
        user.put("homeAdress", editHomeAdress.text.toString())
        user.put("phoneNumber", editPhoneNumber.text.toString())
        user.put("userType", accessInfo?.get("userType"))

        val queue = Volley.newRequestQueue(context)
        val request = JsonObjectRequest(Request.Method.POST,url,user,
            Response.Listener {
                    response ->
                println("Request succesfull")
                println("Response:" + response.toString())
                if (response.get("STATUS") == "OK"){
                    Toast.makeText(context, "Usuario registrado con éxito", Toast.LENGTH_LONG).show()
                } else if (response.get("STATUS") == "EXISTS") {
                    Toast.makeText(context,"El usuario ya existe.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Error, el usuario no pudo ser registrado", Toast.LENGTH_LONG).show()
                }
            }, Response.ErrorListener { error: VolleyError ->
                Toast.makeText(context, "Error, el usuario no pudo ser registrado", Toast.LENGTH_LONG).show()
                println("Error al guardar el usuario $error.message")
            }
        )
        queue.add(request)
    }

    fun loadGoogleInfo() {
        account = accessInfo?.get("account") as GoogleSignInAccount
        val email = account.email
        val names = account.givenName?.split(" ")
        val surnames = account.familyName?.split(" ")
        val urlUserPhoto = account.photoUrl.toString()

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

        Glide.with(this).load(urlUserPhoto).into(imgUserPhoto)
    }

    fun loadFacebookInfo(){
        if (AccessToken.getCurrentAccessToken() != null) {
            val request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken()) { `object`, response ->
                try {
                    val email = `object`.getString("email")
                    val fullName = `object`.getString("name").split(" ")
                    val facebookId = `object`.getString("id")
                    val urlUserPhoto = "https://graph.facebook.com/" + facebookId + "/picture?type=normal"

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

    // Function for validate the length or type of fields
    fun validateFields(field: EditText, typeValidation: String, length: Int) {
        val onlyNumbers = Regex("[0-9]+")
        val onlyLetters = Regex("[a-zA-Z]+")
        val homeAdress = Regex("")
        val SQLQuery = Regex("DROP|DELETE|UPDATE|SELECT", RegexOption.IGNORE_CASE)
        val textToValidate = field.text.toString()

        when (typeValidation) {
            "onlyNumbers" ->
                if (!onlyNumbers.matches(textToValidate)) {
                    Toast.makeText(context, "El campo no puede contener " +
                            "caractéres diferentes a números.", Toast.LENGTH_LONG).show()
                    field.requestFocus()
                }
            "onlyLetters" ->
                if (!onlyLetters.matches(textToValidate)) {
                    Toast.makeText(context, "El campo no puede contener " +
                            "caractéres diferentes a letras.", Toast.LENGTH_LONG).show()
                    field.requestFocus()
                }
            "homeAdress" ->
                if (!onlyNumbers.matches(textToValidate)) {
                    Toast.makeText(context, "El campo no puede contener " +
                            "caractéres diferentes a números.", Toast.LENGTH_LONG).show()
                    field.requestFocus()
                }
            else -> { return }
        }

        if (SQLQuery.containsMatchIn(textToValidate)) {
            Toast.makeText(context, "No puedes incluir querys", Toast.LENGTH_LONG).show()
            field.requestFocus()
        }
    }
}
