package net.d3b8g.landbord.ui.income

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import net.d3b8g.landbord.R
import net.d3b8g.landbord.databinding.FragmentIncomeBinding

class IncomeFragment : Fragment(R.layout.fragment_income) {

    private lateinit var incomeViewModel: IncomeViewModel
    private lateinit var binding: FragmentIncomeBinding

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        binding = FragmentIncomeBinding.bind(view)
        incomeViewModel = ViewModelProvider(this).get(IncomeViewModel::class.java)

        incomeViewModel.text.observe(viewLifecycleOwner, {

        })
    }
}