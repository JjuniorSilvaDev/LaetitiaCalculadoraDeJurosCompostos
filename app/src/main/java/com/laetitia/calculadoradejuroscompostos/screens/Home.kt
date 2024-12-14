package com.laetitia.calculadoradejuroscompostos.screens

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.laetitia.calculadoradejuroscompostos.databinding.FragmentHomeBinding
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class Home : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var initialInvestment: EditText
    private lateinit var monthlyContribution: EditText
    private lateinit var interestRate: EditText
    private lateinit var period: EditText
    private lateinit var calculateButton: Button

    private lateinit var loadScreenView: View
    private lateinit var loadProgressBar: ProgressBar
    private lateinit var loadText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        prepareBinding()

        inflateItems()

        return binding.root
    }

    private fun prepareBinding() {
        binding.apply {
            initialInvestment = initialInvestmentInputFragment
            monthlyContribution = monthlyContributionInputFragment
            interestRate = interestRateInputFragment
            period = periodInputFragment
            calculateButton = calculateButtonFragment
            loadScreenView = loadScreenViewFragmen
            loadProgressBar = loadProgressBarFragment
            loadText = loadTextViewFragment
        }
    }

    private fun inflateItems() {
        setupDecimalEditText(initialInvestment)
        setupDecimalEditText(monthlyContribution)
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
        // Função auxiliar para exibir alertas
        fun showAlert(message: String) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage(message)
            builder.setCancelable(false)
            builder.setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }
            val alertDialog = builder.create()
            alertDialog.show()
        }

        // Verificar se os campos estão vazios ou com valores inválidos
        when {
            // Verifica se os campos de investimento inicial ou aporte mensal estão vazios ou com valor "0,00"
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
            else -> {
                showAlert("Todos os campos foram preenchidos")
            }
        }
    }


}