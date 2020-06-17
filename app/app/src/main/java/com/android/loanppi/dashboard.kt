package com.android.loanppi

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Constraints
import androidx.core.view.isVisible
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

    // Toggle color on selected fragment
    private lateinit var currentIcon: TextView
    private lateinit var currentText: TextView

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val iconManager = icon_manager()

        findViewById<TextView>(R.id.bar_icon_home).setTypeface(iconManager
            .get_icons<Typeface>("icomoon.ttf", this))
        findViewById<TextView>(R.id.bar_icon_deal).setTypeface(iconManager
            .get_icons<Typeface>("icomoon.ttf", this))
        findViewById<TextView>(R.id.bar_icon_my_business).setTypeface(iconManager
            .get_icons<Typeface>("icomoon.ttf", this))
        findViewById<TextView>(R.id.bar_icon_profile).setTypeface(iconManager
            .get_icons<Typeface>("icomoon.ttf", this))


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

        if (userType == "worker") {
            findViewById<TextView>(R.id.bar_icon_deal).setText(getString(R.string.icon_lend))
            findViewById<TextView>(R.id.bar_txt_deal).setText(getString(R.string.txt_lend))
            findViewById<TextView>(R.id.bar_icon_my_business).setText(getString(R.string.icon_my_loan))
            findViewById<TextView>(R.id.bar_txt_my_business).setText(getString(R.string.txt_my_loan))
        } else {
            findViewById<TextView>(R.id.bar_icon_deal).setText(getString(R.string.icon_invest))
            findViewById<TextView>(R.id.bar_txt_deal).setText(getString(R.string.txt_invest))
            findViewById<TextView>(R.id.bar_icon_my_business).setText(getString(R.string.icon_my_investments))
            findViewById<TextView>(R.id.bar_txt_my_business).setText(getString(R.string.txt_my_investments))
        }

        currentIcon = findViewById(R.id.bar_icon_home)
        currentText = findViewById(R.id.bar_txt_home)
        toggleColor(currentIcon, currentText)

        if (accessFrom == "signup") {
            loadFragment(profile(bundle))
            findViewById<ConstraintLayout>(R.id.dash_menu_bar).visibility = View.INVISIBLE
            findViewById<ConstraintLayout>(R.id.dash_menu_bar).isEnabled = false
        } else {
            selectFragment("main")
        }

        // Load main
        findViewById<TextView>(R.id.bar_icon_home).setOnClickListener(View.OnClickListener {
            /*val myFragment: MyFragment =
            fragmentManager.findFragmentByTag("MY_FRAGMENT") as MyFragment
            if ()*/
            selectFragment("main")
            toggleColor(findViewById(R.id.bar_icon_home), findViewById(R.id.bar_txt_home))
        })
        findViewById<TextView>(R.id.bar_txt_home).setOnClickListener(View.OnClickListener {
            selectFragment("main")
            toggleColor(findViewById(R.id.bar_icon_home), findViewById(R.id.bar_txt_home))
        })

        // Load deal
        findViewById<TextView>(R.id.bar_icon_deal).setOnClickListener(View.OnClickListener {
            selectFragment("deal")
            toggleColor(findViewById(R.id.bar_icon_deal), findViewById(R.id.bar_txt_deal))
        })
        findViewById<TextView>(R.id.bar_txt_deal).setOnClickListener(View.OnClickListener {
            selectFragment("deal")
            toggleColor(findViewById(R.id.bar_icon_deal), findViewById(R.id.bar_txt_deal))
        })

        /*if (myLoan.get("status") != "loading") {
            if (myLoan.get("status") == "not_found") {
                replaceFragment(lend(bundle), parentFragmentManager)
            } else {
                Toast.makeText(context, "Ya tienes un préstamo activo", Toast.LENGTH_LONG)
                    .show()
            }
    }
    if (myLoan.get("status") != "loading") {
            if (myLoan.get("status") != "not_found") {
                replaceFragment(my_loan(bundle), parentFragmentManager)
            } else {
                Toast.makeText(context, "Debes solicitar un préstamo primero", Toast.LENGTH_LONG).show()
            }
    }*/

        // Load my business
        findViewById<TextView>(R.id.bar_icon_my_business).setOnClickListener(View.OnClickListener {
            selectFragment("my_business")
            toggleColor(
                findViewById(R.id.bar_icon_my_business),
                findViewById(R.id.bar_txt_my_business)
            )
        })
        findViewById<TextView>(R.id.bar_txt_my_business).setOnClickListener(View.OnClickListener {
            selectFragment("my_business")
            toggleColor(
                findViewById(R.id.bar_icon_my_business),
                findViewById(R.id.bar_txt_my_business)
            )
        })

        // Load profile
        findViewById<TextView>(R.id.bar_icon_profile).setOnClickListener(View.OnClickListener {
            selectFragment("profile")
            toggleColor(findViewById(R.id.bar_icon_profile), findViewById(R.id.bar_txt_profile))
        })
        findViewById<TextView>(R.id.bar_txt_profile).setOnClickListener(View.OnClickListener {
            selectFragment("profile")
            toggleColor(findViewById(R.id.bar_icon_profile), findViewById(R.id.bar_txt_profile))
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        if (accessFrom == "signup") {
            inflater.inflate(R.menu.menu_non_registered, menu)
        } else {
            inflater.inflate(R.menu.menu_registered, menu)
        }
        return true
    }

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

    // Function to finish the current session
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

    // Load main segment
    fun selectFragment(fragmentName: String) {
        if (userType == "worker")
            when (fragmentName) {
                "main" -> replaceFragment(main_worker(bundle), "main_worker")
                "deal" -> replaceFragment(lend(bundle), "lend")
                "my_business" -> replaceFragment(my_loan(bundle), "my_loan")
                "profile" -> replaceFragment(profile(bundle), "profile")
                else -> {}
            }
        else
            when (fragmentName) {
                "main" -> replaceFragment(main_investor(bundle), "main_investor")
                "deal" -> replaceFragment(invest_options(account), "invest")
                "my_business" -> replaceFragment(my_investment_options(bundle), "my_investment")
                "profile" -> replaceFragment(profile(bundle), "profile")
                else -> {}
            }
    }

    fun toggleColor(icon: TextView, text: TextView) {
        currentIcon.setTextColor(getColor(R.color.textPrimary))
        currentText.setTextColor(getColor(R.color.textPrimary))
        currentIcon = icon
        currentText = text
        currentIcon.setTextColor(getColor(R.color.loanppi))
        currentText.setTextColor(getColor(R.color.loanppi))
    }
}
