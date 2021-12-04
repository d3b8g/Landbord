package net.d3b8g.landbord.ui.home

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListPopupWindow
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import net.d3b8g.landbord.R
import net.d3b8g.landbord.components.Converter.convertDateToPattern
import net.d3b8g.landbord.components.Converter.convertUnixToDate
import net.d3b8g.landbord.components.Converter.getTodayDate
import net.d3b8g.landbord.database.Booking.BookingData
import net.d3b8g.landbord.database.Booking.BookingDatabase
import net.d3b8g.landbord.database.Flat.FlatDatabase
import net.d3b8g.landbord.databinding.FragmentHomeBinding
import net.d3b8g.landbord.models.BookingInfoModel
import net.d3b8g.landbord.payments.PaymentsDetails.isSponsor
import net.d3b8g.landbord.ui.add.AddViewModel
import net.d3b8g.landbord.ui.add.AddViewState
import net.d3b8g.landbord.ui.widgets.add_info.AddInfoFragment
import net.d3b8g.landbord.ui.widgets.add_info.AddInfoViewModel
import net.d3b8g.landbord.ui.widgets.booking.BookingInfoFragment
import net.d3b8g.landbord.ui.widgets.booking.BookingInfoViewModel
import net.d3b8g.landbord.ui.widgets.statistic.StatisticFragment

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private val modelAddInfo: AddInfoViewModel by activityViewModels()
    private val modelDateInfo: BookingInfoViewModel by activityViewModels()
    private val addViewModel: AddViewModel by activityViewModels()

    private val dbFlat by lazy { initFlat() }
    private val dbBooking by lazy { initBooking() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentHomeBinding.bind(view)

        val viewModelFactory = HomeViewModelFactory(dbFlat, dbBooking, requireActivity().application)
        homeViewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]

        // UI components
        val dropDownFlat: Button = binding.flatBtn
        val addNewFlat: ImageButton = binding.homeAddNewFlat
        val listPopupWindow = ListPopupWindow(requireContext(), null, R.attr.listPopupWindowStyle)

        listPopupWindow.anchorView = dropDownFlat

        binding.homeNotification.setOnClickListener {
            val action = HomeFragmentDirections.actionNavigationHomeToNavigationNotification()
            findNavController().navigate(action)
        }

        homeViewModel.flatList.observe(viewLifecycleOwner, {
            dropDownFlat.text = it[0]

            val adapter = ArrayAdapter(requireContext(), R.layout.cell_list_popup_window, it)
            listPopupWindow.setAdapter(adapter)

            dropDownFlat.setOnClickListener { listPopupWindow.show() }
        })

        homeViewModel.getBookingData(getTodayDate()).observe(viewLifecycleOwner, {
            initCalendarWidget(it, getTodayDate())
        })

        homeViewModel.haveBooking.observe(viewLifecycleOwner, {
            if (it) {
                generateStatisticsWidget()
            }
        })

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val correctDateFormat = convertDateToPattern("${year}-${month+1}-${dayOfMonth}")
            homeViewModel.getBookingData(correctDateFormat).observe(viewLifecycleOwner, {
                initCalendarWidget(it, correctDateFormat)
            })
        }

        modelAddInfo.widgetSetState.observe(viewLifecycleOwner, {
            if (it) {
                //binding.widgetInfo.visibility = View.GONE
            }
        })

        modelAddInfo.shouldUpdateWidget.observe(viewLifecycleOwner, {
            if (it != null) {
                generateInfoWidget()
                generateStatisticsWidget()
                val pickedDate = convertUnixToDate(binding.calendarView.date)
                setBookingInfo(it, pickedDate)
            }
        })

        modelDateInfo.deleteUserBooking.observe(viewLifecycleOwner, {
            if (it) {
                generateAddWidget()
                generateStatisticsWidget()
            }
        })

        addNewFlat.setOnClickListener {
            val navigation = HomeFragmentDirections.actionNavigationHomeToNavigationAdd()
            addViewModel.state.value = AddViewState.ADD_NEW_FLAT
            findNavController().navigate(navigation)
        }

        isSponsor.observe(viewLifecycleOwner, {
            if (it) binding.homePaymentsWidget.visibility = View.GONE
            else binding.homePaymentsWidget.visibility = View.VISIBLE
        })
    }

    private fun generateStatisticsWidget() {
        parentFragmentManager.beginTransaction()
            .add(binding.widgetHome.id, StatisticFragment())
            .commit()
    }

    private fun generateAddWidget() {
        parentFragmentManager.beginTransaction()
            .replace(binding.widgetInfo.id, AddInfoFragment())
            .commit()
    }

    private fun generateInfoWidget() = parentFragmentManager.beginTransaction()
            .replace(binding.widgetInfo.id, BookingInfoFragment())
            .commit()

    private fun setBookingInfo(date: BookingData, dateChosen: String) {
        modelDateInfo.widgetModel.value = BookingInfoModel(
            id = date.id,
            date = dateChosen,
            bookedBy = date.username,
            phone = date.userPhone,
            deposit = date.deposit
        )
    }

    private fun initCalendarWidget(bookingData: BookingData?, dateFormat: String) {
        if (bookingData == null) {
            modelAddInfo.chosenCalendarDate.value = dateFormat
            generateAddWidget()
        } else {
            generateInfoWidget()
            lifecycleScope.launch { setBookingInfo(bookingData, dateFormat) }
        }
    }

    private fun initBooking() = BookingDatabase.getInstance(requireContext()).bookedDatabaseDao
    private fun initFlat() = FlatDatabase.getInstance(requireContext()).flatDatabaseDao

    companion object {
        const val FLAT_LAST_KEY = "flat_last"
    }
}