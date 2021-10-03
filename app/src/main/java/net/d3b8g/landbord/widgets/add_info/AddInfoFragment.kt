package net.d3b8g.landbord.widgets.add_info

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.d3b8g.landbord.R
import net.d3b8g.landbord.components.Converter.covertStringToDate
import net.d3b8g.landbord.components.DateHelper
import net.d3b8g.landbord.database.Booking.BookingData
import net.d3b8g.landbord.database.Booking.BookingDatabase
import net.d3b8g.landbord.databinding.WidgetAddInfoBinding
import net.d3b8g.landbord.ui.home.HomeFragment.Companion.FLAT_LAST_KEY
import java.text.SimpleDateFormat

class AddInfoFragment : Fragment(R.layout.widget_add_info) {

    private lateinit var binding: WidgetAddInfoBinding
    private val model: AddInfoViewModel by activityViewModels()
    private val db by lazy { initDB() }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = WidgetAddInfoBinding.bind(view)

        binding.closeWidget.setOnClickListener {
            model.widgetSetState.value = true
        }

        binding.fieldDateTo.onFocusChangeListener =
            View.OnFocusChangeListener { _ , hasFocus ->
                val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(getString(R.string.date_to))
                    .build()

                if (hasFocus) {
                    binding.fieldDateTo.hint = "YYYY-MM-DD"
                    datePicker.show(parentFragmentManager , "DateOfEnd")
                    datePicker.addOnPositiveButtonClickListener {
                        val calendarDate = model.chosenCalendarDate.value!!
                        val chosenDate = SimpleDateFormat("yyyy-MM-dd").format(it)

                        if (it < SimpleDateFormat("yyyy-MM-dd").parse(calendarDate)!!.time) {
                            Snackbar.make(
                                view ,
                                getString(R.string.date_early_error) ,
                                Snackbar.LENGTH_SHORT
                            ).show()
                            binding.fieldDateTo.setText(calendarDate)
                        } else {
                            binding.fieldDateTo.setText(chosenDate)
                        }
                    }
                } else binding.fieldDateTo.hint = ""
            }


        binding.updateAddInfo.setOnClickListener {
            if (canUpdateDateInfo()) {
                lifecycleScope.launch {
                    updateDateInfo()
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private suspend fun updateDateInfo() = withContext(Dispatchers.IO) {
        val calendarDate = model.chosenCalendarDate.value!!

        val insertData = db.insert(
            BookingData(
                id = 0 ,
                flatId = getFlatId(),
                bookingDate = calendarDate,
                deposit = binding.fieldDeposit.text!!.toString().toInt(),
                username = binding.fieldUsername.text!!.toString(),
                userPhone = binding.fieldPhone.text!!.toString().toLong(),
                bookingEnd = binding.fieldDateTo.text.toString(),
                bookingChatLink = binding.fieldChatLink.text!!.toString()
            )
        )

        withContext(Dispatchers.Main) {
            model.shouldUpdateWidget.value = (insertData > 0)
        }
    }

    private fun isDateFree(dateStart: String, dateEnd: String): Boolean {
        val data = db.getListByMonth("${dateStart.dropLast(2)}01","${dateEnd.dropLast(2)}31")
        return if (!data.isNullOrEmpty()) {
            val closerDateIdFirst = DateHelper.getCloserDate(data, dateStart.covertStringToDate())
            val closerDateIdSecond = DateHelper.getCloserDate(data, dateEnd.covertStringToDate())
            //return if we havent closer date on Start/End
            closerDateIdFirst != null && closerDateIdSecond != null
        } else true
    }

    private fun canUpdateDateInfo(): Boolean {

        var canUpdate: Boolean

        binding.filledDepositField.error = if (binding.fieldDeposit.text!!.isEmpty()) {
            canUpdate = false
            getString(R.string.field_empty)
        } else {
            canUpdate = true
            ""
        }

        binding.filledUsernameField.error = if (binding.fieldUsername.text!!.isEmpty()) {
            canUpdate = false
            getString(R.string.field_empty)
        } else {
            canUpdate = true
            ""
        }

        binding.filledPhoneField.error = if (binding.fieldPhone.text!!.isEmpty()) {
            canUpdate = false
            getString(R.string.field_empty)
        } else {
            canUpdate = true
            ""
        }

        binding.filledPhoneField.error = if (binding.fieldDateTo.text!!.isEmpty()) {
            canUpdate = false
            getString(R.string.field_empty)
        } else {
            canUpdate = true
            ""
        }

        binding.filledChatLinkField.error =
            if (binding.fieldChatLink.text!!.toString().isNotEmpty() &&
                Patterns.WEB_URL.matcher(binding.fieldChatLink.text!!.toString()).matches()
            ) {
                getString(R.string.invalid_url)
            } else ""

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                if (isDateFree(model.chosenCalendarDate.value!!, binding.fieldDateTo.text.toString())) {
                    canUpdate = true
                } else {
                    withContext(Dispatchers.Main) {
                        canUpdate = false
                        Snackbar.make(
                            binding.root,
                            getString(R.string.dates_busy) ,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        return canUpdate
    }

    private fun getFlatId(): Int = PreferenceManager.getDefaultSharedPreferences(requireContext()).getInt(FLAT_LAST_KEY , 1)

    private fun initDB() = BookingDatabase.getInstance(requireContext()).bookedDatabaseDao
}