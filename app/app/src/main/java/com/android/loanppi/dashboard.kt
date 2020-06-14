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

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        bundle = intent.extras
        accessInfo = bundle?.getBundle("accessInfo") as Bundle

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
                loadFragment(main_worker(bundle))
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
}
