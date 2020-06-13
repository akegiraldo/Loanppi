package com.android.loanppi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.text.NumberFormat

/**
 * A simple [Fragment] subclass.
 * Use the [my_investment_details.newInstance] factory method to
 * create an instance of this fragment.
 */
class my_investment_details(bundle: Bundle) : Fragment() {
    private val myInvestment = bundle

    // My investment values
    lateinit var returnAmount: TextView
    lateinit var interestsMonthlyAmount: TextView
    lateinit var totalInterestsAmount: TextView
    lateinit var duesWeeklyAmount: TextView
    lateinit var duesMonthlyAmount: TextView
    lateinit var amountInvested: TextView
    lateinit var investReturnAmount: TextView

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

        val valueDuesMonthlyAmount = myInvestment.get("valueToReturnWeekly").toString().toFloat() * 4
        val valueTotalInterestsAmount = myInvestment.get("interestsWins").toString().toFloat()
        val valueTimeToReturn = myInvestment.get("timeToReturn").toString().toInt()

        val copFormat: NumberFormat = NumberFormat.getCurrencyInstance()
        copFormat.maximumFractionDigits = 0

        returnAmount.setText(copFormat.format(myInvestment.get("returnTotal").toString().toInt()))
        interestsMonthlyAmount.setText(copFormat.format(valueTotalInterestsAmount / valueTimeToReturn))
        totalInterestsAmount.setText(copFormat.format(myInvestment.get("interestsWins").toString().toInt()))
        duesWeeklyAmount.setText(copFormat.format(myInvestment.get("valueToReturnWeekly").toString().toInt()))
        duesMonthlyAmount.setText(copFormat.format(valueDuesMonthlyAmount))
        amountInvested.setText(copFormat.format(myInvestment.get("moneyInvestment").toString().toInt()))
        investReturnAmount.setText(copFormat.format(myInvestment.get("returnTotal").toString().toInt()))

        return view
    }
}
