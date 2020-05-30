package com.android.loanppi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

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
    private var param1: String? = null
    private var param2: String? = null
    private val bundle_ : Bundle? = bundle
    private lateinit var account: GoogleSignInAccount

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        account = bundle_?.get("account") as GoogleSignInAccount

        val email = account.email
        val names = account.givenName?.split(" ")
        val surnames = account.familyName?.split(" ")

        val firstName = names?.get(0)
        var secondName = ""
        if (names?.size == 2) { secondName = names.get(1) }

        val firstLastName = surnames?.get(0)
        var secondLastName = ""
        if (surnames?.size == 2) { secondLastName = surnames.get(1) }

        view.findViewById<TextView>(R.id.edit_first_name).setText(firstName)
        view.findViewById<TextView>(R.id.edit_second_name).setText(secondName)
        view.findViewById<TextView>(R.id.edit_first_last_name).setText(firstLastName)
        view.findViewById<TextView>(R.id.edit_second_last_name).setText(secondLastName)
        view.findViewById<TextView>(R.id.edit_email_adress).setText(email)

        return view
    }
}
