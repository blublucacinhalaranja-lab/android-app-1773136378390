package com.example.calculadora

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.calculadora.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: CalculatorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[CalculatorViewModel::class.java]

        binding.resultTextView.text = viewModel.result.value
        binding.expressionTextView.text = viewModel.expression.value

        viewModel.result.observe(this) {
            binding.resultTextView.text = it
        }

        viewModel.expression.observe(this) {
            binding.expressionTextView.text = it
        }

        setButtonListeners()
    }

    private fun setButtonListeners() {
        binding.button0.setOnClickListener { viewModel.appendNumber("0") }
        binding.button1.setOnClickListener { viewModel.appendNumber("1") }
        binding.button2.setOnClickListener { viewModel.appendNumber("2") }
        binding.button3.setOnClickListener { viewModel.appendNumber("3") }
        binding.button4.setOnClickListener { viewModel.appendNumber("4") }
        binding.button5.setOnClickListener { viewModel.appendNumber("5") }
        binding.button6.setOnClickListener { viewModel.appendNumber("6") }
        binding.button7.setOnClickListener { viewModel.appendNumber("7") }
        binding.button8.setOnClickListener { viewModel.appendNumber("8") }
        binding.button9.setOnClickListener { viewModel.appendNumber("9") }

        binding.buttonAdd.setOnClickListener { viewModel.appendOperation("+") }
        binding.buttonSubtract.setOnClickListener { viewModel.appendOperation("-") }
        binding.buttonMultiply.setOnClickListener { viewModel.appendOperation("*") }
        binding.buttonDivide.setOnClickListener { viewModel.appendOperation("/") }

        binding.buttonDecimal.setOnClickListener { viewModel.appendDecimal() }
        binding.buttonClear.setOnClickListener { viewModel.clear() }
        binding.buttonPlusMinus.setOnClickListener { viewModel.plusMinus() }
        binding.buttonPercent.setOnClickListener { viewModel.percent() }

        binding.buttonEquals.setOnClickListener { viewModel.calculate() }
    }
}