package com.danylom73.a7minutesworkout

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.danylom73.a7minutesworkout.databinding.ActivityBmiBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BMIActivity : AppCompatActivity() {
    companion object {
        private const val METRIC_UNITS_VIEW = "METRIC_UNITS_VIEW"
        private const val US_UNITS_VIEW = "US_UNITS_VIEW"
    }

    private var currentVisibleView = METRIC_UNITS_VIEW

    private lateinit var binding: ActivityBmiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.bmiToolbar)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Calculate BMI"
        }

        binding.bmiToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        makeVisibleMetricUnitsView()

        binding.unitsRg.setOnCheckedChangeListener { _, checkedId: Int ->
            if (checkedId == R.id.metricUnitsRb) makeVisibleMetricUnitsView()
            else makeVisibleUsUnitsView()
        }

        binding.calculateUnitsBtn.setOnClickListener {
            calculateUnits()
        }
    }

    private fun validateMetricUnits(): Boolean {
        var isValid = false

        if (
            binding.metricUnitHeightEt.text.toString().isNotEmpty() &&
            binding.metricUnitWeightEt.text.toString().isNotEmpty()
        ) {
            isValid = true
        }

        return isValid
    }

    private fun validateUsUnits(): Boolean {
        var isValid = false

        if (
            binding.usMetricUnitHeightFeetEt.text.toString().isNotEmpty() &&
            binding.usMetricUnitHeightInchEt.text.toString().isNotEmpty() &&
            binding.metricUnitWeightEt.text.toString().isNotEmpty()
        ) {
            isValid = true
        }

        return isValid
    }

    private fun calculateUnits() {
        if (currentVisibleView == METRIC_UNITS_VIEW) {
            if (validateMetricUnits()) {
                val height: Float =
                    binding.metricUnitHeightEt.text.toString().toFloat() / 100
                val weight: Float =
                    binding.metricUnitWeightEt.text.toString().toFloat()

                val bmi = weight / (height * height)

                displayBMIResult(bmi)
            } else {
                Toast.makeText(
                    this@BMIActivity,
                    "Please, enter valid values",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            if (validateUsUnits()) {
                val heightFeet =
                    binding.usMetricUnitHeightFeetEt.text.toString()
                val heightInch =
                    binding.usMetricUnitHeightInchEt.text.toString()
                val weight: Float =
                    binding.metricUnitWeightEt.text.toString().toFloat()
                val height =
                    heightFeet.toFloat() + heightInch.toFloat() * 12

                val bmi = 703 * (weight / (height * height))

                displayBMIResult(bmi)
            } else {
                Toast.makeText(
                    this@BMIActivity,
                    "Please, enter valid values",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun makeVisibleMetricUnitsView() {
        currentVisibleView = METRIC_UNITS_VIEW
        binding.metricUnitHeightTil.visibility = View.VISIBLE
        binding.metricUsUnitHeightFeetTil.visibility = View.INVISIBLE
        binding.metricUsUnitHeightInchTil.visibility = View.INVISIBLE

        binding.metricUnitHeightEt.text!!.clear()
        binding.metricUnitWeightEt.text!!.clear()

        binding.displayBMIResultLl.visibility = View.INVISIBLE
    }

    private fun makeVisibleUsUnitsView() {
        currentVisibleView = US_UNITS_VIEW
        binding.metricUnitHeightTil.visibility = View.INVISIBLE
        binding.metricUsUnitHeightFeetTil.visibility = View.VISIBLE
        binding.metricUsUnitHeightInchTil.visibility = View.VISIBLE

        binding.usMetricUnitHeightFeetEt.text!!.clear()
        binding.usMetricUnitHeightInchEt.text!!.clear()
        binding.metricUnitWeightEt.text!!.clear()

        binding.displayBMIResultLl.visibility = View.INVISIBLE
    }

    private fun displayBMIResult(bmi: Float) {
        val bmiLabel: String
        val bmiDescription: String

        when (bmi) {
            in 0f..<15f -> {
                bmiLabel = "Very severely underweight"
            }
            in 15f..<16f -> {
                bmiLabel = "Severely underweight"
            }
            in 16f..<18.5f -> {
                bmiLabel = "Underweight"
            }
            in 18.5f..<25f -> {
                bmiLabel = "Normal"
            }
            in 25f..<30f -> {
                bmiLabel = "Overweight"

            }
            in 30f..<35f -> {
                bmiLabel = "Obese Class | (Moderately obese)"
            }
            in 35f..40f -> {
                bmiLabel = "Obese Class || (Severely obese)"
            }
            else -> {
                bmiLabel = "Obese Class ||| (Very Severely obese)"
            }
        }

        when (bmi) {
            in 0f..<18.5f -> {
                bmiDescription = "Oops! You really need to take " +
                        "better care of yourself! Eat more!"
            }
            in 18.5f..<25f -> {
                bmiDescription = "Congratulations! You are in a good shape!"
            }
            in 25f..<35f -> {
                bmiDescription = "Oops! You really need to take " +
                        "care of your yourself! Workout maybe!"
            }
            else -> {
                bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
            }
        }

        val bmiValue = BigDecimal(bmi.toDouble())
            .setScale(2, RoundingMode.HALF_EVEN).toString()

        binding.displayBMIResultLl.visibility = View.VISIBLE
        binding.BMIValueTv.text = bmiValue
        binding.BMITypeTv.text = bmiLabel
        binding.BMIDescriptionTv.text = bmiDescription
    }
}