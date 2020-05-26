package com.android.loanppi

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi

class dashboard : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        findViewById<TextView>(R.id.txt_saludo).setText(intent.getStringExtra("type"))
        findViewById<TextView>(R.id.txt_meta).setText(intent.getStringExtra("label"))
        val porc_val = intent.getIntExtra("porc", 0)
        val meta = intent.getFloatExtra("meta", 0.0F)
        val porc = porc_val / meta * 100
        findViewById<TextView>(R.id.val_meta).setText(meta.toString())
        findViewById<SeekBar>(R.id.bar_prog_moto).setProgress(porc.toInt())
        findViewById<TextView>(R.id.bar_prog_value).setText(porc_val.toString())
        findViewById<Button>(R.id.btn_1).setText(intent.getStringExtra("btn1"))
    }
}
