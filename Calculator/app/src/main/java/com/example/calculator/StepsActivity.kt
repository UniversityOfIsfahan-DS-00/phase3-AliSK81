package com.example.calculator

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.bold
import androidx.core.text.color
import androidx.core.text.italic
import java.util.*


class StepsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steps)

        val steps = findViewById<TextView>(R.id.steps)
        val btnBack = findViewById<TextView>(R.id.btnBack)


        if (this.supportActionBar != null)
            this.supportActionBar!!.hide()

        steps.showSoftInputOnFocus = false

        // view settings
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR

        // set English locate
        val locale = Locale("en")
        Locale.setDefault(locale)
        resources.configuration.setLocale(locale)

        // scroll steps
        steps.movementMethod = ScrollingMovementMethod()

        btnBack.setOnClickListener {
            this.finish()
        }

        val input = intent.getStringExtra("input") as String

        val calc = Calculator()


        Runnable {

            try {
                val result = calc
                    .calculate(input, true) as ArrayList<*>

                val inp = result.first().toString()
                val res = result.last().toString()

                val stp = result
                    .subList(1, result.size)
                    .joinToString("\n\n= ")

                steps.text = ""

                steps.append(formatTitle("> Input:"))
                steps.append("$inp\n")

                steps.append(formatTitle("> Solution:"))
                steps.append("$stp\n")

                steps.append(formatTitle("> Result:"))
                steps.append("$res\n")

            } catch (ex: RuntimeException) {
                steps.text = ex.message
            }

        }.run()
    }

    private fun formatTitle(s: String): SpannableStringBuilder {

        return SpannableStringBuilder()
            .color(Color.YELLOW) { italic { bold { append("\n$s\n\n") } } }
    }
}