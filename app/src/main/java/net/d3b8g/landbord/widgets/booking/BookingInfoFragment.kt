package net.d3b8g.landbord.widgets.booking

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
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
        binding = WidgetBookingInfoBinding.bind(view)

        model.widgetModel.observe(viewLifecycleOwner, { bookingInfoModel ->
            binding.biDate.text = convertDate(parseDateToModel(bookingInfoModel.date).month.toInt(), parseDateToModel(bookingInfoModel.date).day.toInt())
            binding.biUser.text = "Booked by: ${bookingInfoModel.bookedBy}"
            binding.biPhone.text = "Phone: ${bookingInfoModel.phone}"
            binding.biDeposit.text = "Deposit: ${bookingInfoModel.deposit}"

            binding.biPhone.setOnClickListener {
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:${bookingInfoModel.phone}"))
                startActivity(intent)
            }
        })
    }

    private fun convertDate(month: Int, day: Int) = when (Locale.getDefault().displayLanguage) {
        "русский" -> "$day " + when (month) {
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