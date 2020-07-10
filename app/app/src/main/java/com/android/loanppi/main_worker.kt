package com.android.loanppi

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.collection.arrayMapOf
import androidx.core.view.isVisible
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.android.synthetic.main.dialog_payment.*
import org.json.JSONArray
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 * Use the [main_worker.newInstance] factory method to
 * create an instance of this fragment.
 */
class main_worker(bundle: Bundle?) : Fragment() {
    // User account info
    private lateinit var accessInfo: Bundle
    private lateinit var account: Bundle
    private var bundle = bundle

    private var myLoan = Bundle()

    // Main worker values
    private var valueToPayWeekly = 0.0f
    private var amountRemaining = 0.0f
    private var progressPercent = 0.0f
    private var amountPaid = 0.0f
    private lateinit var valueWeeklyFee: TextView
    private lateinit var valueFeeNumber: TextView
    private lateinit var valueAmountRemaining: TextView
    private lateinit var valueAmountPaid: TextView
    private lateinit var valueGoalAmount: TextView
    private lateinit var btnOpenPayment: Button
    private lateinit var progressBar: SeekBar
    private lateinit var gettingMoney: TextView

    // Payment fields
    private lateinit var editPaymentAmount: EditText
    private lateinit var paymentBtnLetPay: Button
    private lateinit var paymentValueWeeklyFee: TextView
    private lateinit var paymentValueAmountRemaining: TextView
    private lateinit var paymentValueAmountPaid: TextView

    private var paymentValueAmount: Float = 0.0f
    private lateinit var dialogLayout: View

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

        // Calls the function responsible for checking whether the user has currently
        // active loans
        getLoan("main")

        // Get textviews and edittext from fragment
        btnOpenPayment = view.findViewById(R.id.btn_open_payment)
        valueAmountRemaining = view.findViewById(R.id.txt_main_value_amount_remaining)
        valueFeeNumber = view.findViewById(R.id.txt_main_value_your_fee_number)
        valueWeeklyFee = view.findViewById(R.id.txt_main_value_your_weekly_fee)
        valueAmountPaid = view.findViewById(R.id.txt_main_value_amount_paid)
        valueGoalAmount = view.findViewById(R.id.txt_main_value_goal_amount)
        progressBar = view.findViewById(R.id.bar_main_progress_moto_bar)
        gettingMoney = view.findViewById(R.id.txt_main_getting_money)

        // Loads the photo and the first name of the user
        val firstName: String? = account.getString("firstName")
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

        // Listen when pay button is clicked and call function that show payment window
        btnOpenPayment.setOnClickListener(View.OnClickListener {
            showPaymentWindow()
        })

