package com.android.loanppi

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import java.util.*

class dashboard : AppCompatActivity() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var gso: GoogleSignInOptions
    private lateinit var userType: String
    private lateinit var loginMethod: String

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val loginInfo: Bundle? = intent.extras

        userType = loginInfo?.get("userType") as String
        loginMethod = loginInfo.get("loginMethod") as String

        if (loginMethod == "google") {
            gso = loginInfo.get("gso") as GoogleSignInOptions
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        }

        loadFragment(profile(loginInfo))
        /*if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance()
                .logInWithReadPermissions(this, Arrays.asList("email"))
        }*/
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        if (userType == "worker") {
            inflater.inflate(R.menu.menu_worker, menu)
        } else {
            inflater.inflate(R.menu.menu_investor, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.menu_i_home -> { replaceFragment(main_investor(null)) ; true }
            R.id.menu_i_profile -> { replaceFragment(profile(null)) ; true }
            R.id.menu_i_invest -> { replaceFragment(invest()) ; true }
            R.id.menu_i_my_investment -> { replaceFragment(my_investment()) ; true }
            R.id.menu_i_history -> { true }
            R.id.menu_i_signout -> { signOut() ; true }

            R.id.menu_w_home -> { replaceFragment(main_worker()) ; true }
            R.id.menu_w_profile -> { replaceFragment(profile(null)) ; true }
            R.id.menu_w_lend -> { replaceFragment(lend()) ; true }
            R.id.menu_w_my_loan -> { replaceFragment(my_loan()) ; true }
            R.id.menu_w_history -> { true }
            R.id.menu_w_signout -> { signOut() ; true }
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

    fun onLend(view: View) { replaceFragment(lend()) }

    fun onMyLoan(view: View) { replaceFragment(my_loan()) }

    fun onInvest(view: View) { replaceFragment(invest()) }

    fun onMyInvestment(view: View) { replaceFragment(my_investment()) }

    private fun signOut() {
        if (loginMethod == "google")
            mGoogleSignInClient.signOut()
        if (AccessToken.getCurrentAccessToken() != null) {
            GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/",
                null, HttpMethod.DELETE, GraphRequest.Callback {
                    AccessToken.setCurrentAccessToken(null)
                    LoginManager.getInstance().logOut()
                }).executeAsync()
        }

        finish()
    }
}
