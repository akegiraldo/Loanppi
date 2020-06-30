package com.android.loanppi

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView


class dashboard : AppCompatActivity() {
    // Data from login
    private lateinit var userType: String
    private lateinit var accessWith: String
    private lateinit var accessFrom: String
    private var accessInfo: Bundle? = Bundle()
    private var account: Bundle? = Bundle()
    private var bundle: Bundle? = Bundle()

    private lateinit var dashMenuBarContainer: BottomNavigationView

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Get info from login
        bundle = intent.extras
        accessInfo = bundle?.getBundle("accessInfo") as Bundle

        accessWith = accessInfo?.get("accessWith") as String
        accessFrom = accessInfo?.get("accessFrom") as String

        // Update info depending of access method
        if (accessFrom == "login") {
            account = bundle?.getBundle("account") as Bundle
            userType = account?.get("userType").toString()
            userType = userType.substring(0, userType.length - 1)
        } else {
            userType = accessInfo?.get("userType") as String
        }

        dashMenuBarContainer = findViewById(R.id.dash_menu_bar_container)

        // Decide fragment to load depending of access from
        if (accessFrom == "signup") {
            loadFragment(profile(bundle))
            findViewById<BottomNavigationView>(R.id.dash_menu_bar_container).visibility = View.INVISIBLE
            findViewById<BottomNavigationView>(R.id.dash_menu_bar_container).isEnabled = false
        } else {
            if (userType == "worker") {
                replaceFragment(main_worker(bundle), "main_worker")
                dashMenuBarContainer.inflateMenu(R.menu.worker_dash_bar)
            } else {
                replaceFragment(main_investor(bundle), "main_investor")
                dashMenuBarContainer.inflateMenu(R.menu.investor_dash_bar)
            }
        }

        dashMenuBarContainer.setOnNavigationItemSelectedListener(barListener)

        // Handle back button pressed
        onBackPressedDispatcher.addCallback(this) {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.dashboard_container)
                ?.javaClass?.name.toString().replace("com.android.loanppi.", "")

            if (userType == "investor") {
                if (currentFragment == "invest_options" || currentFragment == "my_investment_options"
                    || currentFragment == "profile") {
                    replaceFragment(main_investor(bundle), "main_investor")
                    dashMenuBarContainer.selectedItemId = R.id.nav_i_home
                } else {
                    if (currentFragment == "invest_details") {
                        replaceFragment(invest_options(bundle), "invest_options")
                        dashMenuBarContainer.selectedItemId = R.id.nav_i_invest
                    } else if (currentFragment == "my_investment_details") {
                        replaceFragment(my_investment_options(bundle), "my_investment_options")
                        dashMenuBarContainer.selectedItemId = R.id.nav_i_my_investments
                    }
                }
            } else {
                if (currentFragment == "lend" || currentFragment == "my_loan"
                    || currentFragment == "profile") {
                    replaceFragment(main_worker(bundle), "main_worker")
                    dashMenuBarContainer.selectedItemId = R.id.nav_w_home
                }
            }
        }
    }

    // Load fragment from dashboard bar depending of user role
    private val barListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        var newFragment: Fragment? = null
        var tag = ""
        when (item.itemId) {
            R.id.nav_i_home -> { newFragment = main_investor(bundle); tag = "main_investor" }
            R.id.nav_i_invest -> { newFragment = invest_options(bundle); tag = "invest_options" }
            R.id.nav_i_my_investments -> { newFragment = my_investment_options(bundle); tag = "my_investment_options" }
            R.id.nav_i_profile -> { newFragment = profile(bundle); tag = "profile" }

            R.id.nav_w_home -> { newFragment = main_worker(bundle); tag = "main_worker" }
            R.id.nav_w_lend -> { newFragment = lend(bundle); tag = "lend" }
            R.id.nav_w_my_loan -> { newFragment = my_loan(bundle); tag = "my_loan" }
            R.id.nav_w_profile -> { newFragment = profile(bundle); tag = "profile" }
        }

        if (newFragment != null) {
            replaceFragment(newFragment, tag)
        }

        true
    }

    // Function that create a menu depending of access from
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        if (accessFrom == "signup") {
            inflater.inflate(R.menu.menu_non_registered, menu)
        } else {
            inflater.inflate(R.menu.menu_registered, menu)
        }
        return true
    }

    // Function that allows know menu item selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.menu_signout -> { signOut() ; true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Function that load a fragment on container
    fun loadFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.dashboard_container, fragment)
        fragmentTransaction.commit()
    }

    // Function that replace the current fragment for other fragment
    fun replaceFragment(fragment: Fragment, tag: String) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.dashboard_container, fragment, tag)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    // Function to finish the current session access method
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
