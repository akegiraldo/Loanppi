package com.android.loanppi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import org.json.JSONArray
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 * Use the [main_worker.newInstance] factory method to
 * create an instance of this fragment.
 */
class main_worker(bundle: Bundle?) : Fragment() {
    private lateinit var accessInfo: Bundle
    private lateinit var account: Bundle
    private var myLoan = Bundle()
    private var bundle = bundle

    // Main worker values
    private var amountRemaining = 0.0f
    private var progressPercent = 0.0f
    private var amountPaid = 0.0f
    private lateinit var valueWeeklyFee: TextView
    private lateinit var valueFeeNumber: TextView
    private lateinit var valueAmountRemaining: TextView
    private lateinit var valueAmountPaid: TextView
    private lateinit var valueGoalAmount: TextView
    private lateinit var btnLetPay: Button
    private lateinit var progressBar: SeekBar
    private lateinit var gettingMoney: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_main_worker, container, false)

        accessInfo = bundle?.getBundle("accessInfo") as Bundle
        account = bundle?.getBundle("account") as Bundle
        myLoan.putString("status", "loading")
        bundle?.putBundle("myLoan", myLoan)
        getLoan()

        val firstName: String? = account.getString("firstName")
        btnLetPay = view.findViewById(R.id.btn_let_pay)
        valueAmountRemaining = view.findViewById(R.id.txt_main_value_amount_remaining)
        valueFeeNumber = view.findViewById(R.id.txt_main_value_your_fee_number)
        valueWeeklyFee = view.findViewById(R.id.txt_main_value_your_weekly_fee)
        valueAmountPaid = view.findViewById(R.id.txt_main_value_amount_paid)
        valueGoalAmount = view.findViewById(R.id.txt_main_value_goal_amount)
        progressBar = view.findViewById(R.id.bar_main_progress_moto_bar)
        gettingMoney = view.findViewById(R.id.txt_main_getting_money)

        var urlPhoto: String? = ""
        if (accessInfo.get("accessWith") == "facebook") {
            val facebookAccount = accessInfo.get("facebookAccount") as Bundle
            urlPhoto = facebookAccount.get("urlUserPhoto") as String
        } else {
            val googleAccount = accessInfo.get("googleAccount") as GoogleSignInAccount
            urlPhoto = googleAccount.photoUrl.toString()
        }

        view.findViewById<TextView>(R.id.txt_grettings).setText("Hola, " + firstName)
        Glide.with(this).load(urlPhoto).into(view.findViewById(R.id.img_user_photo))

        btnLetPay.setOnClickListener(View.OnClickListener {
            letPay()
        })

        return view
    }

    fun getLoan() {
        val id = account.get("userId")
        val url = "http://loanppi.kevingiraldo.tech/app/api/v1/my_loan?idWorker="+id
        val queue = Volley.newRequestQueue(context)

        // Request a JSON response from the provided URL.
        val request = JsonArrayRequest(Request.Method.GET, url, null,
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
                        loadMainInfo()
                        loadMainFields("show")
                    } else {
                        loadMainFields("hide")
                    }
                } else {
                    Toast.makeText(context, "No se encuentra ningún préstamo asociado al usuario.",
                        Toast.LENGTH_LONG).show()
                    myLoan.putString("status", "not_found")
                    loadMainFields("hide")
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

    fun loadMainInfo() {
        val valueToPayWeekly = myLoan.get("valueToPayWeekly").toString().toFloat()
        val goal = myLoan.get("totalToPay").toString().toFloat()

        amountPaid = myLoan.get("amountRemaining").toString().toFloat()
        amountRemaining = goal - amountPaid


        copFormat.maximumFractionDigits = 0

        progressBar.progress = progressPercent.toInt()
        valueGoalAmount.setText(copFormat.format(goal))
        valueWeeklyFee.setText(copFormat.format(valueToPayWeekly))
        valueFeeNumber.setText(myLoan.get("feeNumber").toString())
        if (myLoan.get("status") == "resolved") {
            btnLetPay.isVisible = true
            gettingMoney.isVisible = false
            progressPercent = (amountPaid / goal) * 100
            valueAmountPaid.setText(copFormat.format(amountPaid))
            valueAmountRemaining.setText(copFormat.format(amountRemaining))
        } else {
            progressPercent = 0.0f
            valueAmountRemaining.setText(copFormat.format(goal))
            gettingMoney.isVisible = true
            btnLetPay.isVisible = false
        }
    }

    fun letPay() {
        val url = "http://loanppi.kevingiraldo.tech/app/api/v1/payment/"
        val payment = JSONObject()

        var fee = myLoan.get("valueToPayWeekly").toString().toFloat()
        val goal = myLoan.get("totalToPay").toString().toFloat()
        val amountPaid = myLoan.get("amountRemaining").toString().toFloat()
        val amountRemaining = goal - amountPaid

        if (fee > amountRemaining && amountRemaining != 0.0f) {
            fee = amountRemaining
        }

        payment.put("idWorker", myLoan.get("idWorker"))
        payment.put("idNeed", myLoan.get("idNeed"))
        payment.put("payment", fee)

        val queue = Volley.newRequestQueue(context)
        val request = JsonObjectRequest(Request.Method.POST, url, payment,
            Response.Listener { response ->
                println("RESPONSE:" + response.toString())
                if (response.get("status") == "paid") {
                    Toast.makeText(context, "Pago realizado con éxito.", Toast.LENGTH_LONG).show()
                    getLoan()
                } else {
                    Toast.makeText(context, "Error, no se pudo procesar el pago.", Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error: VolleyError ->
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
                println("Error: ${error.message}")
            }
        )
        queue.add(request)
    }

    fun loadMainFields(visibility: String) {
        if (visibility == "show") {
            view?.findViewById<TextView>(R.id.txt_main_amount_remaining)?.isVisible = true
            view?.findViewById<TextView>(R.id.txt_main_your_weekly_fee)?.isVisible = true
            view?.findViewById<TextView>(R.id.txt_main_your_fee_number)?.isVisible = true
            view?.findViewById<TextView>(R.id.txt_main_goal_amount)?.isVisible = true
            view?.findViewById<TextView>(R.id.txt_my_loan_balance)?.isVisible = true
            view?.findViewById<TextView>(R.id.txt_is_empty)?.isVisible = false
            valueAmountRemaining.isVisible = true
            valueAmountPaid.isVisible = true
            valueGoalAmount.isVisible = true
            valueFeeNumber.isVisible = true
            valueWeeklyFee.isVisible = true
            progressBar.isVisible = true
        } else {
            view?.findViewById<TextView>(R.id.txt_main_amount_remaining)?.isVisible = false
            view?.findViewById<TextView>(R.id.txt_main_your_weekly_fee)?.isVisible = false
            view?.findViewById<TextView>(R.id.txt_main_your_fee_number)?.isVisible = false
            view?.findViewById<TextView>(R.id.txt_main_goal_amount)?.isVisible = false
            view?.findViewById<TextView>(R.id.txt_my_loan_balance)?.isVisible = false
            view?.findViewById<TextView>(R.id.txt_is_empty)?.isVisible = true
            valueAmountRemaining.isVisible = false
            valueAmountPaid.isVisible = false
            valueGoalAmount.isVisible = false
            valueFeeNumber.isVisible = false
            valueWeeklyFee.isVisible = false
            progressBar.isVisible = false
            btnLetPay.isVisible = false
        }
    }
}
