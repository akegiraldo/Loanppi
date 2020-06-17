package com.android.loanppi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat

/**
 * A simple [Fragment] subclass.
 * Use the [my_investment_details.newInstance] factory method to
 * create an instance of this fragment.
 */
class my_investment_details(bundle: Bundle) : Fragment() {
    private val myInvestment = bundle

    // My investment values
    private lateinit var returnAmount: TextView
    private lateinit var interestsMonthlyAmount: TextView
    private lateinit var totalInterestsAmount: TextView
    private lateinit var duesWeeklyAmount: TextView
    private lateinit var duesMonthlyAmount: TextView
    private lateinit var amountInvested: TextView
    private lateinit var investReturnAmount: TextView
    private lateinit var numberRemainingDues: TextView
    private lateinit var returnRemaining: TextView
    private lateinit var amountReturned: TextView
    private lateinit var progressBar: SeekBar
    private var progressPercent = 0.0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_investment_details, container, false)

        returnAmount = view.findViewById(R.id.txt_value_return_amount)
        interestsMonthlyAmount = view.findViewById(R.id.txt_invest_value_interests_monthly_amount)
        totalInterestsAmount = view.findViewById(R.id.txt_invest_value_total_interests_amount)
        duesWeeklyAmount = view.findViewById(R.id.txt_invest_value_dues_weekly_amount)
        duesMonthlyAmount = view.findViewById(R.id.txt_invest_value_dues_monthly_amount)
        amountInvested = view.findViewById(R.id.txt_invest_value_amount_invested)
        investReturnAmount = view.findViewById(R.id.txt_invest_value_return_amount)
        numberRemainingDues = view.findViewById(R.id.txt_invest_value_number_remaining_dues)
        returnRemaining = view.findViewById(R.id.txt_invest_value_return_remaining)
        amountReturned = view.findViewById(R.id.txt_value_amount_returned)
        progressBar = view.findViewById(R.id.bar_investment_progress_moto_bar)

        getInvestment()

        return view
    }

    fun getInvestment() {
        val id = myInvestment.get("idInvestment").toString()
        val url = "http://loanppi.kevingiraldo.tech/app/api/v1/my_investment?idInvestment=" + id
        val queue = Volley.newRequestQueue(context)

        // Request a JSON response from the provided URL.
        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                println("MYINVESTMENT: " + response.toString())
                if (response.length() > 0) {
                    val investment = response.get(0) as JSONObject
                    val paysList = response.get(1) as JSONArray
                    myInvestment.putString("idInvestment", investment.get("idInvestment").toString())
                    myInvestment.putString("timeToReturn", investment.get("timeToReturn").toString())
                    myInvestment.putString("returnTotal", investment.get("returnTotal").toString())
                    myInvestment.putString("valueToReturnWeekly", investment.get("valueToReturnWeekly").toString()                    )
                    myInvestment.putString("interestsWins", investment.get("interestsWins").toString())
                    myInvestment.putString("moneyInvestment", investment.get("moneyInvestment").toString())
                    myInvestment.putString("totalReturn", investment.get("totalReturn").toString())
                    loadMyInvestmentInfo()
                    //loadPays(response.get(1) as JSONArray)
                } else {
                    Toast.makeText(context, "No se encuentra ningún préstamo asociado al usuario.",
                        Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener {
                Toast.makeText(context, "Error en la consulta", Toast.LENGTH_LONG).show()
                println("ERROR CONSULTA: " + it.toString())
            })

// Add the request to the RequestQueue.
        queue.add(request)
    }

    fun loadMyInvestmentInfo() {
        val valueDuesMonthlyAmount = myInvestment.get("valueToReturnWeekly").toString().toFloat() * 4
        val valueTotalInterestsAmount = myInvestment.get("interestsWins").toString().toFloat()
        val valueAmountInvested = myInvestment.get("moneyInvestment").toString().toInt()
        val valueTimeToReturn = myInvestment.get("timeToReturn").toString().toInt()
        val valueTotalReturned = myInvestment.get("totalReturn").toString().toInt()
        val valueReturnRemaining = myInvestment.get("returnTotal").toString().toInt() - valueTotalReturned

        val copFormat: NumberFormat = NumberFormat.getCurrencyInstance()
        copFormat.maximumFractionDigits = 0

        returnAmount.setText(copFormat.format(myInvestment.get("returnTotal").toString().toInt()))
        interestsMonthlyAmount.setText(copFormat.format(valueTotalInterestsAmount / valueTimeToReturn))
        totalInterestsAmount.setText(copFormat.format(myInvestment.get("interestsWins").toString().toInt()))
        duesWeeklyAmount.setText(copFormat.format(myInvestment.get("valueToReturnWeekly").toString().toFloat()))
        investReturnAmount.setText(copFormat.format(myInvestment.get("returnTotal").toString().toInt()))
        returnRemaining.setText(copFormat.format(valueReturnRemaining))
        duesMonthlyAmount.setText(copFormat.format(valueDuesMonthlyAmount))
        amountInvested.setText(copFormat.format(valueAmountInvested))
        amountReturned.setText(copFormat.format(valueTotalReturned))
        numberRemainingDues.setText((valueReturnRemaining / myInvestment.get("valueToReturnWeekly")
            .toString().toInt()).toString())
        progressPercent = (valueTotalReturned / myInvestment.get("returnTotal").toString().toFloat()) * 100
    }

    fun loadPays(feesList: JSONArray) {
        /*println("feesList: " + feesList.toString())
        for (i in 0..(feesList.length() - 1)) {
            feesData.add((i + 1).toString())
            feesData.add(feesList.getJSONObject(i).get("payment").toString())
            feesArrayList.add(getStringFromArray(feesData))
            feesData.clear()
            arrayAdapter.notifyDataSetChanged()
        }*/
    }

    fun getStringFromArray(feesData: ArrayList<String>): String {
        return (feesData.get(0).toString())
    }
}
