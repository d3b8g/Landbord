package net.d3b8g.landbord.widgets.statistic

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import net.d3b8g.landbord.R
import net.d3b8g.landbord.components.Converter.parseDateToModel
import net.d3b8g.landbord.database.Booking.BookingDatabase
import net.d3b8g.landbord.databinding.WidgetStatisticsBinding
import java.util.*

class StatisticFragment : Fragment(R.layout.widget_statistics) {

    private lateinit var homeViewModel: StatisticViewModel
    lateinit var binding: WidgetStatisticsBinding

    private val maxDaysMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = WidgetStatisticsBinding.bind(view)

        val db = BookingDatabase.getInstance(requireContext()).bookedDatabaseDao
        val viewModelFactory = StatisticViewModelFactory(db, requireActivity().application)
        homeViewModel = ViewModelProvider(this, viewModelFactory).get(StatisticViewModel::class.java)

        homeViewModel.statisticsBookingData.observe(viewLifecycleOwner, {
            binding.apply {

                var counterBusyDays = 0
                it.forEachIndexed { index, bookingData ->
                    val res = parseDateToModel(bookingData.bookingEnd).day.toInt() + 1 - parseDateToModel(bookingData.bookingDate).day.toInt()
                    counterBusyDays += res
                }

                bookedInTt.text = "${getString(R.string.booked_in)} ${correctMonthName()}:"
                bookedInCount.text = counterDays(counterBusyDays)

                busyInTt.text = "${getString(R.string.busy_in_tt)} ${correctMonthName()}:"
                busyInCount.text = counterDays(counterBusyDays)
            }
        })
    }

    private fun correctMonthName() : String {
        val monthNow = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())!!
        return when(Locale.getDefault().displayLanguage) {
            "русский" -> monthNow.dropLast(1) + "е"
            else -> monthNow
        }
    }

    private fun counterDays(count: Int) : String = "$count ${getString(R.string.of)} $maxDaysMonth"
}