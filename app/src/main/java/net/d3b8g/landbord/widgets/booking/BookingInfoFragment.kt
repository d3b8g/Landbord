package net.d3b8g.landbord.widgets.booking

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import net.d3b8g.landbord.R
import net.d3b8g.landbord.components.Converter.parseDateToModel
import net.d3b8g.landbord.databinding.WidgetBookingInfoBinding
import java.text.DateFormatSymbols
import java.util.*

class BookingInfoFragment : Fragment(R.layout.widget_booking_info) {

    private lateinit var binding: WidgetBookingInfoBinding
    private val model: BookingInfoViewModel by activityViewModels()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.widgetModel.observe(viewLifecycleOwner, {
            binding.biDate.text = convertDate(parseDateToModel(it.date).month, parseDateToModel(it.date).day)
            binding.biUser.text = "Booked by: ${it.bookedBy}"
            binding.biPhone.text = "Phone: ${it.phone}"
            binding.biDeposit.text = "Deposit: ${it.deposit}"
        })
    }

    private fun convertDate(month: Int, day: Int) = when (Locale.getDefault().displayLanguage) {
        "русский" -> "$day " + when (month + 1) {
            1 -> "Января"
            2 -> "Февраля"
            3 -> "Марта"
            4 -> "Апреля"
            5 -> "Мая"
            6 -> "Июня"
            7 -> "Июля"
            8 -> "Августа"
            9 -> "Сентрября"
            10 -> "Октября"
            11 -> "Ноября"
            12 -> "Декабря"
            else -> "Января"
        }
        else -> "$day of ${DateFormatSymbols().months[month]}"
    }
}