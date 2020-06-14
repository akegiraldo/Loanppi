package com.android.loanppi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.android.synthetic.main.fragment_main_worker.*

/**
 * A simple [Fragment] subclass.
 * Use the [main_worker.newInstance] factory method to
 * create an instance of this fragment.
 */
class main_worker(bundle: Bundle?) : Fragment() {
    private lateinit var accessInfo: Bundle
    private lateinit var account: Bundle
    private var myLoan = Bundle()
    private var bundle = bundle

    // Main buttons
    private lateinit var btn_lend: Button
    private lateinit var btn_my_loan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_main_worker, container, false)

        btn_lend = view.findViewById(R.id.btn_lend)
        btn_my_loan = view.findViewById(R.id.btn_my_loan)

        accessInfo = bundle?.getBundle("accessInfo") as Bundle
        account = bundle?.getBundle("account") as Bundle
        myLoan.putString("status", "loading")
        bundle?.putBundle("myLoan", myLoan)
        getLoan()

        val firstName: String? = account.getString("firstName")

        var urlPhoto: String? = ""
        if (accessInfo.get("accessWith") == "facebook") {
            val facebookAccount = accessInfo.get("facebookAccount") as Bundle
            urlPhoto = facebookAccount.get("urlUserPhoto") as String
        } else {
            val googleAccount = accessInfo.get("googleAccount") as GoogleSignInAccount
            urlPhoto = googleAccount.photoUrl.toString()
        }

        view.findViewById<TextView>(R.id.txt_grettings).setText("Hola, "+ firstName)
        Glide.with(this).load(urlPhoto).into(view.findViewById(R.id.img_user_photo))

        btn_lend.setOnClickListener(View.OnClickListener {
            if (myLoan.get("status") != "loading") {
                if (myLoan.get("status") == "not_found") {
                    replaceFragment(lend(bundle), parentFragmentManager)
                } else {
                    Toast.makeText(context, "Ya tienes un préstamo activo", Toast.LENGTH_LONG)
                        .show()
                }
            }
        })

        btn_my_loan.setOnClickListener(View.OnClickListener {
            if (myLoan.get("status") != "loading") {
                if (myLoan.get("status") != "not_found") {
                    replaceFragment(my_loan(bundle), parentFragmentManager)
                } else {
                    Toast.makeText(context, "Debes solicitar un préstamo primero", Toast.LENGTH_LONG).show()
                }
            }
        })

        return view
    }

    fun getLoan() {
        val id = account.get("userId")
        val url = "http://loanppi.kevingiraldo.tech/app/api/v1/active_loan?idWorker="+id
        val queue = Volley.newRequestQueue(context)

        // Request a JSON response from the provided URL.
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                if (response.length() > 0) {
                    myLoan.putString("idNeed", response.get("idNeed").toString())
                    myLoan.putString("idWorker", response.get("idWorker").toString())
                    myLoan.putString("totalToPay", response.get("totalToPay").toString())
                    myLoan.putString("timeToPay", response.get("timeToPay").toString())
                    myLoan.putString("valueToPayWeekly", response.get("valueToPayWeekly").toString())
                    myLoan.putString("interests", response.get("interests").toString())
                    myLoan.putString("loanAmount", response.get("loanAmount").toString())
                    myLoan.putString("amountRemaining", response.get("amountRemaining").toString())
                    myLoan.putString("loanReason", response.get("loanReason").toString())
                    myLoan.putString("status", response.get("status").toString())

                    val meta = myLoan.get("totalToPay")
                    view?.findViewById<TextView>(R.id.txt_value_meta_amount)?.setText(meta.toString())
                } else {
                    Toast.makeText(context, "No se encuentra ningún préstamo asociado al usuario.",
                        Toast.LENGTH_LONG).show()
                    myLoan.putString("status", "not_found")
                }
            },
            Response.ErrorListener {
                Toast.makeText(context, "Error en la consulta", Toast.LENGTH_LONG).show()
                println("ERROR CONSULTA: " + it.toString())
                myLoan.putString("status", "not_found")
            })

        // Add the request to the RequestQueue.
        queue.add(request)
    }
}
