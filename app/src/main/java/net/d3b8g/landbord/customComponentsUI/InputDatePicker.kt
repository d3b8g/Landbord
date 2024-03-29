package net.d3b8g.landbord.customComponentsUI

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View.OnFocusChangeListener
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import net.d3b8g.landbord.MainActivity
import net.d3b8g.landbord.R
import net.d3b8g.landbord.components.Converter.getTodayDate
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
class InputDatePicker @JvmOverloads constructor(
    context: Context ,
    attrs: AttributeSet? = null ,
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    private val filledInput: TextInputLayout
    private val input: TextInputEditText
    private val fragmentParent: FragmentManager = (context as MainActivity).supportFragmentManager

    private var pickedDate: Long = 0
    var pickedDateString: String = "2021-00-00"

    private var isOpenPicker = false

    init {
        inflate(context , R.layout.input_date_picker , this)
        orientation = VERTICAL

        filledInput = findViewById(R.id.filledInputDatePicker)
        input = findViewById(R.id.fieldInputDatePicker)

        input.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            openDatePicker(hasFocus)
        }
        input.setOnClickListener { openDatePicker() }
    }

    private fun openDatePicker(hasFocus: Boolean = true) {
        if (!isOpenPicker && hasFocus) {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(resources.getString(R.string.date_to))
                .build()

            filledInput.hint = resources.getString(R.string.date_pattern_format)
            datePicker.apply {
                show(fragmentParent , "DateOfEnd")
                addOnPositiveButtonClickListener {
                    val chosenDate = SimpleDateFormat("yyyy-MM-dd").format(it)
                    pickedDate = it
                    pickedDateString = chosenDate
                    input.setText(chosenDate)
                }
                addOnNegativeButtonClickListener {
                    it.clearFocus()
                }
                addOnDismissListener {
                    input.clearFocus()
                    isOpenPicker = false
                }
            }
            isOpenPicker = true
        }
    }

    fun clearInput() {
        input.setText("")
        input.clearFocus()
        filledInput.error = null
    }

    fun isDateCurrent(selectedDate: String = getTodayDate()): Boolean =
        when (true) {
            pickedDate == 0L -> {
                filledInput.error = resources.getString(R.string.field_empty)
                false
            }
            pickedDate < SimpleDateFormat("yyyy-MM-dd").parse(selectedDate)!!.time -> {
                Snackbar.make(
                    this ,
                    resources.getString(R.string.date_early_error) ,
                    Snackbar.LENGTH_SHORT
                ).show()
                input.setText(selectedDate)
                false
            }
            else -> {
                filledInput.error = ""
                true
            }
        }
}