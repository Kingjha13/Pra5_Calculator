package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class CalculatorKotlin : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Calculator()
        }
    }
}

@Composable
fun Calculator() {
    var display by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {

        Text(
            text = if (display.isEmpty()) "0" else display,
            color = Color.White,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )

        Spacer(modifier = Modifier.height(20.dp))

        val buttons = listOf(
            listOf("7", "8", "9", "÷"),
            listOf("4", "5", "6", "×"),
            listOf("1", "2", "3", "-"),
            listOf("C", "0", "=", "+")
        )

        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { label ->
                    CalcButton(label) {
                        display = onButtonClick(label, display)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun CalcButton(text: String, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(80.dp)
            .background(Color.DarkGray, shape = MaterialTheme.shapes.large)
            .clickable { onClick() }
    ) {
        Text(text, color = Color.White, fontSize = 24.sp)
    }
}

fun onButtonClick(value: String, display: String): String {
    return when (value) {
        "C" -> ""
        "=" -> formatResult(evaluateExpression(display))
        else -> display + value
    }
}

fun evaluateExpression(expr: String): Double {
    val values = ArrayDeque<Double>()
    val ops = ArrayDeque<Char>()

    var i = 0
    while (i < expr.length) {
        val c = expr[i]

        when {
            c.isDigit() -> {
                var num = ""
                while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
                    num += expr[i]
                    i++
                }
                values.addLast(num.toDouble())
                continue
            }
            c == '+' || c == '-' || c == '×' || c == '÷' -> {
                while (ops.isNotEmpty() && precedence(ops.last()) >= precedence(c)) {
                    val result = applyOp(values.removeLast(), values.removeLast(), ops.removeLast())
                    values.addLast(result)
                }
                ops.addLast(c)
            }
        }
        i++
    }

    while (ops.isNotEmpty()) {
        val result = applyOp(values.removeLast(), values.removeLast(), ops.removeLast())
        values.addLast(result)
    }

    return values.last()
}

fun precedence(op: Char): Int = when (op) {
    '+', '-' -> 1
    '×', '÷' -> 2
    else -> 0
}

fun applyOp(b: Double, a: Double, op: Char): Double = when (op) {
    '+' -> a + b
    '-' -> a - b
    '×' -> a * b
    '÷' -> if (b != 0.0) a / b else Double.NaN
    else -> 0.0
}

fun formatResult(r: Double): String {
    return if (r % 1.0 == 0.0) r.toInt().toString() else r.toString()
}
