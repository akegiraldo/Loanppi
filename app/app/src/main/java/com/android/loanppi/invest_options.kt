package com.android.loanppi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_invest_options.*
import org.json.JSONArray
import java.text.NumberFormat

/**
 * A simple [Fragment] subclass.
 * Use the [invest_options.newInstance] factory method to
 * create an instance of this fragment.
 */
class invest_options(bundle: Bundle?) : Fragment() {
    // Account
    private var account = bundle

    // Cards views
    private var card_1: View? = null
    private var card_2: View? = null
    private var card_3: View? = null

    // Cards info
    private val bundle_card_1 = Bundle()
    private val bundle_card_2 = Bundle()
    private val bundle_card_3 = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_invest_options, container, false)

        card_1 = view.findViewById(R.id.card_1)
        card_2 = view.findViewById(R.id.card_2)
        card_3 = view.findViewById(R.id.card_3)

        getInvestOptions()

        card_1?.setOnClickListener(View.OnClickListener {
            replaceFragment(invest_details(bundle_card_1, account))
        })
        card_2?.setOnClickListener(View.OnClickListener {
            replaceFragment(invest_details(bundle_card_2, account))
        })
        card_3?.setOnClickListener(View.OnClickListener {
            replaceFragment(invest_details(bundle_card_3, account))
        })

        return view
    }

    fun getInvestOptions() {
        val url = "http://loanppi.kevingiraldo.tech/app/api/v1/invest_options"
        val queue = Volley.newRequestQueue(context)

        // Request a JSON response from the provided URL.
        val request = JsonArrayRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                //println("RESPONSE options: " + response.toString() + " " + response[0].toString())
                var investStack = account?.get("investStack").toString().toInt()
                var amountRemaining = 0
                if (response.length() > 0) {
                    amountRemaining = response.getJSONObject(0).getString("amountRemaining").toInt()
                    if (investStack > amountRemaining) { investStack = amountRemaining }
                    bundle_card_1.putString("investStack", investStack.toString())
                    bundle_card_1.putString("idNeed", response.getJSONObject(0).getString("idNeed"))
                    bundle_card_1.putString("amountRemaining", response.getJSONObject(0).getString("amountRemaining"))
                    bundle_card_1.putString("timeToPay", response.getJSONObject(0).getString("timeToPay"))
                    if (response.length() > 1) {
                        amountRemaining = response.getJSONObject(1).getString("amountRemaining").toInt()
                        if (investStack > amountRemaining) { investStack = amountRemaining }
                        bundle_card_2.putString("idNeed", response.getJSONObject(1).getString("idNeed"))
                        bundle_card_2.putString("investStack", investStack.toString())
                        bundle_card_2.putString("amountRemaining", response.getJSONObject(1).getString("amountRemaining"))
                        bundle_card_2.putString("timeToPay", response.getJSONObject(1).getString("timeToPay"))
                        if (response.length() > 2) {
                            amountRemaining = response.getJSONObject(2).getString("amountRemaining").toInt()
                            if (investStack > amountRemaining) { investStack = amountRemaining }
                            bundle_card_3.putString("idNeed", response.getJSONObject(2).getString("idNeed"))
                            bundle_card_3.putString("investStack", investStack.toString())
                            bundle_card_3.putString("amountRemaining", response.getJSONObject(2).getString("amountRemaining"))
                            bundle_card_3.putString("timeToPay", response.getJSONObject(2).getString("timeToPay"))
                        }
                    }
                    loadInvestOptions()
                } else {
                    Toast.makeText(context, "No se encuentran inversiones disponibles.",
                        Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener {
                println("Error al cargar las opciones: " + it.toString())
                Toast.makeText(context, "Error al cargar las opciones", Toast.LENGTH_LONG).show()
            })

        // Add the request to the RequestQueue.
        queue.add(request)
    }

    fun loadInvestOptions() {
        val copFormat: NumberFormat = NumberFormat.getCurrencyInstance()
        copFormat.maximumFractionDigits = 0
        if (bundle_card_1 != null) {
            card_1?.isVisible = true
            txt_card_1_value_money_to_invest.setText(copFormat.format(
                bundle_card_1.getString("investStack")?.toInt()))
            txt_card_1_value_amount_remaining.setText(copFormat.format(
                bundle_card_1.getString("amountRemaining")?.toInt()))
            txt_card_1_value_return_time.setText(bundle_card_1.get("timeToPay").toString() + " meses")
        }
        /*if (bundle_card_2 != null) {
            card_2?.isVisible = true
            txt_card_2_value_money_to_invest.setText(copFormat.format(
                bundle_card_2.getString("investStack")?.toInt()))
            txt_card_2_value_amount_remaining.setText(copFormat.format(
                bundle_card_2.getString("amountRemaining")?.toInt()))
            txt_card_2_value_return_time.setTe0xt(bundle_card_2.get("timeToPay").toString() + " meses")
        }
        if (bundle_card_3 != null) {
            card_2?.isVisible = true
            txt_card_3_value_money_to_invest.setText(copFormat.format(
                bundle_card_3.getString("investStack")?.toInt()))
            txt_card_3_value_amount_remaining.setText(copFormat.format(
                bundle_card_3.getString("amountRemaining")?.toInt()))
            txt_card_3_value_return_time.setText(bundle_card_3.get("timeToPay").toString() + " meses")
        }*/
    }

    fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.dashboard_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}