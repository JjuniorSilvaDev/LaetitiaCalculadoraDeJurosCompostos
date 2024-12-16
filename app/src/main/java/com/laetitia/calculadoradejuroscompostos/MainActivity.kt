package com.laetitia.calculadoradejuroscompostos

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.laetitia.calculadoradejuroscompostos.databinding.ActivityMainBinding
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import kotlin.math.pow

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var initialInvestment: EditText
    private lateinit var monthlyContribution: EditText
    private lateinit var interestRate: EditText
    private lateinit var interestRateSpinner: Spinner
    private lateinit var period: EditText
    private lateinit var periodSpinner: Spinner
    private lateinit var calculateButton: Button

    private var initialInvestmentValue: BigDecimal = BigDecimal.ZERO
    private var monthlyContributionValue: BigDecimal = BigDecimal.ZERO
    private var interestRateValue: BigDecimal = BigDecimal.ZERO
    private var periodValue: Int = 0

    private lateinit var loadScreenView: View
    private lateinit var loadProgressBar: ProgressBar
    private lateinit var loadText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        prepareBinding()

        inflateItems()

    }


    private fun prepareBinding() {
        binding.apply {
            initialInvestment = initialInvestmentInputFragment
            monthlyContribution = monthlyContributionInputFragment
            interestRate = interestRateInputFragment
            interestRateSpinner = interestRateSpinnerFragment
            period = periodInputFragment
            periodSpinner = periodSpinnerFragment
            calculateButton = calculateButtonFragment
            loadScreenView = loadScreenViewFragmen
            loadProgressBar = loadProgressBarFragment
            loadText = loadTextViewFragment
        }
    }

    private fun inflateItems() {
        setupDecimalEditText(initialInvestment)
        setupDecimalEditText(monthlyContribution)
        setupDecimalEditText(interestRate)
        loadScreenView.setOnClickListener { }
        calculateButton.setOnClickListener {
            checkFields()
        }
    }

    private fun setupDecimalEditText(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                editText.removeTextChangedListener(this)

                s?.let {
                    val cleanString = it.toString().replace(".", "").replace(",", "")
                    if (cleanString.isNotEmpty()) {
                        val parsed = cleanString.toDoubleOrNull() ?: 0.0
                        val formatSymbols = DecimalFormatSymbols().apply {
                            decimalSeparator = ','
                            groupingSeparator = '.'
                        }
                        val formatter = DecimalFormat("#,##0.00", formatSymbols)
                        editText.setText(formatter.format(parsed / 100))
                        editText.setSelection(editText.text.length)
                    }
                }
                editText.addTextChangedListener(this)
            }

        })
    }

    private fun checkFields() {

        when {
            (initialInvestment.text.isNullOrBlank() || initialInvestment.text.toString() == "0,00") &&
                    (monthlyContribution.text.isNullOrBlank() || monthlyContribution.text.toString() == "0,00") -> {
                showAlert("Por Favor, defina o investimento inicial ou o valor do aporte mensal")
            }

            interestRate.text.isNullOrBlank() -> {
                showAlert("Por Favor, defina a taxa de juros")
            }

            period.text.isNullOrBlank() -> {
                showAlert("Por Favor, defina o período de tempo")
            }

            monthlyContribution.text.isNullOrBlank() || monthlyContribution.text.toString() == "0,00" -> {

                valueAssignment()

                matchInterestRateTimeframeWithPeriod()

                calculateCompoundInterestWithoutMonthlyContribution()
            }

            else -> {

                valueAssignment()

                matchInterestRateTimeframeWithPeriod()

                calculateCompoundInterestWithMonthlyContribution()

            }
        }
    }

    private fun showAlert(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun valueAssignment() {

        val initialInvestmentString = initialInvestment.text.toString()
        initialInvestmentValue = initialInvestmentString
            .replace(".", "")
            .replace(",", ".")
            .toBigDecimalOrNull() ?: BigDecimal.ZERO

        val monthlyContributionString = monthlyContribution.text.toString()
        monthlyContributionValue = monthlyContributionString
            .replace(".", "")
            .replace(",", ".")
            .toBigDecimalOrNull() ?: BigDecimal.ZERO

        val interestRateString = interestRate.text.toString()
        interestRateValue = interestRateString
            .replace(".", "")
            .replace(",", ".")
            .toBigDecimalOrNull() ?: BigDecimal.ZERO
        interestRateValue = interestRateValue.divide(BigDecimal("100"), MathContext.DECIMAL128)

        val periodString = period.text.toString()
        periodValue = periodString
            .replace(".", "")
            .replace(",", ".")
            .toInt()

    }

    private fun matchInterestRateTimeframeWithPeriod() {

//        val newInterestValue: BigDecimal = when {
//
//            periodSpinner.selectedItem.toString() == resources.getStringArray(R.array.period_spinner)[0].toString() &&
//                    interestRateSpinner.selectedItem.toString() == resources.getStringArray(R.array.interest_rate_spinner)[1].toString() -> {
//
//                (BigDecimal.ONE + interestRateValue).pow(12) - BigDecimal.ONE
//
//            }
//
//            periodSpinner.selectedItem.toString() == resources.getStringArray(R.array.period_spinner)[1].toString() &&
//                    interestRateSpinner.selectedItem.toString() == resources.getStringArray(R.array.interest_rate_spinner)[0].toString() -> {
//
//                val monthlyRate = BigDecimal.ONE + interestRateValue
//                val fractionalExponent =
//                    BigDecimal("1").divide(BigDecimal("12"), 10, RoundingMode.HALF_UP)
//
//                val result = monthlyRate.toDouble().pow(fractionalExponent.toDouble())
//                BigDecimal(result) - BigDecimal.ONE
//
//            }
//
//            else -> {
//
//                interestRateValue
//
//            }
//
//        }

        if (interestRateSpinner.selectedItem.toString() == resources.getStringArray(R.array.interest_rate_spinner)[0]){
            val monthlyRate = BigDecimal.ONE + interestRateValue
                val fractionalExponent =
                    BigDecimal("1").divide(BigDecimal("12"), 10, RoundingMode.HALF_UP)

                val result = monthlyRate.toDouble().pow(fractionalExponent.toDouble())
            interestRateValue = BigDecimal(result) - BigDecimal.ONE
        }

        if (periodSpinner.selectedItem.toString() == resources.getStringArray(R.array.period_spinner)[0]){
            periodValue *= 12
        }

    }

    private fun calculateCompoundInterestWithoutMonthlyContribution() {

        val calcResult =
            initialInvestmentValue * (BigDecimal.ONE + interestRateValue).pow(periodValue)

        val totalInterest = calcResult - initialInvestmentValue
        val formatSymbols = DecimalFormatSymbols().apply {
            decimalSeparator = ','
            groupingSeparator = '.'
        }
        val formatter = DecimalFormat("#,##0.00", formatSymbols)
        val calcResultString = formatter.format(calcResult)
        val totalInterestString = formatter.format(totalInterest)
        val message =
            String.format("Total Investido (Capital): R$ $initialInvestmentValue\n\nJuros Totais: $totalInterestString\n\nValor Total (Montante): $calcResultString")

        showAlert(message)
    }

    private fun calculateCompoundInterestWithMonthlyContribution() {

        val scale = 10
        val roundingMode = RoundingMode.HALF_UP

        val onePlusRate = BigDecimal.ONE.add(interestRateValue)
        val power = onePlusRate.pow(periodValue, MathContext(scale))

        val futureValueInitialInvestment = initialInvestmentValue.multiply(power)
        val futureValueContributions = monthlyContributionValue.multiply(
            (power.subtract(BigDecimal.ONE)).divide(interestRateValue, scale, roundingMode)
        )
        val calcResult = futureValueInitialInvestment.add(futureValueContributions)

        val totalInvested =
            initialInvestmentValue.add(monthlyContributionValue.multiply(periodValue.toBigDecimal()))

        val totalInterest = calcResult.subtract(totalInvested)

        val formatSymbols = DecimalFormatSymbols().apply {
            decimalSeparator = ','
            groupingSeparator = '.'
        }
        val formatter = DecimalFormat("#,##0.00", formatSymbols)

        val calcResultString = formatter.format(calcResult)
        val totalInterestString = formatter.format(totalInterest)
        val totalInvestedString = formatter.format(totalInvested)

        val message = """
        Total Investido (Capital): R$ $totalInvestedString

        Juros Totais: R$ $totalInterestString

        Valor Total (Montante): R$ $calcResultString
        """.trimIndent()

        showAlert(message)

    }


}
