package com.example.calculator

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.GravityCompat
import androidx.core.widget.doAfterTextChanged
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var navView: NavigationView
    private lateinit var input: EditText
    private lateinit var result: EditText

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nav_activity_main)

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

        input = findViewById(R.id.input)
        result = findViewById(R.id.result)

        val btnEqual = findViewById<Button>(R.id.btnEqual)
        val btnAC = findViewById<Button>(R.id.btnAC)
        val btnDel = findViewById<Button>(R.id.btnDel)


        disableKeyboard()


        // events

        (digits + operators + symbols).forEach { btn ->
            btn.setOnClickListener {
                input.append(("${btn.text}"))
            }
        }

        angles.forEach { angle ->
            angle.setOnClickListener {
                input.append(("${angle.text}("))
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

            } else {

                cur = input.text.toString()

                try {
                    val ans = calc.calculate(cur, false) as Double
                    result.setText(("= " + calc.format(ans)))

                } catch (ex: ArithmeticException) {
                    result.setText(ex.message)

                } catch (ex2: RuntimeException) {
                    result.setText("")
                }

                input.setSelection(input.length())
            }
        }

        btnEqual.setOnClickListener {

            val cur = input.text.toString()

            if (cur.isNotEmpty()) {
                try {
                    val ans = calc.calculate(cur, false) as Double
                    input.setText(calc.format(ans))

                } catch (ex: RuntimeException) {
                    result.setText(ex.message)
                }
            }
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host)

        setupActionBarWithNavController(navController, drawerLayout)
        navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener(this)

        initThemeSwitch()
    }


    private fun disableKeyboard() {
        input.showSoftInputOnFocus = false
        result.showSoftInputOnFocus = false
    }

    private fun initThemeSwitch() {
        val menu = navView.menu.findItem(R.id.nav_dark_switch)
        val darkThemeSwitch = menu.actionView as SwitchCompat

        darkThemeSwitch.setOnCheckedChangeListener { _, checked ->
            AppCompatDelegate.setDefaultNightMode(
                if (checked) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(drawerLayout) || super.onSupportNavigateUp()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.nav_steps -> {
                val i = Intent(this, StepsActivity::class.java)
                i.putExtra("input", input.text.toString())
                startActivity(i)
            }
            R.id.nav_tree -> {
                val i = Intent(this, TreeActivity::class.java)
                i.putExtra("input", input.text.toString())
                startActivity(i)
            }
            R.id.nav_dark_switch -> {
                return false
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return false
    }

}