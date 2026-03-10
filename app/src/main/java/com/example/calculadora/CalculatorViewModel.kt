package com.example.calculadora

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.math.sqrt

class CalculatorViewModel : ViewModel() {

    private val _expression = MutableLiveData<String>("0")
    val expression: LiveData<String> = _expression

    private val _result = MutableLiveData<String>("0")
    val result: LiveData<String> = _result

    fun appendNumber(number: String) {
        if (_expression.value == "0") {
            _expression.value = number
        } else {
            _expression.value = _expression.value + number
        }
    }

    fun appendOperation(operation: String) {
        if (_expression.value != null && _expression.value!!.isNotEmpty()) {
            val lastChar = _expression.value!!.last()
            if (lastChar.isDigit()) {
                _expression.value = _expression.value + operation
            }
        }
    }

    fun appendDecimal() {
        if (_expression.value != null && !(_expression.value!!.contains("."))) {
            _expression.value = _expression.value + "."
        }
    }

    fun clear() {
        _expression.value = "0"
        _result.value = "0"
    }

    fun plusMinus() {
        try {
            val currentValue = _expression.value?.toDouble() ?: 0.0
            _expression.value = (-currentValue).toString()
        } catch (e: NumberFormatException) {
            _expression.value = "Error"
        }
    }

    fun percent() {
        try {
            val currentValue = _expression.value?.toDouble() ?: 0.0
            _expression.value = (currentValue / 100).toString()
            calculate()
        } catch (e: NumberFormatException) {
            _expression.value = "Error"
        }
    }

    fun calculate() {
        try {
            val expressionValue = _expression.value ?: "0"
            val resultValue = eval(expressionValue).toString()
            _result.value = resultValue
        } catch (e: Exception) {
            _result.value = "Error"
        }
    }

    private fun eval(expression: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if ((++pos < expression.length)) expression[pos].code else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < expression.length) throw Exception("Unexpected: " + ch.toChar())
                return x
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'.code)) x += parseTerm() // addition
                    else if (eat('-'.code)) x -= parseTerm() // subtraction
                    else return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'.code)) x *= parseFactor() // multiplication
                    else if (eat('/'.code)) x /= parseFactor() // division
                    else return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+'.code)) return parseFactor() // unary plus
                if (eat('-'.code)) return -parseFactor() // unary minus

                var x: Double
                val startPos = pos
                if (eat('('.code)) { // parentheses
                    x = parseExpression()
                    eat(')'.code)
                } else if ((ch >= '0'.code && ch <= '9'.code) || ch == '.'.code) { // numbers
                    while ((ch >= '0'.code && ch <= '9'.code) || ch == '.'.code) nextChar()
                    val s = expression.substring(startPos, pos)
                    x = s.toDouble()
                } else if (ch >= 'a'.code && ch <= 'z'.code) { // functions
                    while (ch >= 'a'.code && ch <= 'z'.code) nextChar()
                    val func = expression.substring(startPos, pos)
                    x = parseFactor()
                    x =
                        if (func == "sqrt") sqrt(x)
                        else if (func == "sin") Math.sin(Math.toRadians(x))
                        else if (func == "cos") Math.cos(Math.toRadians(x))
                        else if (func == "tan") Math.tan(Math.toRadians(x))
                        else throw Exception("Unknown function: $func")
                } else {
                    throw Exception("Unexpected: " + ch.toChar())
                }

                if (eat('^'.code)) x = Math.pow(x, parseFactor()) // exponentiation

                return x
            }
        }.parse()
    }
}