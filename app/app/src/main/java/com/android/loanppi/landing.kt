package com.android.loanppi

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible

class landing : AppCompatActivity() {

    var type: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)
    }

    fun onSignUp(view: View) {
        val intent = Intent(this, signup::class.java)
        startActivity(intent)
    }

    @SuppressLint("ResourceType")
    fun onLogin(view: View) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_login, null)
        val title = dialogLayout.findViewById<TextView>(R.id.txt_login_title)
        val btn_f = dialogLayout.findViewById<Button>(R.id.btn_l_facebook)
        val btn_g = dialogLayout.findViewById<Button>(R.id.btn_l_google)
        builder.setView(dialogLayout).setNegativeButton(R.string.cancel) { dialog, which ->
            Toast.makeText(
                applicationContext,
                R.string.cancel, Toast.LENGTH_SHORT
            ).show()
        }
        builder.show()
    }

    fun onFb(view: View) {
        type = "w"
        onDashboard(view)
    }

    fun onGo(view: View) {
        type = "i"
        onDashboard(view)
    }

    fun onDashboard(view: View) {
        val intent = Intent(this, dashboard::class.java)
        intent.putExtra("type", type)
        startActivity(intent)
    }
}

