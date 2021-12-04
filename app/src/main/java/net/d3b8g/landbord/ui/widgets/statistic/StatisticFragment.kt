package net.d3b8g.landbord.ui.widgets.statistic

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import net.d3b8g.landbord.R
import net.d3b8g.landbord.components.Converter.parseDateToModel
import net.d3b8g.landbord.components.appLog
import net.d3b8g.landbord.database.Booking.BookingDatabase
import net.d3b8g.landbord.database.Checklists.CheckListDatabase
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
        val dbCL = CheckListDatabase.getInstance(requireContext()).checkListDatabaseDao
        val viewModelFactory = StatisticViewModelFactory(db, dbCL, requireActivity().application)
        homeViewModel = ViewModelProvider(this, viewModelFactory)[StatisticViewModel::class.java]

        homeViewModel.statisticsBookingData.observe(viewLifecycleOwner, {
            binding.apply {
                var counterBusyDays = 0
                it.forEachIndexed { _ , bookingData ->
                    val currentMonthInt = Calendar.getInstance().get(Calendar.MONTH) + 1
                    val res: Int = if (parseDateToModel(bookingData.bookingEnd).month.toInt() == currentMonthInt &&
                        parseDateToModel(bookingData.bookingDate).month.toInt() == currentMonthInt) {
                        parseDateToModel(bookingData.bookingEnd).day.toInt() + 1 - parseDateToModel(bookingData.bookingDate).day.toInt()
                    } else {
                        // If started or ended at another month
                        if (parseDateToModel(bookingData.bookingDate).month.toInt() != currentMonthInt) {
                            //Started by another month
                            parseDateToModel(bookingData.bookingEnd).day.toInt()
                        } else {
                            maxDaysMonth - parseDateToModel(bookingData.bookingDate).day.toInt() + 1
                        }
                    }
                    counterBusyDays += res
                }


            }
        })

        homeViewModel.statisticsBuyItems.observe(viewLifecycleOwner, {
            with(binding) {
                busyInTt.text = "${getString(R.string.notification_should_buy)} ${correctMonthName()}:"
                //busyInCount.text = shouldBuyItems.toString()
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