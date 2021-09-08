package net.d3b8g.landbord.widgets.add_info

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.PatternMatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.d3b8g.landbord.R
import net.d3b8g.landbord.database.Booking.BookingData
import net.d3b8g.landbord.database.Booking.BookingDatabase
import net.d3b8g.landbord.databinding.WidgetAddInfoBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class AddInfoFragment : Fragment(R.layout.widget_add_info) {

    private lateinit var binding: WidgetAddInfoBinding
    private val model: AddInfoViewModel by activityViewModels()

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = WidgetAddInfoBinding.bind(view)

        binding.closeWidget.setOnClickListener {
            model.widgetSetState.value = true
        }

        binding.fieldDateTo.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                val datePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText(getString(R.string.date_to))
                        .build()

                if(hasFocus) {
                    binding.fieldDateTo.hint = "YYYY-MM-DD"

                    datePicker.show(parentFragmentManager, "DateOfEnd")
                    datePicker.addOnPositiveButtonClickListener {
                        val calendarDate = model.chosenCalendarDate.value!!
                        val chosenDate = SimpleDateFormat("yyyy-MM-dd").format(it)

                        if(it < SimpleDateFormat("yyyy-MM-dd").parse(calendarDate)!!.time) {
                            Snackbar.make(view, getString(R.string.date_early_error), Snackbar.LENGTH_SHORT).show()
                            binding.fieldDateTo.setText(calendarDate)
                        } else {
                            binding.fieldDateTo.setText(chosenDate)
                        }
                    }
                }
                else binding.fieldDateTo.hint = ""
            }


        binding.updateAddInfo.setOnClickListener {
            if(canUpdateDateInfo()) {
                lifecycleScope.launch {
                    updateDateInfo()
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private suspend fun updateDateInfo() = withContext(Dispatchers.IO) {
        val db = BookingDatabase.getInstance(requireContext()).bookedDatabaseDao
        val today = SimpleDateFormat("yyyy-MM-dd").format(Date())
        val calendarDate = model.chosenCalendarDate.value!!

        // Если не выбрана дата - возвращаем 0
        val getDaysBooked = if(binding.fieldDateTo.text!!.toString().isEmpty()) {
            val currentDate = SimpleDateFormat("yyyy-MM-dd").parse(model.chosenCalendarDate.value!!)!!.time

            //Получаем разность между выбранной датой и выбранной в календаре и делим на кол-во секунд в сутках, чтобы получить кол-во дней букинга.
            (currentDate - SimpleDateFormat("yyyy-MM-dd").parse(calendarDate)!!.time) / 86400
        } else 0

        db.insert(
            BookingData(
                id = 0,
                bookingDate = today,
                deposit = binding.fieldDeposit.text!!.toString().toInt(),
                username = binding.fieldUsername.text!!.toString(),
                userPhone = binding.fieldPhone.text!!.toString().toLong(),
                daysBooked = getDaysBooked.toInt(),
                bookingChatLink = binding.fieldChatLink.text!!.toString()
            )
        )
    }

    private fun canUpdateDateInfo(): Boolean {

        var canUpdate = false

        binding.filledDepositField.error = if(binding.fieldDeposit.text!!.isEmpty()) {
            canUpdate = false
            getString(R.string.field_empty)
        } else {
            canUpdate = true
            ""
        }

        binding.filledUsernameField.error = if(binding.fieldUsername.text!!.isEmpty()) {
            canUpdate = false
            getString(R.string.field_empty)
        } else {
            canUpdate = true
            ""
        }

        binding.filledPhoneField.error = if(binding.fieldPhone.text!!.isEmpty()) {
            canUpdate = false
            getString(R.string.field_empty)
        } else {
            canUpdate = true
            ""
        }

        binding.filledPhoneField.error = if(binding.fieldDateTo.text!!.isEmpty()) {
            canUpdate = false
            getString(R.string.field_empty)
        } else {
            canUpdate = true
            ""
        }

        binding.filledChatLinkField.error = if (binding.fieldChatLink.text!!.matches(Patterns.WEB_URL.toRegex())) {
            getString(R.string.invalid_url)
        } else ""

        return canUpdate
    }
}