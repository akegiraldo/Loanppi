package com.android.loanppi

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.android.synthetic.main.fragment_main_investor.*
import org.json.JSONArray
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 * Use the [main_investor.newInstance] factory method to
 * create an instance of this fragment.
 */
class main_investor(bundle: Bundle?) : Fragment() {
    // User account info
    private lateinit var accessInfo: Bundle
    private lateinit var account: Bundle
    private var bundle: Bundle? = bundle

    // Main investor fields
    private lateinit var valueFullInvestedAmount: TextView
    private lateinit var valueFullEarns: TextView
    private var fullInvestedAmount = 0
    private var fullEarns = 0.0f

    // Bar chart
    private lateinit var barChart: HorizontalBarChart
    private lateinit var values: Array<Int>
    private var titles: Array<String> = arrayOf("Inversión", "Retorno")
    private var colors: ArrayList<Int> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_main_investor, container, false)

        accessInfo = bundle?.getBundle("accessInfo") as Bundle
        account = bundle?.getBundle("account") as Bundle

        // Get textviews and edittext from fragment
        valueFullInvestedAmount = view.findViewById(R.id.txt_main_value_full_invested_amount)
        valueFullEarns = view.findViewById(R.id.txt_main_value_full_earns)

        barChart = view.findViewById(R.id.bar_chart)
        colors = arrayListOf(resources.getColor(R.color.loanppi_fresh), resources.getColor(R.color.yellow_fresh))

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

        view.findViewById<TextView>(R.id.txt_grettings).setText("Hola, "+ firstName)
        Glide.with(this).load(urlPhoto).into(view.findViewById(R.id.img_user_photo))

        // Calls the function responsible for checking whether the user has currently
        // active investments
        getInvestments()

        return view
    }

    // Function that loads a chart
    fun setChart(chart: HorizontalBarChart, description: String, textColor: Int, background: Int, animateY: Int): HorizontalBarChart {
        chart.description.text = description
        chart.description.textSize = 15F
        chart.description.textColor = textColor
        chart.setBackgroundColor(background)
        chart.animateY(animateY)

        return chart
    }

    // Function that configure the legend
    fun legend(chart: HorizontalBarChart) {
        val legend: Legend = chart.legend

        legend.form = Legend.LegendForm.CIRCLE
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER

        val entries: ArrayList<LegendEntry> = ArrayList()
        for (i in 0..titles.size - 1) {
            val entry = LegendEntry()
            entry.formColor = colors[i]
            entry.label = titles[i]
            entries.add(entry)
        }
        legend.setCustom(entries)
    }

    // Function that get entries
    fun getEntries(): ArrayList<BarEntry> {
        val entries: ArrayList<BarEntry> = ArrayList()
        for (i in 0..values.size - 1) {
            entries.add(BarEntry(i.toFloat(), values[i].toFloat()))
        }
        return entries
    }

    // Function to set the axes
    fun axisX(axis: XAxis) {
        axis.isGranularityEnabled = true
        axis.position = XAxis.XAxisPosition.BOTTOM
        axis.valueFormatter = IndexAxisValueFormatter(titles)
        axis.isEnabled = false
    }
    fun axisLeft(axis: YAxis) {
        axis.spaceTop = 30F
        axis.axisMinimum = 0F
    }
    fun axisRigth(axis: YAxis) {
        axis.isEnabled = false
    }

    // Function that creates a chart
    fun createChart() {
        barChart = setChart(barChart, "", Color.BLACK,
            resources.getColor(R.color.gray_lite), 3000)
        legend(barChart)
        barChart.setDrawGridBackground(true)
        barChart.setDrawBarShadow(true)
        barChart.data = getBarData()
        barChart.invalidate()

        axisX(barChart.xAxis)
        axisLeft(barChart.axisLeft)
        axisRigth(barChart.axisRight)
    }
    fun getData(): BarDataSet {
        val dataSet = BarDataSet(getEntries(), "")
        dataSet.colors = colors
        dataSet.valueTextColor = Color.BLACK
        dataSet.barShadowColor = Color.TRANSPARENT
        dataSet.valueTextSize = 10F

        return dataSet
    }
    fun getBarData(): BarData {
        val barData = BarData(getData())
        barData.barWidth = 0.45F
        return barData
    }

    // Function that makes the request to the server to get user investments
    fun getInvestments() {
        val id = account.get("userId")
        val url = "http://loanppi.kevingiraldo.tech/app/api/v1/my_investments?idInvestor=" + id
        val queue = Volley.newRequestQueue(context)

        // Request a JSON response from the provided URL.
        // Create the request. If you have any active investments, it calculates information on
        // all investments and calls the function responsible for displaying this data. Otherwise,
        // it calls the function in charge of showing a message encouraging to invest
        val request = JsonArrayRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                if (response.length() > 0) {
                    for (i in 0..(response.length() - 1)) {
                        fullInvestedAmount += response.getJSONObject(i).get("moneyInvestment").toString().toInt()
                        fullEarns += response.getJSONObject(i).get("totalReturn").toString().toFloat()
                    }
                    loadMainInfo("show")
                    values = arrayOf(fullInvestedAmount, fullEarns.toInt())
                    createChart()
                } else {
                    loadMainInfo("hide")
                }
            },
            Response.ErrorListener {
                println("Error al cargar inversiones: " + it.toString())
                Toast.makeText(context, "Error al intentar cargar las inversiones.",
                    Toast.LENGTH_SHORT).show()
            })

        // Add the request to the RequestQueue.
        queue.add(request)
    }

    // Loads the information obtained to the user's main fragment depending on whether or not he
    // has at least one active investment
    fun loadMainInfo(status: String) {
        if (status == "hide") {
            view?.findViewById<TextView>(R.id.txt_is_empty)?.isVisible = true
            view?.findViewById<TextView>(R.id.txt_is_not_empty)?.isVisible = false
            view?.findViewById<TextView>(R.id.txt_main_full_earns)?.isVisible = false
            view?.findViewById<TextView>(R.id.txt_main_full_invested_amount)?.isVisible = false
            view?.findViewById<TextView>(R.id.txt_main_sub_earns)?.isVisible = false
            barChart.isVisible = false
            valueFullEarns.isVisible = false
            valueFullInvestedAmount.isVisible = false
        } else {
            view?.findViewById<TextView>(R.id.txt_is_empty)?.isVisible = false
            view?.findViewById<TextView>(R.id.txt_is_not_empty)?.isVisible = true
            view?.findViewById<TextView>(R.id.txt_main_full_earns)?.isVisible = true
            view?.findViewById<TextView>(R.id.txt_main_full_invested_amount)?.isVisible = true
            view?.findViewById<TextView>(R.id.txt_main_sub_earns)?.isVisible = true
            barChart.isVisible = true
            valueFullEarns.isVisible = true
            valueFullInvestedAmount.isVisible = true
        }
        copFormat.maximumFractionDigits = 0
        valueFullInvestedAmount.setText(copFormat.format(fullInvestedAmount))
        valueFullEarns.setText(copFormat.format(fullEarns))
    }
}
