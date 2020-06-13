package com.android.loanppi

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class dashboard : AppCompatActivity() {
    private lateinit var userType: String
    private lateinit var accessWith: String
    private lateinit var accessFrom: String
    private var accessInfo: Bundle? = Bundle()
    private var account: Bundle? = Bundle()
    private var bundle: Bundle? = Bundle()
    private val myLoan = Bundle()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        bundle = intent.extras
        accessInfo = bundle?.getBundle("accessInfo") as Bundle
        myLoan.putString("status", "not_found")
        bundle?.putBundle("myLoan", myLoan)

        accessWith = accessInfo?.get("accessWith") as String
        accessFrom = accessInfo?.get("accessFrom") as String

        if (accessFrom == "login") {
            account = bundle?.getBundle("account") as Bundle
            userType = account?.get("userType").toString()
            userType = userType.substring(0, userType.length - 1)
        } else {
            userType = accessInfo?.get("userType") as String
        }

        if (accessFrom == "signup") {
            loadFragment(profile(bundle))
        } else {
            if (userType == "investor")
                loadFragment(main_investor(bundle))
            else {
                getLoan()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        if (accessFrom == "signup") {
            inflater.inflate(R.menu.menu_non_registered, menu)
        } else {
            if (userType == "worker") {
                inflater.inflate(R.menu.menu_worker, menu)
            } else {
                inflater.inflate(R.menu.menu_investor, menu)
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.menu_i_home -> { replaceFragment(main_investor(bundle)) ; true }
            R.id.menu_i_profile -> { replaceFragment(profile(bundle)) ; true }
            R.id.menu_i_invest -> { replaceFragment(invest_options(account)) ; true }
            R.id.menu_i_my_investment -> { replaceFragment(my_investment_options(bundle)) ; true }
            R.id.menu_i_history -> { true }

            R.id.menu_w_home -> { replaceFragment(main_worker(bundle)) ; true }
            R.id.menu_w_profile -> { replaceFragment(profile(bundle)) ; true }
            R.id.menu_w_lend -> { replaceFragment(lend(bundle)) ; true }
            R.id.menu_w_my_loan -> { replaceFragment(my_loan(bundle)) ; true }
            R.id.menu_w_history -> { true }

            R.id.menu_signout -> { signOut() ; true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun loadFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.dashboard_container, fragment)
        fragmentTransaction.commit()
    }

    fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.dashboard_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    fun onLend(view: View) {
        if (myLoan.get("status") == "not_found") {
            replaceFragment(lend(bundle))
        } else {
            Toast.makeText(this, "Ya tienes un préstamo activo", Toast.LENGTH_LONG).show()
        }
    }

    fun onMyLoan(view: View) {
        if (myLoan.get("status") != "not_found") {
            replaceFragment(my_loan(bundle))
        } else {
            Toast.makeText(this, "Debes solicitar un préstamo primero", Toast.LENGTH_LONG).show()
        }
    }

    fun onInvest(view: View) {
        if (account?.get("investStack").toString().toFloat() >= 50000) {
            replaceFragment(invest_options(account))
        }
    }

    fun onMyInvestment(view: View) { replaceFragment(my_investment_options(bundle)) }

    private fun signOut() {
        if (accessWith == "google") {
            val gso = accessInfo?.get("gso") as GoogleSignInOptions
            val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
            mGoogleSignInClient.signOut()
        } else if (AccessToken.getCurrentAccessToken() != null) {
            GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/",
                null, HttpMethod.DELETE, GraphRequest.Callback {
                    AccessToken.setCurrentAccessToken(null)
                    LoginManager.getInstance().logOut()
                }).executeAsync()
        }

        finish()
    }

    fun getLoan() {
        val id = account?.get("userId")
        val url = "http://loanppi.kevingiraldo.tech/app/api/v1/active_loan?idWorker="+id
        val queue = Volley.newRequestQueue(this)

        // Request a JSON response from the provided URL.
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                if (response.length() > 0) {
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
                    loadFragment(main_worker(bundle))
                } else {
                    /*Toast.makeText(this, "No se encuentra ningún préstamo asociado al usuario.",
                        Toast.LENGTH_LONG).show()*/
                    loadFragment(main_worker(bundle))
                }
            },
            Response.ErrorListener {
                Toast.makeText(this, "Error en la consulta", Toast.LENGTH_LONG).show()
                println("ERROR CONSULTA: " + it.toString())
            })

        // Add the request to the RequestQueue.
        queue.add(request)
    }
}
