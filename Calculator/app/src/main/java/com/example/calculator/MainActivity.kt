package com.example.calculator

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import java.util.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (this.supportActionBar != null)
            this.supportActionBar!!.hide()

        // calculator
        val calc = Calculator()

        // view settings
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR

        // set English locate
        val locale = Locale("en")
        Locale.setDefault(locale)
        resources.configuration.setLocale(locale)

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
        val btnSteps = findViewById<View>(R.id.btnSteps)

        btnSteps.visibility = INVISIBLE

        // disable keyboard
        input.showSoftInputOnFocus = false
        result.showSoftInputOnFocus = false


        // events

        (digits + operators + symbols).forEach { btn ->
            btn.setOnClickListener {
                input.append(("${btn.text}"))
            }
        }

        angles.forEach { digit ->
            digit.setOnClickListener {
                input.append(("${digit.text}("))
            }
        }

        btnAC.setOnClickListener {
            input.text.clear()
            result.text.clear()
        }

        btnDel.setOnClickListener {
            var cur = input.text.toString()

            if (cur.isNotEmpty()) {
                var angle = false
                if (cur.length > 3) {
                    val tmp = cur.substring(cur.length - 4, cur.length)
                    angle = tmp in arrayOf("sin(", "cos(", "tan(")
                }
                cur = cur.substring(0, cur.length - if (angle) 4 else 1)
                input.setText(cur)
            }
        }

        input.doAfterTextChanged {
            var cur = input.text.toString()

            if (cur.isEmpty()) {
                result.setText("")
                btnSteps.visibility = INVISIBLE

            } else {

                cur = input.text.toString()
                result.textSize = 22F
                input.textSize = 30F

                try {
                    val ans = calc.calculate(cur, false) as Double
                    result.setText(("= " + calc.format(ans)))
                    btnSteps.visibility = VISIBLE


                } catch (ex: ArithmeticException) {
                    result.setText(ex.message)
                    btnSteps.visibility = INVISIBLE

                } catch (ex2: RuntimeException) {
                    result.setText("")
                    btnSteps.visibility = INVISIBLE
                }

                input.setSelection(input.length())
            }
        }

        input.setOnClickListener {
            result.textSize = 22F
            input.textSize = 30F
        }

        btnEqual.setOnClickListener {

            val cur = input.text.toString()
            result.textSize = 30F
            input.textSize = 22F

            if (cur.isNotEmpty()) {
                try {
                    val ans = calc.calculate(cur, false) as Double
                    result.setText(("= " + calc.format(ans)))
                    btnSteps.visibility = VISIBLE

                } catch (ex: RuntimeException) {
                    result.setText(ex.message)
                    btnSteps.visibility = INVISIBLE
                }
            }
        }

        btnSteps.setOnClickListener {
            val i = Intent(this, StepsActivity::class.java)
            i.putExtra("input", input.text.toString())
            startActivity(i)
        }
    }
}