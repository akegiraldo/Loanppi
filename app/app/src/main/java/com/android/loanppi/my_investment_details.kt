package com.android.loanppi

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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

    // List of fees
    private lateinit var feesList: ListView
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var feesData: ArrayList<String>
    private lateinit var feesArrayList: ArrayList<String>

    // Others
    private lateinit var scrollView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("ClickableViewAccessibility")
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
        feesList = view.findViewById(R.id.list_fees_returned)
        scrollView = view.findViewById(R.id.investment_scroll)

        feesData = ArrayList()
        feesArrayList = ArrayList()
        arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, feesArrayList)
        feesList.adapter = arrayAdapter

        // Get selected investment
        getInvestment()

        scrollView.setOnTouchListener(View.OnTouchListener { v, event ->
            feesList.getParent()
                .requestDisallowInterceptTouchEvent(false)
            false
        })

        feesList.setOnTouchListener(View.OnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        })

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
                    loadPays(paysList)
                } else {
                    Toast.makeText(context, "No se encuentra ninguna inversión asociada al usuario.",
                        Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener {
                Toast.makeText(context, "Error en la conexión con el servidor.",
                    Toast.LENGTH_SHORT).show()
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
        val valueRemainingDues = (valueReturnRemaining / myInvestment.get("valueToReturnWeekly")
            .toString().toFloat()).toInt()

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
        numberRemainingDues.setText(valueRemainingDues.toString())
        progressPercent = (valueTotalReturned / myInvestment.get("returnTotal").toString().toFloat()) * 100
    }

    fun loadPays(feesList: JSONArray) {
        println("feesList: " + feesList.toString())
        for (i in 0..(feesList.length() - 1)) {
            feesData.add((i + 1).toString())
            feesData.add(feesList.getJSONObject(i).get("investorShare").toString())
            feesData.add(feesList.getJSONObject(i).get("dateReturn").toString())
            feesArrayList.add(getStringFromArray(feesData))
            feesData.clear()
            arrayAdapter.notifyDataSetChanged()
        }
    }

    fun getStringFromArray(feesData: ArrayList<String>): String {
        copFormat.maximumFractionDigits = 0
        var string = ""
        var date = feesData.get(2).replace("T", " ")
        date = date.replace(".000Z", "")

        for (i in 0..7) { string += " " }
        string += feesData.get(0)
        for (i in feesData.get(0).length..15) { string += " " }
        string += copFormat.format(feesData.get(1).toInt())
        for (i in feesData.get(1).length..16) { string += " " }
        string += date.toDate().formatTo("dd MMM yyyy HH:mm:ss")
        return string
    }
}
