package com.android.loanppi

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager

/**
 * A simple [Fragment] subclass.
 * Use the [invest.newInstance] factory method to
 * create an instance of this fragment.
 */
class invest() : Fragment() {

    // Invest fields
    private lateinit var editInvestAmount: EditText
    private lateinit var spinTimeToReturn: Spinner
    private lateinit var valTimeToReturn: TextView
    private lateinit var valReturnWeekly: TextView
    private lateinit var valReturnMonthly: TextView
    private lateinit var valInterestsWins: TextView
    private lateinit var valReturnTotal: TextView
    private lateinit var btnInvest: Button

    // Invest values
    private var moneyAvailable = 0.0F
    private var investAmount = 0.0F
    private var interestsWins = 0.0F
    private var returnTotal = 0.0F
    private var timeToReturn = 0
    private var valueToReturnMonthly = 0.0F
    private var valueToReturnWeekly = 0.0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_invest, container, false)

        editInvestAmount = view.findViewById(R.id.edit_invest_amount)
        spinTimeToReturn = view.findViewById(R.id.spin_time_to_return)
        valTimeToReturn = view.findViewById(R.id.txt_value_time_to_return)
        valReturnWeekly = view.findViewById(R.id.txt_value_return_weekly)
        valReturnMonthly = view.findViewById(R.id.txt_value_return_monthly)
        valInterestsWins = view.findViewById(R.id.txt_value_interests_wins)
        valReturnTotal = view.findViewById(R.id.txt_value_return_total)

        btnInvest = view.findViewById(R.id.btn_invest)

        //getMoneyAvailable()

        editInvestAmount.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val investAmountTxt = editInvestAmount.text.toString()
                /*if (investAmountTxt != "" && investAmountTxt.toInt() >= 200000 &&
                    lendAmountTxt.toInt() <= 3000000) {
                    lendAmount = lendAmountTxt.toFloat()
                    interests = lendAmount * 0.05F
                    totalToPay = lendAmount + interests

                    if (lendAmount >= 200000 && lendAmount <= 1500000) {
                        timeToPay = 6
                    } else {
                        timeToPay = 12
                    }

                    valueToPayMonthly = totalToPay / timeToPay
                    valueToPayWeekly = valueToPayMonthly / 4

                    valTimeToPay.setText(timeToPay.toString() + " meses")
                    valToPayWeekly.setText(valueToPayWeekly.toString())
                    valToPayMonthly.setText(valueToPayMonthly.toString())
                    valInterests.setText(interests.toString())
                    valTotalToPay.setText(totalToPay.toString())
                } else {
                    valTimeToPay.setText("0 meses")
                    valToPayWeekly.setText("00.000")
                    valToPayMonthly.setText("000.000")
                    valInterests.setText("000.000")
                    valTotalToPay.setText("0'000.000")
                }*/
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        return view
    }

    fun getMoneyAvailable() {
        val url = "http://loanppi.kevingiraldo.tech/app/api/v1/"
        val queue = Volley.newRequestQueue(context)

        // Request a JSON response from the provided URL.
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                Toast.makeText(context, response.get("status").toString(), Toast.LENGTH_LONG).show()
                if (response.get("status") == "available") {
                    moneyAvailable = response.get("moneyAvailable") as Float
                } else {
                    Toast.makeText(context, "No hay negocios disponibles para invertir",
                        Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener {
                Toast.makeText(context, "Error en la petición de dinero disponible",
                    Toast.LENGTH_LONG).show()
            })

        // Add the request to the RequestQueue.
        queue.add(request)
    }
}