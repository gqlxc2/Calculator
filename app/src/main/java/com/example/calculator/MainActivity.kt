package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.ui.theme.BackGround
import com.example.calculator.ui.theme.DarkGray
import com.example.calculator.ui.theme.LightGray
import com.example.calculator.ui.theme.Orange
import java.math.BigDecimal

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Calculator()
        }
    }
}

val buttons = arrayOf(
    arrayOf("AC" to LightGray, "+/-" to LightGray, "%" to LightGray, "÷" to Orange),
    arrayOf("7" to DarkGray, "8" to DarkGray, "9" to DarkGray, "×" to Orange),
    arrayOf("4" to DarkGray, "5" to DarkGray, "6" to DarkGray, "-" to Orange),
    arrayOf("1" to DarkGray, "2" to DarkGray, "3" to DarkGray, "+" to Orange),
    arrayOf("0" to DarkGray, "." to DarkGray, "=" to Orange)
)

data class CalculatorState(
    val number1: String = "0",
    val number2: String = "0",
    val opt: String? = null,
    val isLastOpt: Boolean = false
)

@Preview(showSystemUi = true)
@Composable
fun Calculator() {
    var state by remember {
        mutableStateOf(CalculatorState())
    }

    Column(
        Modifier
            .background(BackGround)
            .padding(horizontal = 10.dp)
    ) {
        Box(
            Modifier
                .fillMaxHeight(0.3f)
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(text = if (state.isLastOpt) state.number1 else state.number2, fontSize = 100.sp, color = Color.White)
        }
        Column(Modifier.fillMaxSize()) {
            buttons.forEach {
                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    it.forEach {
                        CalculatorButton(
                            Modifier
                                .weight(if (it.first == "0") 2f else 1f)
                                .aspectRatio(if (it.first == "0") 2f else 1f)
                                .background(it.second), it.first
                        ) {
                            state = calculate(state, it.first)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(modifier: Modifier, symbol: String, onClick: () -> Unit = {}) {
    Box(
        Modifier
            .clip(CircleShape)
            .then(modifier)
            .clickable { onClick.invoke() }, contentAlignment = Alignment.Center
    ) {
        Text(text = symbol, fontSize = 40.sp, color = Color.White)
    }
}

fun calculate(curState: CalculatorState, input: String): CalculatorState {
    return when (input) {
        in "0".."9" -> curState.copy(
            number2 = if (curState.number2 == "0") input else curState.number2 + input,
            number1 = if (curState.opt == "=") "0" else curState.number1,
            isLastOpt = false
        )

        "." -> curState.copy(
            number2 = if (curState.number2.contains(".")) curState.number2 else curState.number2 + input,
            number1 = if (curState.opt == "=") "0" else curState.number1,
            isLastOpt = false
        )

        "AC" -> curState.copy(number1 = "0", number2 = "0", opt = null, isLastOpt = false)

        in arrayOf("+", "-", "×", "÷") -> if (curState.isLastOpt) curState.copy(
            opt = input,
            isLastOpt = true
        ) else when (curState.opt) {
            "+" -> curState.copy(
                number1 = BigDecimal(curState.number1).add(BigDecimal(curState.number2)).stripTrailingZeros()
                    .toPlainString(),
                number2 = "0",
                opt = input,
                isLastOpt = true
            )
            "-" -> curState.copy(
                number1 = BigDecimal(curState.number1).subtract(BigDecimal(curState.number2)).stripTrailingZeros()
                    .toPlainString(),
                number2 = "0",
                opt = input,
                isLastOpt = true
            )
            "×" -> curState.copy(
                number1 = BigDecimal(curState.number1).multiply(BigDecimal(curState.number2)).stripTrailingZeros()
                    .toPlainString(),
                number2 = "0",
                opt = input,
                isLastOpt = true
            )
            "÷" -> curState.copy(
                number1 = try {
                    BigDecimal(curState.number1).divide(BigDecimal(curState.number2), 15, BigDecimal.ROUND_DOWN)
                        .stripTrailingZeros()
                        .toPlainString()
                } catch (e: Exception) {
                    "0"
                },
                number2 = "0",
                opt = input,
                isLastOpt = true
            )
            else -> curState.copy(
                number1 = curState.number2,
                number2 = "0",
                opt = input,
                isLastOpt = true
            )
        }

        "%" -> if (curState.isLastOpt) curState.copy(
            number1 = BigDecimal(curState.number1).divide(BigDecimal("100")).stripTrailingZeros().toPlainString(),
        ) else curState.copy(
            number2 = BigDecimal(curState.number2).divide(BigDecimal("100")).stripTrailingZeros().toPlainString(),
        )

        "+/-" -> if (curState.isLastOpt) curState.copy(
            number1 = BigDecimal(curState.number1).multiply(BigDecimal("-1")).stripTrailingZeros().toPlainString(),
        ) else curState.copy(
            number2 = BigDecimal(curState.number2).multiply(BigDecimal("-1")).stripTrailingZeros().toPlainString(),
        )

        "=" -> when (curState.opt) {
            "+" -> curState.copy(
                number1 = BigDecimal(curState.number1).add(BigDecimal(curState.number2)).stripTrailingZeros()
                    .toPlainString(),
                number2 = "0",
                opt = input,
                isLastOpt = true
            )
            "-" -> curState.copy(
                number1 = BigDecimal(curState.number1).subtract(BigDecimal(curState.number2)).stripTrailingZeros()
                    .toPlainString(),
                number2 = "0",
                opt = input,
                isLastOpt = true
            )
            "×" -> curState.copy(
                number1 = BigDecimal(curState.number1).multiply(BigDecimal(curState.number2)).stripTrailingZeros()
                    .toPlainString(),
                number2 = "0",
                opt = input,
                isLastOpt = true
            )
            "÷" -> curState.copy(
                number1 = try {
                    BigDecimal(curState.number1).divide(BigDecimal(curState.number2), 15, BigDecimal.ROUND_DOWN)
                        .stripTrailingZeros()
                        .toPlainString()
                } catch (e: Exception) {
                    "0"
                },
                number2 = "0",
                opt = input,
                isLastOpt = true
            )
            else -> curState
        }

        else -> curState
    }
}