package com.android.loanppi

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment

class dashboard : AppCompatActivity() {

    var type = ""

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        type = intent.getStringExtra("type")

        if (type == "w") {
            loadFragment(main_worker())
        } else {
            loadFragment(main_investor())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        if (type == "w") {
            inflater.inflate(R.menu.menu_worker, menu)
        } else {
            inflater.inflate(R.menu.menu_investor, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.menu_i_home -> { replaceFragment(main_investor()) ; true }
            R.id.menu_i_profile -> { replaceFragment(profile()) ; true }
            R.id.menu_i_invest -> { replaceFragment(invest()) ; true }
            R.id.menu_i_my_investment -> { replaceFragment(my_investment()) ; true }
            R.id.menu_i_history -> { true }
            R.id.menu_i_signout -> { finish() ; true }

            R.id.menu_w_home -> { replaceFragment(main_worker()) ; true }
            R.id.menu_w_profile -> { replaceFragment(profile()) ; true }
            R.id.menu_w_lend -> { replaceFragment(lend()) ; true }
            R.id.menu_w_my_loan -> { replaceFragment(my_loan()) ; true }
            R.id.menu_w_history -> { true }
            R.id.menu_w_signout -> { finish() ; true }
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
}
