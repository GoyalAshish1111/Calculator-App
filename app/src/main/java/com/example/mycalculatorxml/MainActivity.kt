package com.example.mycalculatorxml

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.lang.ArithmeticException
import java.util.*

class MainActivity : AppCompatActivity() {

    private var tvInput: TextView? = null
    private var lastNumeric: Boolean = false
    private var lastDot: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvInput = findViewById(R.id.tvInput)
    }

    fun onDigit(view: View) {
        tvInput?.append((view as Button).text)
        lastNumeric = true
        lastDot = false
    }

    fun onClear(view: View) {
        tvInput?.text = ""
        lastNumeric = false
        lastDot = false
    }

    fun onDecimalPoint(view: View) {
        if (lastNumeric && !lastDot) {
            tvInput?.append(".")
            lastNumeric = false
            lastDot = true
        }
    }

    fun onOperator(view: View) {
        tvInput?.text?.let {
            if (lastNumeric && !isOperatorAdded(it.toString())) {
                tvInput?.append((view as Button).text)
                lastNumeric = false
                lastDot = false
            }
        }
    }

    fun onBack(view: View) {
        if (tvInput?.text != null && tvInput!!.text.isNotEmpty()) {
            val currentText = tvInput!!.text.toString()
            val newText = currentText.substring(0, currentText.length - 1)
            tvInput!!.text = newText
        }
    }

    fun onEqual(view: View) {
        val expression = tvInput?.text.toString()
        val result = evaluateExpression(expression)
        tvInput?.text = result.toString()
    }

    private fun evaluateExpression(expression: String): Double {
        val tokens = expression.toCharArray()
        val values = Stack<Double>()
        val ops = Stack<Char>()
        var i = 0

        while (i < tokens.size) {
            if (tokens[i].isWhitespace()) {
                i++
                continue
            }

            // If the current token is a number, push it to values stack
            if (tokens[i].isDigit() || tokens[i] == '.') {
                val sbuf = StringBuilder()
                while (i < tokens.size && (tokens[i].isDigit() || tokens[i] == '.')) {
                    sbuf.append(tokens[i++])
                }
                values.push(sbuf.toString().toDouble())
                i--
            }
            // If the current token is an opening parenthesis, push it to ops stack
            else if (tokens[i] == '(') {
                ops.push(tokens[i])
            }
            // If the current token is a closing parenthesis, solve the entire sub-expression
            else if (tokens[i] == ')') {
                while (ops.peek() != '(') {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()))
                }
                ops.pop() // Pop the opening parenthesis from the ops stack
            }
            // If the current token is an operator, resolve operators with higher or equal precedence
            else if (isOperator(tokens[i])) {
                while (!ops.isEmpty() && hasPrecedence(tokens[i], ops.peek())) {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()))
                }
                ops.push(tokens[i])
            }

            i++
        }

        // Resolve any remaining operators in the ops stack
        while (!ops.isEmpty()) {
            values.push(applyOp(ops.pop(), values.pop(), values.pop()))
        }

        // The result is the top of the values stack
        return values.pop()
    }

    // Function to check if a character is an operator
    private fun isOperator(c: Char): Boolean {
        return c == '+' || c == '-' || c == '*' || c == '/'
    }

    // Function to determine precedence of operators
    private fun hasPrecedence(op1: Char, op2: Char): Boolean {
        if (op2 == '(' || op2 == ')') return false
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) return false
        return true
    }

    // Function to apply the operator to operands
    private fun applyOp(op: Char, b: Double, a: Double): Double {
        return when (op) {
            '+' -> a + b
            '-' -> a - b
            '*' -> a * b
            '/' -> if (b != 0.0) a / b else throw UnsupportedOperationException("Cannot divide by zero")
            else -> 0.0
        }
    }

    // Function to check if an operator is already added in the expression
    private fun isOperatorAdded(value: String): Boolean {
        return value.endsWith('+') || value.endsWith('-') || value.endsWith('*') || value.endsWith('/')
    }
}
