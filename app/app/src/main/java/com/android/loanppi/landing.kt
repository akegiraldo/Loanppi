package com.android.loanppi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class landing : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)
    }

    fun onSignUp(view: View) {
        val intent = Intent(this, signup::class.java)
        startActivity(intent)
    }

    fun onLogin(view: View) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_login, null)
        val title = dialogLayout.findViewById<TextView>(R.id.txt_login_title)
        val btn_f = dialogLayout.findViewById<Button>(R.id.btn_l_facebook)
        val btn_g = dialogLayout.findViewById<Button>(R.id.btn_l_google)
        builder.setView(dialogLayout)
        builder.setNegativeButton(R.string.cancelar) { dialog, which ->
            Toast.makeText(applicationContext,
                R.string.cancelar, Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }
}

