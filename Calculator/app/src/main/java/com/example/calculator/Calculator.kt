package com.example.calculator

import kotlin.math.*

class Calculator {

    private var history = ArrayList<String>()

    private fun toInfix(postfix: ArrayList<String>): String {

        if (postfix.size == 1)
            return postfix.first()

        val stack = MyStack<String>(postfix.size)

        for (c in postfix) {
            when {
                isNumber(c) -> {
                    if (c == PI.toString())
                        stack.push("π")
                    else
                        stack.push(
                            if (c[0] == '-') "($c)" else c
                        )
                }
                else -> {

                    var o1 = stack.pop().toString()

                    when {
                        c == "p" -> stack.push("(+$o1)")
                        c == "n" -> stack.push(("(-$o1)"))
                        c == "s" -> stack.push(("sin($o1)"))
                        c == "c" -> stack.push(("cos($o1)"))
                        c == "t" -> stack.push(("tan($o1)"))
                        isOperator(c[0]) -> {
                            var o2 = stack.pop().toString()
                            if (o1[0] == '-') o1 = "($o1)"
                            if (o2[0] == '-') o2 = "($o2)"
                            stack.push("($o2 $c $o1)")
                        }
                    }
                }
            }
        }

        val res = stack.pop()!!

        return if (res.first() == '(' && res.last() == ')')
            res.substring(1, res.length - 1)
        else
            res
    }


    fun calculate(infix: String, steps: Boolean): Any {
        val operands = MyStack<String>(infix.length)

        if (steps) {
            history = ArrayList()
            history.add(infix)
            history.add(toInfix(toPostfix(infix)))
        }

        val postfix = toPostfix(infix)
        val newPostfix = ArrayList(postfix)
        var cur = 0

        for (c in postfix) {
            when {
                isNumber(c) -> {
                    operands.push(c)
                }
                else -> {
                    val o1 = operands.pop().toString()

                    val o2 = if (isOperator(c[0]))
                        operands.pop().toString()
                    else "0"

                    val res = calculate(c[0], o1, o2)
                    operands.push(
                        res.toString()
                    )

                    if (steps) {
                        newPostfix[cur] = format(res)

                        newPostfix.removeAt(cur-- - 1)

                        if (isOperator(c[0])) {
                            newPostfix.removeAt(cur-- - 1)
                        }

                        val step = toInfix(newPostfix)
                        if (history.last() != step)
                            history.add(step)
                    }
                }
            }
            cur++
        }

        return if (steps)
            history
        else
            operands.pop()!!.toDouble()
    }

    private fun calculate(operator: Char, operand1: String, operand2: String): Double {

        var result = 0.0
        val o1 = operand1.toDouble()
        val o2 = operand2.toDouble()
        when (operator) {
            '+' -> result = o2 + o1
            '-' -> result = o2 - o1
            '*' -> result = o2 * o1
            '/' -> {
                if (o1 == 0.0) throw ArithmeticException("Error: div by zero")
                result = o2 / o1
            }
            '^' -> result = o2.pow(o1)
            's' -> result = "%.9f".format(sin(o1)).toDouble()
            'c' -> result = "%.9f".format(cos(o1)).toDouble()
            't' -> {
                val f = "%.7f".format(cos(o1)).toDouble()
                if (f == 0.0) throw ArithmeticException("Error: undefined")
                result = "%.9f".format(tan(o1)).toDouble()
            }
            'p' -> result = o1
            'n' -> result = -o1
        }

        if (result.isNaN()) throw ArithmeticException("Error: NaN result")
        if (result.isInfinite()) throw ArithmeticException("Error: too big result")

//        println("\t$o2 $operator $o1 = $result")

        return result
    }

