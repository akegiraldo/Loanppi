package com.android.loanppi

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import org.json.JSONException
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

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


        if (loginInfo?.get("loginMethod") == "google") {
            loadGoogleInfo()
        } else {
            loadFacebookInfo()
        }

        return view
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
                    //imgUserPhoto.setImageResource()

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