        return view
    }

    // Function that makes the request to the server to get user loan
    fun getLoan(from: String) {
        val id = account.get("userId")
        val url = "http://loanppi.kevingiraldo.tech/app/api/v1/my_loan?idWorker="+id
        val queue = Volley.newRequestQueue(context)

        // Request a JSON response from the provided URL.
        // Create the request. If you have any active loans calculates information about it and calls
        // the function responsible for displaying this data. Otherwise, it calls the function in
        // charge of showing a message encouraging to lend
        val request = JsonArrayRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
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
                    if (from == "payment") {
                        loadPaymentInfo("unpaid")
                    }
                } else {
                    myLoan.putString("status", "not_found")
                    loadMainFields("hide")
                    if (from == "payment") {
                        loadPaymentInfo("paid")
                    }
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

    // Loads the information obtained to the user's main fragment depending on whether or not he
    // has an active loan
    fun loadMainInfo() {
        valueToPayWeekly = myLoan.get("valueToPayWeekly").toString().toFloat()
        val goal = myLoan.get("totalToPay").toString().toFloat()

        amountPaid = myLoan.get("amountRemaining").toString().toFloat()
        amountRemaining = goal - amountPaid

        copFormat.maximumFractionDigits = 0

        valueGoalAmount.setText(copFormat.format(goal))
        valueWeeklyFee.setText(copFormat.format(valueToPayWeekly))
        valueFeeNumber.setText(myLoan.get("feeNumber").toString())
        if (myLoan.get("status") == "resolved") {
            btnOpenPayment.isVisible = true
            btnOpenPayment.isEnabled = true
            gettingMoney.isVisible = false
            progressPercent = (amountPaid / goal) * 100
            valueAmountPaid.setText(copFormat.format(amountPaid))
            valueAmountRemaining.setText(copFormat.format(amountRemaining))
        } else {
            progressPercent = 0.0f
            valueAmountRemaining.setText(copFormat.format(goal))
            gettingMoney.isVisible = true
            btnOpenPayment.isVisible = false
            btnOpenPayment.isEnabled = false
        }
        progressBar.progress = progressPercent.toInt()
    }

    fun showPaymentWindow() {
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        val inflater = layoutInflater
        dialogLayout = inflater.inflate(R.layout.dialog_payment, null)
        editPaymentAmount  = dialogLayout.findViewById(R.id.edit_pay_amount)

        paymentBtnLetPay = dialogLayout.findViewById(R.id.btn_let_pay)
        paymentValueWeeklyFee = dialogLayout.findViewById(R.id.txt_payment_value_your_weekly_fee)
        paymentValueAmountRemaining = dialogLayout.findViewById(R.id.txt_payment_value_amount_remaining)
        paymentValueAmountPaid = dialogLayout.findViewById(R.id.txt_payment_value_amount_paid)

        loadPaymentInfo("unpaid")

        if (valueToPayWeekly > amountRemaining && amountRemaining > 0.0f) {
            editPaymentAmount.setText(amountRemaining.toInt().toString())
        } else {
            editPaymentAmount.setText(valueToPayWeekly.toInt().toString())
        }

        builder.setView(dialogLayout).setNegativeButton(R.string.cancel) { dialog, which -> }
        builder.show()

        // Listen when the pay button is clicked, ask to validate the content of the field and
        // call the function in charge of making the payment
        paymentBtnLetPay.setOnClickListener(View.OnClickListener {
            if (validateFields()) {
                letPay()
            }
        })
    }

    // Function that validate the content of the field and return true ir all its okay or false
    // in other case
    fun validateFields(): Boolean {
        paymentValueAmount = editPaymentAmount.text.toString().toFloat()
        if (fieldsValidator(context, editPaymentAmount, "onlyNumbers", 4,
                7, true)) {
            val diference = amountRemaining - paymentValueAmount
            if (diference > -1 && diference < 1) {
                paymentValueAmount = amountRemaining
                return true
            } else if (diference < -1) {
                Toast.makeText(context, "El valor a pagar no puede ser superior a la cantidad" +
                        " restante.", Toast.LENGTH_LONG).show()
            } else if (diference > 1 && diference < 5000) {
                Toast.makeText(context, "No puedes dejar una deuda inferior a 5000.",
                    Toast.LENGTH_LONG).show()
                editPaymentAmount.setText(amountRemaining.toInt().toString())
            } else {
                return true
            }
        }
        return false
    }

    // Loads the loan information into the payment window
    fun loadPaymentInfo(status: String) {
        if (status != "paid") {
            paymentValueAmountPaid.setText(copFormat.format(amountPaid))
            paymentValueAmountRemaining.setText(copFormat.format(amountRemaining))
            paymentValueWeeklyFee.setText(copFormat.format(valueToPayWeekly))
        } else {
            dialogLayout.findViewById<TextView>(R.id.payment_title)?.isVisible = false
            dialogLayout.findViewById<TextView>(R.id.txt_payment_amount_paid)?.isVisible = false
            dialogLayout.findViewById<TextView>(R.id.txt_payment_amount_remaining)?.isVisible = false
            dialogLayout.findViewById<TextView>(R.id.txt_payment_your_weekly_fee)?.isVisible = false
            dialogLayout.findViewById<TextView>(R.id.txt_pay_amount)?.isVisible = false
            dialogLayout.findViewById<TextView>(R.id.txt_loan_paid)?.isVisible = true
            paymentValueAmountPaid.isVisible = false
            paymentValueAmountRemaining.isVisible = false
            paymentValueWeeklyFee.isVisible = false
            editPaymentAmount.isVisible = false
            paymentBtnLetPay.isVisible = false
        }
    }

    // Function that makes the request to the server to register a new pay
    fun letPay() {
        val url = "http://loanppi.kevingiraldo.tech/app/api/v1/payment/"
        val payment = JSONObject()

        payment.put("idWorker", myLoan.get("idWorker"))
        payment.put("idNeed", myLoan.get("idNeed"))
        payment.put("payment", paymentValueAmount)

        // Create the request. In case of success call the function that gets the current loan.
        // On the contrary, report the error.
        val queue = Volley.newRequestQueue(context)
        val request = JsonObjectRequest(Request.Method.POST, url, payment,
            Response.Listener { response ->
                if (response.get("status") == "paid") {
                    Toast.makeText(context, "Pago realizado con éxito.", Toast.LENGTH_SHORT).show()
                    getLoan("payment")
                } else {
                    Toast.makeText(context, "No se pudo procesar el pago.", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error: VolleyError ->
                Toast.makeText(context, "Error, en la conexión con el servidor",
                    Toast.LENGTH_SHORT).show()
                println("Error: ${error.message}")
            }
        )
        queue.add(request)
    }

    // Loads the fields to the user's main fragment depending on whether or not he
    // has at least one active loan
    fun loadMainFields(visibility: String) {
        if (visibility == "show") {
            view?.findViewById<TextView>(R.id.txt_main_amount_remaining)?.isVisible = true
            view?.findViewById<TextView>(R.id.txt_main_your_weekly_fee)?.isVisible = true
            view?.findViewById<TextView>(R.id.txt_main_your_fee_number)?.isVisible = true
            view?.findViewById<TextView>(R.id.txt_main_goal_amount)?.isVisible = true
            view?.findViewById<TextView>(R.id.txt_my_loan_balance)?.isVisible = true
            view?.findViewById<ImageView>(R.id.img_main_goal_flag)?.isVisible = true
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
            view?.findViewById<ImageView>(R.id.img_main_goal_flag)?.isVisible = false
            view?.findViewById<TextView>(R.id.txt_is_empty)?.isVisible = true
            valueAmountRemaining.isVisible = false
            valueAmountPaid.isVisible = false
            valueGoalAmount.isVisible = false
            valueFeeNumber.isVisible = false
            valueWeeklyFee.isVisible = false
            progressBar.isVisible = false
            btnOpenPayment.isVisible = false
        }
    }
}
