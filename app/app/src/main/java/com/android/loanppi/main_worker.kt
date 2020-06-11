package com.android.loanppi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

/**
 * A simple [Fragment] subclass.
 * Use the [main_worker.newInstance] factory method to
 * create an instance of this fragment.
 */
class main_worker(bundle: Bundle?) : Fragment() {
    private lateinit var accessInfo: Bundle
    private lateinit var account: Bundle
    private lateinit var myLoan: Bundle
    private var bundle: Bundle? = bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_main_worker, container, false)
        accessInfo = bundle?.getBundle("accessInfo") as Bundle
        account = bundle?.getBundle("account") as Bundle
        myLoan = bundle?.getBundle("myLoan") as Bundle

        val firstName: String? = account.getString("firstName")

        var urlPhoto: String? = ""
        if (accessInfo.get("accessWith") == "facebook") {
            val facebookAccount = accessInfo.get("facebookAccount") as Bundle
            urlPhoto = facebookAccount.get("urlUserPhoto") as String
        } else {
            val googleAccount = accessInfo.get("googleAccount") as GoogleSignInAccount
            urlPhoto = googleAccount.photoUrl.toString()
        }

        if (myLoan.get("status") != "not_found") {
            val meta = myLoan.get("totalToPay")
            view.findViewById<TextView>(R.id.txt_value_meta_amount).setText(meta.toString())
        }

        view.findViewById<TextView>(R.id.txt_grettings).setText("Hola, "+ firstName)
        Glide.with(this).load(urlPhoto).into(view.findViewById(R.id.img_user_photo))

        return view
    }

}
