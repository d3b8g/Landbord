package net.d3b8g.landbord.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListPopupWindow
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import net.d3b8g.landbord.R
import net.d3b8g.landbord.database.Booking.BookingDatabase
import net.d3b8g.landbord.database.Flat.FlatDatabase
import net.d3b8g.landbord.databinding.FragmentHomeBinding
import net.d3b8g.landbord.widgets.add_info.AddInfoFragment
import net.d3b8g.landbord.widgets.add_info.AddInfoViewModel
import net.d3b8g.landbord.widgets.statistic.StatisticFragment
import java.text.DateFormatSymbols
import java.util.*

class HomeFragment : Fragment(){

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private val model: AddInfoViewModel by activityViewModels()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val db_flat = FlatDatabase.getInstance(requireContext()).flatDatabaseDao
        val db_booking = BookingDatabase.getInstance(requireContext()).bookedDatabaseDao
        val viewModelFactory = HomeViewModelFactory(db_flat, db_booking, requireActivity().application)
        homeViewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val dropDownFlat: Button = binding.flatBtn

        homeViewModel.flat.observe(viewLifecycleOwner, {
            dropDownFlat.text = it?.flatName
        })

        val listPopupWindow = ListPopupWindow(requireContext(), null, R.attr.listPopupWindowStyle)
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

        dropDownFlat.setOnClickListener { listPopupWindow.show() }

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            binding.tvDate.text = convertDate(month, dayOfMonth)
            homeViewModel.getBookingData("${year}-${month}-${dayOfMonth}").observe(viewLifecycleOwner, {
                if(it == null) {
                    generateAddWidget()
                }
            })
        }

        model.widgetSetState.observe(viewLifecycleOwner, {
            if(it) {
                binding.widgetAddInfo.visibility = View.GONE
            }
        })

        return root
    }

    private fun generateAddWidget() {
        binding.widgetInfo.visibility = View.GONE

        if(binding.widgetAddInfo.visibility == View.GONE) binding.widgetAddInfo.visibility = View.VISIBLE
        parentFragmentManager.beginTransaction()
            .add(binding.widgetAddInfo.id, AddInfoFragment())
            .commit()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val FLAT_LAST_KEY = "flat_last"
    }
}