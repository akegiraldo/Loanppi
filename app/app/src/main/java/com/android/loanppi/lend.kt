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
 * Use the [lend.newInstance] factory method to
 * create an instance of this fragment.
 */
class lend(bundle: Bundle?) : Fragment() {
    // Account info
    private var account: Bundle? = Bundle()
    private var bundle: Bundle? = bundle

    // Lend fields
    private lateinit var editLendAmount: EditText
    private lateinit var spinLendReason: Spinner
    private lateinit var valTimeToPay: TextView
    private lateinit var valToPayWeekly: TextView
    private lateinit var valToPayMonthly: TextView
    private lateinit var valInterests: TextView
    private lateinit var valTotalToPay: TextView
    private lateinit var btnLend: Button

    // Loan values
    private var loanAmount = 0
    private var interests = 0.0F
    private var totalToPay = 0.0F
    private var timeToPay = 0
    private var valueToPayMonthly = 0.0F
    private var valueToPayWeekly = 0.0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_lend, container, false)

        account = bundle?.get("account") as Bundle

        // Get textviews and edittext from fragment
        editLendAmount = view.findViewById(R.id.edit_lend_amount)
        spinLendReason = view.findViewById(R.id.spin_lend_reason)
        valTimeToPay = view.findViewById(R.id.txt_value_time_to_pay)
        valToPayWeekly = view.findViewById(R.id.txt_value_to_pay_weekly)
        valToPayMonthly = view.findViewById(R.id.txt_value_to_pay_monthly)
        valInterests = view.findViewById(R.id.txt_value_interests)
        valTotalToPay = view.findViewById(R.id.txt_value_total_to_pay)
        btnLend = view.findViewById(R.id.btn_let_lend)

        copFormat.maximumFractionDigits = 0

        // Listen changes on edit lend amount and calculates loan values
        editLendAmount.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val loanAmountTxt = editLendAmount.text.toString()
                if (loanAmountTxt != "" && loanAmountTxt.toInt() >= 200000 &&
                    loanAmountTxt.toInt() <= 3000000) {
                    loanAmount = loanAmountTxt.toInt()

                    if (loanAmount >= 200000 && loanAmount <= 1500000) {
                        timeToPay = 6
                    } else {
                        timeToPay = 12
                    }

                    interests = (((loanAmount / timeToPay) * 0.05) * timeToPay).toFloat()
                    totalToPay = loanAmount + interests
                    valueToPayMonthly = totalToPay / timeToPay
                    valueToPayWeekly = valueToPayMonthly / 4

                    valTimeToPay.setText(timeToPay.toString() + " meses")
                    valToPayWeekly.setText(copFormat.format(valueToPayWeekly))
                    valToPayMonthly.setText(copFormat.format(valueToPayMonthly))
                    valInterests.setText(copFormat.format(interests))
                    valTotalToPay.setText(copFormat.format(totalToPay))
                } else {
                    valTimeToPay.setText("0 meses")
                    valToPayWeekly.setText("$00,000")
                    valToPayMonthly.setText("$000,000")
                    valInterests.setText("$000,000")
                    valTotalToPay.setText("$0,000,000")
                }

                println("Account: " + account.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Listen when lend button is clicked, Validate that the fields are correct and call the
        // function in charge of making the request
        btnLend.setOnClickListener(View.OnClickListener {
            if (validateField()) {
                lendPost()
            }
        })

        return view
    }

    // Function that makes the request to the server to register a new loan
    fun lendPost() {
        val url = "http://loanppi.kevingiraldo.tech/app/api/v1/lend/"
        val loan = JSONObject()

        loan.put("totalToPay", totalToPay)
        loan.put("loanAmount", loanAmount)
        loan.put("timeToPay", timeToPay)
        loan.put("valueToPayWeekly", valueToPayWeekly)
        loan.put("loanReason", spinLendReason.selectedItem.toString())
        loan.put("interests", interests)
        loan.put("idWorker", account?.get("userId"))

        // Create the request. In case of success save the values in a variable and call the
        // fragment in charge. On the contrary, Report the error.
        val queue = Volley.newRequestQueue(context)
        val request = JsonObjectRequest(Request.Method.POST, url, loan, Response.Listener {
            response ->
                if (response.get("status") == "pending") {
                    Toast.makeText(context, "Préstamo solicitado correctamente.",
                        Toast.LENGTH_SHORT).show()
                    /*val myLoan = Bundle()
                    myLoan.putString("idNeed", response.get("idNeed").toString())
                    myLoan.putString("idWorker", response.get("idWorker").toString())
                    myLoan.putString("totalToPay", response.get("totalToPay").toString())
                    myLoan.putString("timeToPay", response.get("timeToPay").toString())
                    myLoan.putString("valueToPayWeekly", response.get("valueToPayWeekly").toString())
                    myLoan.putString("interests", response.get("interests").toString())
                    myLoan.putString("status", response.get("status").toString())
                    myLoan.putString("loanAmount", response.get("loanAmount").toString())
                    myLoan.putString("amountRemaining", response.get("amountRemaining").toString())
                    myLoan.putString("loanReason", response.get("loanReason").toString())
                    bundle?.putBundle("myLoan", myLoan)*/
                    replaceFragment(my_loan(bundle), parentFragmentManager)
                } else if (response.get("status") == "already_exists") {
                    Toast.makeText(context, "Debes pagar el préstamo actual antes de solicitar" +
                            " uno nuevo.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "El préstamo no pudo ser procesado.",
                        Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error: VolleyError ->
                Toast.makeText(context,"Error al procesar el pŕestamo.", Toast.LENGTH_SHORT).show()
                println("Error al procesar el préstamo: ${error.message}")
            }
        )

        // Add request to queue
        queue.add(request)
    }

    // Function that allows us to validate the amount to be lend, the content and length of the field.
    fun validateField(): Boolean {
        if (fieldsValidator(context, editLendAmount, "onlyNumbers", 6,
            7, true)) {
            if (loanAmount < 200000 || loanAmount > 3000000) {
                Toast.makeText(context, "La cantidad a prestar tiene que estar entre $200,000" +
                        " y $3,000,000.", Toast.LENGTH_LONG).show()
                editLendAmount.requestFocus()
                return false
            } else if (loanAmount % 10000 != 0) {
                Toast.makeText(context, "La cantidad a prestar debe aumentar de 10,000 en 10,000",
                    Toast.LENGTH_LONG).show()
                editLendAmount.requestFocus()
                return false
            } else if (spinLendReason.selectedItemPosition == 0) {
                Toast.makeText(context, "Debes seleccionar el motivo del préstamo.",
                    Toast.LENGTH_LONG).show()
                return false
            } else {
                return true
            }
        }
        return false
    }
}
