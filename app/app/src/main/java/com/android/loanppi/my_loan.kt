package com.android.loanppi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_my_loan.*
import java.text.NumberFormat

/**
 * A simple [Fragment] subclass.
 * Use the [my_loan.newInstance] factory method to
 * create an instance of this fragment.
 */
class my_loan(bundle: Bundle?) : Fragment() {
    val bundle = bundle
    var myLoan = Bundle()

    // My loan values
    lateinit var totalInterestsAmount: TextView
    lateinit var interestsMonthlyAmount: TextView
    lateinit var metaAmount: TextView
    lateinit var duesWeeklyAmount: TextView
    lateinit var duesMonthlyAmount: TextView
    lateinit var amountLent: TextView
    lateinit var totalToPay: TextView

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

        metaAmount = view.findViewById(R.id.txt_value_meta_amount)
        interestsMonthlyAmount = view.findViewById(R.id.txt_loan_value_interests_monthly_amount)
        totalInterestsAmount = view.findViewById(R.id.txt_loan_value_total_interests_amount) as TextView
        duesWeeklyAmount = view.findViewById(R.id.txt_loan_value_dues_weekly_amount)
        duesMonthlyAmount = view.findViewById(R.id.txt_loan_value_dues_monthly_amount)
        amountLent = view.findViewById(R.id.txt_loan_value_amount_lent)
        totalToPay = view.findViewById(R.id.txt_loan_value_total_to_pay)

        val valueToPayMonthly = myLoan.get("valueToPayWeekly").toString().toFloat() * 4
        val valueTotalInterestsAmount = myLoan.get("interests").toString().toFloat()
        val valueTimeToPay = myLoan.get("timeToPay").toString().toInt()

        val copFormat: NumberFormat = NumberFormat.getCurrencyInstance()
        copFormat.maximumFractionDigits = 0

        metaAmount.setText(copFormat.format(myLoan.get("totalToPay").toString().toInt()))
        interestsMonthlyAmount.setText(copFormat.format(valueTotalInterestsAmount / valueTimeToPay))
        totalInterestsAmount.setText(copFormat.format(myLoan.get("interests").toString().toInt()))
        duesWeeklyAmount.setText(copFormat.format(myLoan.get("valueToPayWeekly").toString().toInt()))
        duesMonthlyAmount.setText(copFormat.format(valueToPayMonthly))
        amountLent.setText(copFormat.format(myLoan.get("loanAmount").toString().toInt()))
        totalToPay.setText(copFormat.format(myLoan.get("totalToPay").toString().toInt()))

        return view
    }
}

