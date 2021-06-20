package net.d3b8g.landbord.ui.income

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import net.d3b8g.landbord.databinding.FragmentIncomeBinding

class IncomeFragment : Fragment() {

    private lateinit var incomeViewModel: IncomeViewModel
    private var _binding: FragmentIncomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        incomeViewModel = ViewModelProvider(this).get(IncomeViewModel::class.java)

        _binding = FragmentIncomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textIncome
        incomeViewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}