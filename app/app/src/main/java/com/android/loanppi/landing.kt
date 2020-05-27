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
    var label: String = ""
    var business: String = ""
    var porc: Int = 0
    var meta: Float = 0.0F

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
        type = "Inversor"
        label = "Retorno"
        business = "Mi inversión"
        porc = 52000
        meta = 120000F
        onDashboard(view)
    }

    fun onGo(view: View) {
        type = "Trabajador"
        label = "Meta"
        business = "Mi préstamo"
        porc = 350000
        meta = 800000F
        onDashboard(view)
    }

    fun onDashboard(view: View) {
        val intent = Intent(this, dashboard::class.java)
        intent.putExtra("type", "Hola, "+type)
        intent.putExtra("label", label)
        intent.putExtra("business", business)
        intent.putExtra("porc", porc)
        intent.putExtra("meta", meta)
        startActivity(intent)
    }
}

