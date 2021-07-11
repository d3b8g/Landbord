package net.d3b8g.landbord.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListPopupWindow
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import net.d3b8g.landbord.R
import net.d3b8g.landbord.database.Flat.FlatDatabase
import net.d3b8g.landbord.databinding.FragmentHomeBinding
import net.d3b8g.landbord.widgets.statistic.StatisticFragment

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val db = FlatDatabase.getInstance(requireContext()).flatDatabaseDao
        val viewModelFactory = HomeViewModelFactory(db, requireActivity().application)
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

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val FLAT_LAST_KEY = "flat_last"
    }
}