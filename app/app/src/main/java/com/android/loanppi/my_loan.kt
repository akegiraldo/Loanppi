package com.android.loanppi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_my_loan.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat

/**
 * A simple [Fragment] subclass.
 * Use the [my_loan.newInstance] factory method to
 * create an instance of this fragment.
 */
class my_loan(bundle: Bundle?) : Fragment() {
    val bundle = bundle
    var myLoan = Bundle()
    private lateinit var account: Bundle

    // My loan values
    private var amountRemaining = 0.0f
    private var progressPercent = 0.0f
    private var amountPaid = 0.0f
    lateinit var totalInterestsAmount: TextView
    lateinit var interestsMonthlyAmount: TextView
    lateinit var goalAmount: TextView
    lateinit var duesWeeklyAmount: TextView
    lateinit var duesMonthlyAmount: TextView
    lateinit var amountLent: TextView
    lateinit var totalToPay: TextView
    lateinit var valueAmountPaid: TextView
    lateinit var remainingFeeNumber: TextView
    lateinit var dueAmount: TextView
    lateinit var progressBar: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_loan, container, false)
        myLoan = bundle?.get("myLoan") as Bundle
        account = bundle.getBundle("account") as Bundle
        getLoan()

        goalAmount = view.findViewById(R.id.txt_loan_value_goal_amount)
        interestsMonthlyAmount = view.findViewById(R.id.txt_loan_value_interests_monthly_amount)
        totalInterestsAmount = view.findViewById(R.id.txt_loan_value_total_interests_amount) as TextView
        duesWeeklyAmount = view.findViewById(R.id.txt_loan_value_dues_weekly_amount)
        duesMonthlyAmount = view.findViewById(R.id.txt_loan_value_dues_monthly_amount)
        amountLent = view.findViewById(R.id.txt_loan_value_amount_lent)
        totalToPay = view.findViewById(R.id.txt_loan_value_total_to_pay)
        valueAmountPaid = view.findViewById(R.id.txt_loan_value_amount_paid)
        remainingFeeNumber = view.findViewById(R.id.txt_loan_value_remaining_fee_number)
        dueAmount = view.findViewById(R.id.txt_loan_value_due_amount)
        progressBar = view.findViewById(R.id.bar_loan_progress_moto_bar)
        
        return view
    }

    fun getLoan() {
        val id = account.get("userId")
        val url = "http://loanppi.kevingiraldo.tech/app/api/v1/my_loan?idWorker="+id
        val queue = Volley.newRequestQueue(context)

        // Request a JSON response from the provided URL.
        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                println("MYLOAN: " + response.toString())
                if (response.length() > 0) {
                    val loan = response.get(0) as JSONObject
                    val feeNumber = response.get(1) as JSONArray
                    myLoan.putString("idNeed", loan.get("idNeed").toString())
                    myLoan.putString("idWorker", loan.get("idWorker").toString())
                    myLoan.putString("totalToPay", loan.get("totalToPay").toString())
                    myLoan.putString("timeToPay", loan.get("timeToPay").toString())
                    myLoan.putString("valueToPayWeekly", loan.get("valueToPayWeekly").toString())
                    myLoan.putString("interests", loan.get("interests").toString())
                    myLoan.putString("loanAmount", loan.get("loanAmount").toString())
                    myLoan.putString("amountRemaining", loan.get("amountRemaining").toString())
                    myLoan.putString("loanReason", loan.get("loanReason").toString())
                    myLoan.putString("status", loan.get("status").toString())
                    myLoan.putString("feeNumber", feeNumber.length().toString())
                    if (myLoan.get("status") != "paid") {
                        loadMyLoanInfo()
                    }
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

    fun loadMyLoanInfo() {
        val valueToPayWeekly = myLoan.get("valueToPayWeekly").toString().toFloat()
        val goal = myLoan.get("totalToPay").toString().toFloat()

        amountPaid = myLoan.get("amountRemaining").toString().toFloat()
        amountRemaining = goal - amountPaid
        copFormat.maximumFractionDigits = 0

        val valueToPayMonthly = myLoan.get("valueToPayWeekly").toString().toFloat() * 4
        val valueTotalInterestsAmount = myLoan.get("interests").toString().toFloat()
        val valueTimeToPay = myLoan.get("timeToPay").toString().toInt()

        progressBar.progress = progressPercent.toInt()
        goalAmount.setText(copFormat.format(goal))
        duesWeeklyAmount.setText(copFormat.format(valueToPayWeekly))
        remainingFeeNumber.setText(myLoan.get("feeNumber").toString())
        dueAmount.setText(copFormat.format(goal))
        interestsMonthlyAmount.setText(copFormat.format(valueTotalInterestsAmount / valueTimeToPay))
        totalInterestsAmount.setText(copFormat.format(myLoan.get("interests").toString().toFloat()))
        duesMonthlyAmount.setText(copFormat.format(valueToPayMonthly))
        amountLent.setText(copFormat.format(myLoan.get("loanAmount").toString().toFloat()))
        totalToPay.setText(copFormat.format(myLoan.get("totalToPay").toString().toFloat()))

        if (myLoan.get("status") == "resolved") {
            progressPercent = (amountPaid / goal) * 100
            valueAmountPaid.setText(copFormat.format(amountPaid))
            dueAmount.setText(copFormat.format(amountRemaining))
        } else {
            progressPercent = 0.0f
            dueAmount.setText(copFormat.format(goal))
        }
    }
}

