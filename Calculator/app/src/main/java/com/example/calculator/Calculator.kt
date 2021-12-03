package com.example.calculator

import kotlin.math.*

class Calculator {

    fun calculate(infix: String): Double {
        var operand = StringBuilder()
        val operands = MyStack<String>(infix.length)
        for (c in toPostfix(infix).toCharArray()) {
            when {
                c == ' ' -> {
                    operands.push(operand.toString())
                    operand = StringBuilder()
                }
                isDigit(c) -> {
                    operand.append(c)
                }
                isOperator(c) || isAngle(c) -> {
                    operands.push(
                        calculate(
                            c,
                            operands.pop().toString(),
                            operands.pop().toString()
                        ).toString()
                    )
                }
            }
        }
        return operands.pop()!!.toDouble()
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
                if (o1 == 0.0) throw RuntimeException("error: division by zero")
                result = o2 / o1
            }
            '^' -> result = o2.pow(o1)
            's' -> result = "%.9f".format(sin(o1)).toDouble()
            'c' -> result = "%.9f".format(cos(o1)).toDouble()
            't' -> {
                val f = "%.7f".format(cos(o1)).toDouble()
                if (f == 0.0) throw RuntimeException("error: undefined")
                result = "%.9f".format(tan(o1)).toDouble()
            }
        }

        if (result.isNaN()) throw RuntimeException("error: result is NaN")

        return result
    }

    private fun isValid(infix: String): Boolean {
        if (infix.isEmpty()) return false
        val len = infix.length
        val f = infix[0]
        val l = infix[len - 1]
        val stack = MyStack<Char>(len)
        var operand = StringBuilder()

        if (!isDigit(f) && !isAngle(f) && f != '(' && f != '+' && f != '-' || !isDigit(l) && l != ')') {
            return false
        }
        for (i in 0 until len) {
            val c = infix[i]

            if (isDigit(c)) {
                if (c == '.' && (len == 1 || operand.indexOf(".") != -1)) return false
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
                        if (i < len - 1 && ")*/^".indexOf(infix[i + 1]) != -1) return false
                    }
                    c == ')' -> {
                        if (stack.isEmpty || stack.pop() != '(') return false
                        if (i > 0 && isOperator(infix[i - 1])) return false
                    }
                    !isAngle(c) -> {
                        return false
                    }
                }
            }
        }
        return stack.isEmpty
    }


    private fun toPostfix(infixExpression: String): String {
        var infix = infixExpression

        infix = infix
            .replace("\\s".toRegex(), "")
            .replace("π", "($PI)")
            .replace("sin".toRegex(), "s")
            .replace("cos".toRegex(), "c")
            .replace("tan".toRegex(), "t")
            .replace("([)])([\\d(.stc])".toRegex(), "$1*$2")
            .replace("([\\d.])([(sct])".toRegex(), "$1*$2")


        if (!isValid(infix)) throw RuntimeException("error")

        val len = infix.length
        val result = StringBuilder()
        var operand = StringBuilder()
        val operators = MyStack<Char>(len)
        for (i in 0 until len) {
            val c = infix[i]

            // form operand by putting digits together
            if (isDigit(c)) {
                operand.append(c)
            } else {

                // add operand to result
                if (operand.isNotEmpty()) {
                    result.append(operand).append(" ")
                    operand = StringBuilder()
                }

                // arrange operators by priority
                when {
                    isOperator(c) -> {
                        while (!operators.isEmpty && !precedence(c, operators.peek()!!)) {
                            result.append(operators.pop())
                        }
                        operators.push(c)

                        // check - is neg not sub  or  + is not sum
                        if ((c == '-' || c == '+') && (i == 0 || infix[i - 1] == '(')) {
                            result.append("0 ")
                        }
                    }
                    isAngle(c) -> {
                        operators.push(c)
                        result.append("0 ")
                    }
                    c == '(' -> {
                        operators.push(c)
                    }
                    c == ')' -> {
                        while (!operators.isEmpty && operators.peek() != '(') {
                            result.append(operators.pop())
                        }
                        operators.pop()
                    }
                }
            }
        }
        if (operand.isNotEmpty()) {
            result.append(operand).append(" ")
        }
        while (!operators.isEmpty) {
            result.append(operators.pop())
        }

        return result.toString()
    }


    private fun isOperator(c: Char): Boolean {
        return "+-*/^".indexOf(c) != -1
    }

    private fun isAngle(c: Char): Boolean {
        return "sct".indexOf(c) != -1
    }

    private fun isDigit(c: Char): Boolean {
        return c == '.' || Character.isDigit(c)
    }

    // true if "a" precedence is greater than or equal to the precedence of "b"
    private fun precedence(a: Char, b: Char): Boolean {
        if (isAngle(a)) return true

        val oprs = "( +- */ ^ sct"
        val diff = oprs.indexOf(a) - oprs.indexOf(b)

        return diff > 0 || diff == 0 && a == '^'
    }
}