    private fun isValid(infix: String): Boolean {
        if (infix.isEmpty()) return false

        val len = infix.length
        val f = infix.first()
        val l = infix.last()
        val stack = MyStack<Char>(len)
        var operand = StringBuilder()

        if (!isDigit(f) && !isAngle(f) && f != '(' && f != '+' && f != '-' || !isDigit(l) && l != ')') {
            return false
        }
        for ((i, c) in infix.withIndex()) {

            if (isDigit(c)) {
                if (c == '.' && (len == 1 || "." in operand)) return false
                operand.append(c)

            } else {

                if (operand.isNotEmpty()) {
                    if (operand.toString() == ".") return false
                    operand = StringBuilder()
                }
                when {
                    isOperator(c) -> {
                        if (i > 0 && isOperator(infix[i - 1])) return false
                    }
                    c == '(' -> {
                        stack.push(c)
                        if (i < len - 1 && infix[i + 1] in ")*/^") return false
                    }
                    c == ')' -> {
                        if (stack.isEmpty() || stack.pop() != '(') return false
                        if (i > 0 && isOperator(infix[i - 1])) return false
                    }
                    !isAngle(c) -> {
                        return false
                    }
                }
            }
        }
        return stack.isEmpty()
    }


    private fun toPostfix(infixExpression: String): ArrayList<String> {
        var infix = infixExpression

        infix = infix
            .replace("\\s".toRegex(), "")
            .replace("π", "($PI)")
            .replace("sin".toRegex(), "s")
            .replace("cos".toRegex(), "c")
            .replace("tan".toRegex(), "t")
            .replace("([)])([\\d(.stc])".toRegex(), "$1*$2")
            .replace("([\\d.])([(sct])".toRegex(), "$1*$2")


        if (!isValid(infix)) throw RuntimeException("Error: wrong format")

        val len = infix.length
        val result = ArrayList<String>()
        var operand = StringBuilder()
        val operators = MyStack<Char>(len)

        for ((i, c) in infix.withIndex()) {

            // form operand by putting digits together
            if (isDigit(c)) {
                operand.append(c)

            } else {

                // add operand to result
                if (operand.isNotEmpty()) {
                    result.add(operand.toString())
                    operand = StringBuilder()
                }

                // arrange operators by priority
                when {
                    isOperator(c) -> {
                        while (!operators.isEmpty() && !precedence(c, operators.peek()!!)) {
                            result.add(operators.pop().toString())
                        }

                        // check - is neg not sub  or  + is not sum
                        if ((c == '-' || c == '+') && (i == 0 || infix[i - 1] == '(')) {
                            operators.push(if (c == '+') 'p' else 'n')
                        } else {
                            operators.push(c)
                        }
                    }
                    isAngle(c) -> {
                        operators.push(c)
                    }
                    c == '(' -> {
                        operators.push(c)
                    }
                    c == ')' -> {
                        while (!operators.isEmpty() && operators.peek() != '(') {
                            result.add(operators.pop().toString())
                        }
                        operators.pop()
                    }
                }
            }
        }
        if (operand.isNotEmpty()) {
            result.add(operand.toString())
        }
        while (!operators.isEmpty()) {
            result.add(operators.pop().toString())
        }

        return result
    }


    private fun isOperator(c: Char): Boolean {
        return c in "+-*/^"
    }

    private fun isAngle(c: Char): Boolean {
        return c in "sct"
    }

    private fun isDigit(c: Char): Boolean {
        return c.isDigit() || c == '.'
    }

    private fun isNumber(s: String): Boolean {
        return s.matches("[+-.]?\\d+(\\.\\d+)?([Ee][+-]\\d+)?\\.?".toRegex())
    }

    // true if "a" precedence is greater than or equal to the precedence of "b"
    private fun precedence(a: Char, b: Char): Boolean {
        if (isAngle(a)) return true

        val map: HashMap<Char, Int> =
            hashMapOf(
                '(' to 0,
                '+' to 1, '-' to 1,
                'p' to 2, 'n' to 2,
                '*' to 2, '/' to 2,
                '^' to 3,
                's' to 4, 'c' to 4,
                't' to 4
            )

        val diff = map[a]!!.toInt() - map[b]!!.toInt()

        return diff > 0 || diff == 0 && a == '^'
    }

    // format result
    fun format(ans: Double): String {
        if (ans == 0.0) return "0"

        val f = abs(ans) - abs(ans.toLong())

        if (f in 10e-8..1.0 || abs(ans) in 1.0..10e8) {
            return "%.9f".format(ans).replace("\\.?0+$".toRegex(), "")
        }
        return "%.8E".format(ans).format(ans).replace("\\.?0+(\\D)".toRegex(), "$1")
    }
}