package com.android.loanppi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import java.text.NumberFormat


/**
 * A simple [Fragment] subclass.
 * Use the [my_investment_options.newInstance] factory method to
 * create an instance of this fragment.
 */
class my_investment_options(bundle: Bundle?) : Fragment() {
    private val bundle = bundle
    private lateinit var account: Bundle
    private lateinit var listView: ListView

    private lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var investmentData: ArrayList<String>
    private lateinit var investmentsListString: ArrayList<String>
    private lateinit var investmentsListArray: ArrayList<ArrayList<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_investment_options, container, false)

        listView = view.findViewById(R.id.listview)

        // Initializes all array lists and the listview adapter
        investmentData = ArrayList()
        investmentsListString = ArrayList()
        investmentsListArray = ArrayList()
        arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, investmentsListString)
        listView.adapter = arrayAdapter

        account = bundle?.getBundle("account") as Bundle

        // Listen when a specific investment is selected from the list and call the function in
        // charge of loading that investment in detail
        listView.setOnItemClickListener(object : OnItemClickListener {
            val investmentSelected = Bundle()
            override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                investmentSelected.putString("idInvestment", investmentsListArray.get(position).get(2))
                replaceFragment(my_investment_details(investmentSelected), parentFragmentManager)
            }
        })

        // Call function that get list of user investments
        getInvestments()

        return view
    }

    // Function that makes the request to the server to get a list of my investments
    fun getInvestments() {
        val id = account.get("userId")
        val url = "http://loanppi.kevingiraldo.tech/app/api/v1/my_investments?idInvestor=" + id
        val queue = Volley.newRequestQueue(context)

        // Request a JSON response from the provided URL.
        // Create the request. In case of success save the values in a variable, then in the list of
        // investments and notifies the view adapter to show the user each change. On the contrary,
        // report that don't have active investments.
        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                if (response.length() > 0) {
                    for (i in 0..(response.length() - 1)) {
                        investmentData.add((i + 1).toString())
                        investmentData.add(response.getJSONObject(i).get("moneyInvestment").toString())
                        investmentData.add(response.getJSONObject(i).get("idInvestment").toString())
                        investmentData.add(response.getJSONObject(i).get("timeToReturn").toString())
                        investmentData.add(response.getJSONObject(i).get("returnTotal").toString())
                        investmentData.add(response.getJSONObject(i).get("valueToReturnWeekly").toString())
                        investmentData.add(response.getJSONObject(i).get("interestsWins").toString())
                        investmentData.add(response.getJSONObject(i).get("totalReturn").toString())
                        investmentsListString.add(getStringFromArray(investmentData))
                        investmentsListArray.add(investmentData.clone() as ArrayList<String>)
                        investmentData.clear()
                        arrayAdapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(context, "No se encuentran inversiones asociadas al usuario.",
                        Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener {
                println("Error al cargar inversiones: " + it.toString())
                Toast.makeText(context, "Error en la conexión con el servidor.", Toast.LENGTH_SHORT).show()
            })

        // Add the request to the RequestQueue.
        queue.add(request)
    }

    // It obtains a list of investments and returns this information in a string
    fun getStringFromArray(array: ArrayList<String>): String {
        val copFormat: NumberFormat = NumberFormat.getCurrencyInstance()
        copFormat.maximumFractionDigits = 0
        var string = array.get(0)
        for (i in array.get(0).length..15) { string += " " }
        string += copFormat.format(array.get(1).toInt())
        for (i in array.get(7).length..30) { string += " " }
        string += copFormat.format(array.get(7).toInt())
        return string
    }
}