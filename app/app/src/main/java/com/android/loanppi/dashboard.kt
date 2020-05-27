package com.android.loanppi

import android.annotation.SuppressLint
import android.content.ClipData
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.view.menu.MenuView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentController
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import java.util.*

class dashboard : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val type = intent.getStringExtra("type")
        val label = intent.getStringExtra("label")
        val business = intent.getStringExtra("business")
        val porc = intent.getIntExtra("porc", 0)
        val meta = intent.getFloatExtra("meta", 0.0F)

        loadFragment(main())
        /*findViewById<TextView>(R.id.txt_saludo).setText(intent.getStringExtra("type"))
        findViewById<TextView>(R.id.txt_meta).setText(intent.getStringExtra("label"))
        val porc_val = intent.getIntExtra("porc", 0)
        val meta = intent.getFloatExtra("meta", 0.0F)
        val porc = porc_val / meta * 100
        findViewById<TextView>(R.id.val_meta).setText(meta.toString())
        findViewById<SeekBar>(R.id.bar_prog_moto).setProgress(porc.toInt())
        findViewById<TextView>(R.id.bar_prog_value).setText(porc_val.toString())
        findViewById<Button>(R.id.btn_1).setText(intent.getStringExtra("business"))*/
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.menu_home -> {
                replaceFragment(main())
                true
            }
            R.id.menu_profile -> {
                replaceFragment(profile())
                true
            }
            R.id.menu_my_business -> {
                replaceFragment(business())
                true
            }
            R.id.menu_signout -> {
                finish()
                true
            }
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
}
