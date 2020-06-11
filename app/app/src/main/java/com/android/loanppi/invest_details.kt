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
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.text.NumberFormat

/**
 * A simple [Fragment] subclass.
 * Use the [invest_details.newInstance] factory method to
 * create an instance of this fragment.
 */
class invest_details(bundle1: Bundle, bundle2: Bundle?) : Fragment() {
    // Loan data
    private val loanData = bundle1

    // Account data
    private val account = bundle2

    // Invest fields
    private lateinit var editInvestAmount: EditText
    private lateinit var txtMaxInvestAmount: TextView
    private lateinit var valTimeToReturn: TextView
    private lateinit var valReturnWeekly: TextView
    private lateinit var valReturnMonthly: TextView
    private lateinit var valInterestsWins: TextView
    private lateinit var valReturnTotal: TextView
    private lateinit var btnInvest: Button

    // Invest values
    private var investAmount = 0
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
        val view = inflater.inflate(R.layout.fragment_invest_details, container, false)

        editInvestAmount = view.findViewById(R.id.edit_invest_amount)
        txtMaxInvestAmount = view.findViewById(R.id.txt_value_max_invest_amount)
        valTimeToReturn = view.findViewById(R.id.txt_value_time_to_return)
        valReturnWeekly = view.findViewById(R.id.txt_value_return_weekly)
        valReturnMonthly = view.findViewById(R.id.txt_value_return_monthly)
        valInterestsWins = view.findViewById(R.id.txt_value_interests_wins)
        valReturnTotal = view.findViewById(R.id.txt_value_return_total)

        btnInvest = view.findViewById(R.id.btn_let_invest)

        val copFormat: NumberFormat = NumberFormat.getCurrencyInstance()
        copFormat.maximumFractionDigits = 0
        var investStack = loanData.get("investStack").toString().toInt()
        val amountRemaining = loanData.get("amountRemaining").toString().toInt()
        if (investStack > amountRemaining) {
            investStack = amountRemaining
        }

        editInvestAmount.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val investAmountStr = editInvestAmount.text.toString()
                if (investAmountStr != "" && investAmountStr.toInt() >= 50000 &&
                    investAmountStr.toInt() <= investStack) {
                    investAmount = investAmountStr.toInt()
                    interestsWins = investAmount * 0.05F
                    returnTotal = investAmount + interestsWins

                    timeToReturn = loanData.get("timeToPay").toString().toInt()

                    valueToReturnMonthly = returnTotal / timeToReturn
                    valueToReturnWeekly = valueToReturnMonthly / 4

                    valTimeToReturn.setText(timeToReturn.toString() + " meses")
                    valReturnWeekly.setText(copFormat.format(valueToReturnWeekly))
                    valReturnMonthly.setText(copFormat.format(valueToReturnMonthly))
                    valInterestsWins.setText(copFormat.format(interestsWins))
                    valReturnTotal.setText(copFormat.format(returnTotal))
                } else {
                    valTimeToReturn.setText("0 meses")
                    valReturnWeekly.setText("$00,000")
                    valReturnMonthly.setText("$000,000")
                    valInterestsWins.setText("$000,000")
                    valReturnTotal.setText("$0,000,000")
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        editInvestAmount.setText(investStack.toString())
        txtMaxInvestAmount.setText(copFormat.format(investStack))

        btnInvest.setOnClickListener(View.OnClickListener {
            //if (validateFields()) {
            postNewInvestment()
            //}
        })

        return view
    }

    fun postNewInvestment() {
        val url = "http://loanppi.kevingiraldo.tech/app/api/v1/new_investment/"
        val investment = JSONObject()

        println("IDNEED DETAILS: " + loanData.get("idNeed"))

        investment.put("moneyInvestment", investAmount)
        investment.put("idInvestor", account?.get("userId"))
        investment.put("idNeed", loanData.get("idNeed"))

        val queue = Volley.newRequestQueue(context)
        val request = JsonObjectRequest(Request.Method.POST, url, investment,
            Response.Listener { response ->
                println("RESPONSE:" + response.toString())
                if (response.get("status") == "created") {
                    Toast.makeText(context,"Inversión registrada con éxito.", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(context,"Error, la inversión no fue registrada.",
                        Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error: VolleyError ->
                Toast.makeText(context,"Error en la petición.",Toast.LENGTH_LONG
                ).show()
                println("Error en la petición: ${error.message}")
            }
        )
        queue.add(request)
    }
}