package com.android.loanppi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * A simple [Fragment] subclass.
 * Use the [my_loan.newInstance] factory method to
 * create an instance of this fragment.
 */
class my_loan(bundle: Bundle?) : Fragment() {
    val bundle = bundle
    var myLoan = Bundle()

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

        return view
    }
}

