package net.d3b8g.landbord.ui.home

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListPopupWindow
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.d3b8g.landbord.R
import net.d3b8g.landbord.components.Converter.convertDateToPattern
import net.d3b8g.landbord.components.Converter.convertUnixToDate
import net.d3b8g.landbord.components.Converter.getTodayDate
import net.d3b8g.landbord.database.Booking.BookingDatabase
import net.d3b8g.landbord.database.Flat.FlatDatabase
import net.d3b8g.landbord.databinding.FragmentHomeBinding
import net.d3b8g.landbord.models.BookingInfoModel
import net.d3b8g.landbord.widgets.add_info.AddInfoFragment
import net.d3b8g.landbord.widgets.add_info.AddInfoViewModel
import net.d3b8g.landbord.widgets.booking.BookingInfoFragment
import net.d3b8g.landbord.widgets.booking.BookingInfoViewModel
import net.d3b8g.landbord.widgets.statistic.StatisticFragment

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private val modelAddInfo: AddInfoViewModel by activityViewModels()
    private val modelDateInfo: BookingInfoViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentHomeBinding.bind(view)

        // Initialize BD to call in ModelView
        val dbFlat = FlatDatabase.getInstance(requireContext()).flatDatabaseDao
        val dbBooking = BookingDatabase.getInstance(requireContext()).bookedDatabaseDao
        val viewModelFactory = HomeViewModelFactory(dbFlat, dbBooking, requireActivity().application)
        homeViewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)

        // UI components
        val dropDownFlat: Button = binding.flatBtn
        val listPopupWindow = ListPopupWindow(requireContext(), null, R.attr.listPopupWindowStyle)

        dropDownFlat.text = homeViewModel.flat.value?.flatName
        listPopupWindow.anchorView = dropDownFlat

        homeViewModel.flatList.observe(viewLifecycleOwner, {
            val adapter = ArrayAdapter(requireContext(), R.layout.list_popup_window_item, it)
            listPopupWindow.setAdapter(adapter)

            listPopupWindow.setOnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->

                dropDownFlat.text = it[position]

                PreferenceManager.getDefaultSharedPreferences(requireContext()).edit { putInt(FLAT_LAST_KEY, position) }

                listPopupWindow.dismiss()
            }

            if(it.isNotEmpty()) {
                parentFragmentManager.beginTransaction()
                    .add(binding.widgetHome.id, StatisticFragment())
                    .commit()
            }
        })

        homeViewModel.getBookingData(getTodayDate()).observe(viewLifecycleOwner, {
            if(it != null) {
                generateInfoWidget()
                lifecycleScope.launch { setBookingInfo(getTodayDate()) }
            }
        })

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val correctDateFormat = convertDateToPattern("${year}-${month+1}-${dayOfMonth}")
            homeViewModel.getBookingData(correctDateFormat).observe(viewLifecycleOwner, {
                if(it == null) {
                    modelAddInfo.chosenCalendarDate.value = correctDateFormat
                    generateAddWidget()
                } else {
                    if(binding.widgetInfo.visibility == View.GONE) binding.widgetInfo.visibility = View.VISIBLE

                    lifecycleScope.launch { setBookingInfo("${year}-${month+1}-${dayOfMonth}") }
                }
            })
        }

        modelAddInfo.widgetSetState.observe(viewLifecycleOwner, {
            if(it) {
                binding.widgetInfo.visibility = View.GONE
            }
        })

        modelAddInfo.shouldUpdateWidget.observe(viewLifecycleOwner, {
            if(it) {
                generateInfoWidget()
                val pickedDate = convertUnixToDate(binding.calendarView.date)
                lifecycleScope.launch { setBookingInfo(pickedDate) }
            }
        })

        dropDownFlat.setOnClickListener { listPopupWindow.show() }
    }

    private fun generateAddWidget() {
        parentFragmentManager.beginTransaction()
            .replace(binding.widgetInfo.id, AddInfoFragment())
            .commit()

        if(binding.widgetInfo.visibility == View.GONE) binding.widgetInfo.visibility = View.VISIBLE
    }

    private fun generateInfoWidget() = parentFragmentManager.beginTransaction()
            .replace(binding.widgetInfo.id, BookingInfoFragment())
            .commit()

    private suspend fun setBookingInfo(date: String) = withContext(Dispatchers.IO) {
        val connectToBookingBase = BookingDatabase.getInstance(requireContext()).bookedDatabaseDao
        val dataBooking = connectToBookingBase.getByDate(date)

        if(homeViewModel.getBookingData(date).value != null) {
            binding.widgetInfo.visibility = View.VISIBLE
            modelDateInfo.widgetModel.value = BookingInfoModel(
                date = date,
                bookedBy = dataBooking.username,
                phone = dataBooking.userPhone,
                deposit = dataBooking.deposit
            )
        } else {
            withContext(Dispatchers.Main) {
                binding.widgetInfo.visibility = View.GONE
            }
        }
    }

    companion object {
        const val FLAT_LAST_KEY = "flat_last"
    }
}