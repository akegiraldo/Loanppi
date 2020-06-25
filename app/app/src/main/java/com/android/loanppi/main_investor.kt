package com.android.loanppi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.android.synthetic.main.fragment_main_investor.*
import org.json.JSONArray
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 * Use the [main_investor.newInstance] factory method to
 * create an instance of this fragment.
 */
class main_investor(bundle: Bundle?) : Fragment() {
    // Account info
    private lateinit var accessInfo: Bundle
    private lateinit var account: Bundle
    private var bundle: Bundle? = bundle

    // Main investor fields
    private lateinit var valueFullInvestedAmount: TextView
    private lateinit var valueFullEarns: TextView
    private var fullInvestedAmount = 0
    private var fullEarns = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_main_investor, container, false)

        accessInfo = bundle?.getBundle("accessInfo") as Bundle
        account = bundle?.getBundle("account") as Bundle

        // Get textviews and edittext from fragment
        valueFullInvestedAmount = view.findViewById(R.id.txt_main_value_full_invested_amount)
        valueFullEarns = view.findViewById(R.id.txt_main_value_full_earns)

        // Upload the photo and the first name of the user
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

        // Calls the function responsible for checking whether the user has currently
        // active investments
        getInvestments()

        return view
    }

    // Function that makes the request to the server to get user investments
    fun getInvestments() {
        val id = account.get("userId")
        val url = "http://loanppi.kevingiraldo.tech/app/api/v1/my_investments?idInvestor=" + id
        val queue = Volley.newRequestQueue(context)

        // Request a JSON response from the provided URL.
        // Create the request. If you have any active investments, it calculates information on
        // all investments and calls the function responsible for displaying this data. Otherwise,
        // it calls the function in charge of showing a message encouraging to invest
        val request = JsonArrayRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                if (response.length() > 0) {
                    for (i in 0..(response.length() - 1)) {
                        fullInvestedAmount += response.getJSONObject(i).get("moneyInvestment").toString().toInt()
                        fullEarns += response.getJSONObject(i).get("totalReturn").toString().toFloat()
                    }
                    loadMainInfo("show")
                } else {
                    loadMainInfo("hide")
                }
            },
            Response.ErrorListener {
                println("Error al cargar inversiones: " + it.toString())
                Toast.makeText(context, "Error al intentar cargar las inversiones.",
                    Toast.LENGTH_SHORT).show()
            })

        // Add the request to the RequestQueue.
        queue.add(request)
    }

    // Loads the information obtained to the user's main fragment depending on whether or not he
    // has at least one active investment
    fun loadMainInfo(status: String) {
        if (status == "hide") {
            view?.findViewById<TextView>(R.id.txt_is_empty)?.isVisible = true
            view?.findViewById<TextView>(R.id.txt_is_not_empty)?.isVisible = false
            view?.findViewById<TextView>(R.id.txt_main_full_earns)?.isVisible = false
            view?.findViewById<TextView>(R.id.txt_main_full_invested_amount)?.isVisible = false
            valueFullEarns.isVisible = false
            valueFullInvestedAmount.isVisible = false
        } else {
            view?.findViewById<TextView>(R.id.txt_is_empty)?.isVisible = false
            view?.findViewById<TextView>(R.id.txt_is_not_empty)?.isVisible = true
            view?.findViewById<TextView>(R.id.txt_main_full_earns)?.isVisible = true
            view?.findViewById<TextView>(R.id.txt_main_full_invested_amount)?.isVisible = true
            valueFullEarns.isVisible = true
            valueFullInvestedAmount.isVisible = true
        }
        copFormat.maximumFractionDigits = 0
        valueFullInvestedAmount.setText(copFormat.format(fullInvestedAmount))
        valueFullEarns.setText(copFormat.format(fullEarns))
    }
}
