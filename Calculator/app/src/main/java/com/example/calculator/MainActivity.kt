package com.example.calculator

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import kotlin.math.abs

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (this.supportActionBar != null)
            this.supportActionBar!!.hide()

        // view settings
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR


        // components

        val digits = arrayOf(
            findViewById<Button>(R.id.btn0),
            findViewById(R.id.btn1),
            findViewById(R.id.btn2),
            findViewById(R.id.btn3),
            findViewById(R.id.btn4),
            findViewById(R.id.btn5),
            findViewById(R.id.btn6),
            findViewById(R.id.btn7),
            findViewById(R.id.btn8),
            findViewById(R.id.btn9)
        )

        val operators = arrayOf(
            findViewById<Button>(R.id.btnSum),
            findViewById(R.id.btnSub),
            findViewById(R.id.btnMult),
            findViewById(R.id.btnDiv),
            findViewById(R.id.btnPow),
        )

        val angles = arrayOf(
            findViewById<Button>(R.id.btnCos),
            findViewById(R.id.btnSin),
            findViewById(R.id.btnTan)
        )

        val symbols = arrayOf(
            findViewById<Button>(R.id.btnPI),
            findViewById(R.id.btnDot),
            findViewById(R.id.btnOpenP),
            findViewById(R.id.btnCloseP)
        )

        val input = findViewById<EditText>(R.id.input)
        val result = findViewById<EditText>(R.id.result)

        val btnEqual = findViewById<Button>(R.id.btnEqual)
        val btnAC = findViewById<Button>(R.id.btnAC)
        val btnDel = findViewById<Button>(R.id.btnDel)

        var cur = ""

        // disable keyboard
        input.showSoftInputOnFocus = false
        result.showSoftInputOnFocus = false


        // events

        (digits + operators + symbols).forEach { btn ->
            btn.setOnClickListener {
                cur += btn.text
                input.setText(cur)
            }
        }

        angles.forEach { digit ->
            digit.setOnClickListener {
                cur += (digit.text.toString() + "(")
                input.setText(cur)
            }
        }

        btnAC.setOnClickListener {
            cur = ""
            input.setText(cur)
            result.setText(cur)
        }

        btnDel.setOnClickListener {
            if (cur.isNotEmpty()) {
                var angle = false
                if (cur.length > 3) {
                    val tmp = cur.substring(cur.length - 4, cur.length)
                    angle = arrayOf("sin(", "cos(", "tan(").contains(tmp)
                }
                cur = cur.substring(0, cur.length - if (angle) 4 else 1)
                input.setText(cur)
            }
        }

        input.doAfterTextChanged {

            val r = Runnable {
                cur = input.text.toString()

                result.textSize = 22F
                input.textSize = 30F

                try {
                    if (cur.isEmpty()) {
                        result.setText("")

                    } else {
                        val ans = Calculator().calculate(cur)
                        result.setText(("= " + format(ans)))
                    }
                } catch (ex: RuntimeException) {
                    result.setText("")
                }
            }

            input.setSelection(input.length())

            r.run()
        }

        input.setOnClickListener {
            result.textSize = 22F
            input.textSize = 30F
        }

        btnEqual.setOnClickListener {

            result.textSize = 30F
            input.textSize = 22F

            try {
                if (cur.isNotEmpty()) {
                    val ans = Calculator().calculate(cur)
                    result.setText(("= " + format(ans)))
                }
            } catch (ex: RuntimeException) {
                result.setText(ex.message)
            }
        }


//        println(Calculator().calculate("-(-(2^3))/4+1"))
    }

    // format result
    private fun format(ans: Double): String {
        if (ans == 0.0) return "0"

        val f = abs(ans) - abs(ans.toLong())

        if (f in 10e-8..1.0 || abs(ans) in 1.0..10e8) {
            return "%.9f".format(ans).replace("\\.?0+$".toRegex(), "")
        }
        return "%.8E".format(ans).format(ans).replace("\\.?0+(\\D)".toRegex(), "$1")
    }

}