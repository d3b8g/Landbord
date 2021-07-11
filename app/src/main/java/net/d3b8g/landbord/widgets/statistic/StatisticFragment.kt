package net.d3b8g.landbord.widgets.statistic

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import net.d3b8g.landbord.R
import net.d3b8g.landbord.database.Booking.BookingDatabase
import net.d3b8g.landbord.databinding.WidgetStatisticsBinding
import java.util.*

class StatisticFragment : Fragment(R.layout.widget_statistics) {

    private lateinit var homeViewModel: StatisticViewModel
    lateinit var binding: WidgetStatisticsBinding

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = WidgetStatisticsBinding.bind(view)

        val db = BookingDatabase.getInstance(requireContext()).bookedDatabaseDao
        val viewModelFactory = StatisticViewModelFactory(db, requireActivity().application)
        homeViewModel = ViewModelProvider(this, viewModelFactory).get(StatisticViewModel::class.java)

        homeViewModel.statisticsBookingData.observe(viewLifecycleOwner, {
            binding.apply {
                bookedInTt.text = getString(R.string.booked_in) +
                        Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
                bookedInCount.text = it.count().toString() + getString(R.string.of) + Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)
            }
        })
    }

}