package com.example.calculator

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.bold
import androidx.core.text.color
import androidx.core.text.italic
import androidx.core.text.underline
import java.util.*


class StepsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steps)

        val steps = findViewById<TextView>(R.id.steps)

        steps.showSoftInputOnFocus = false

        // view settings
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR

        // set English locate
        val locale = Locale("en")
        Locale.setDefault(locale)
        resources.configuration.setLocale(locale)

        // scroll steps
        steps.movementMethod = ScrollingMovementMethod()

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

                steps.append(formatTitle("Input"))
                steps.append(" $inp\n")

                steps.append(formatTitle("Solution"))
                steps.append(" $stp\n")

                steps.append(formatTitle("Result"))
                steps.append(" $res\n")

            } catch (ex: RuntimeException) {
                Toast.makeText(this, "Check Input", Toast.LENGTH_SHORT).show()
                finish()
            }

        }.run()
    }

    private fun formatTitle(s: String): SpannableStringBuilder {

        return SpannableStringBuilder()
            .color(
                Color.argb(255, 255, 200, 0)
            )
            { underline { italic { bold { append("\n$s\n\n") } } } }
    }
}