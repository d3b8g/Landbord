package net.d3b8g.landbord.widgets.add_info

import android.annotation.SuppressLint
import android.app.ProgressDialog.show
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import net.d3b8g.landbord.R
import net.d3b8g.landbord.databinding.WidgetAddInfoBinding
import java.text.SimpleDateFormat
import java.util.*

class AddInfoFragment : Fragment(R.layout.widget_add_info) {

    lateinit var binding: WidgetAddInfoBinding
    private val model: AddInfoViewModel by activityViewModels()

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = WidgetAddInfoBinding.bind(view)

        binding.closeWidget.setOnClickListener {
            model.widgetSetState.value = true
        }

        binding.fieldDateTo.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->

                if(hasFocus) binding.fieldDateTo.hint = "YYYY-MM-DD"
                else binding.fieldDateTo.hint = ""

                val datePicker =
                    MaterialDatePicker.Builder.datePicker()
                        .setTitleText(getString(R.string.date_to))
                        .build()

                datePicker.show(parentFragmentManager, "DateOfEnd")
                datePicker.addOnPositiveButtonClickListener {
                    val today = SimpleDateFormat("yyyy-MM-DD").format(Date())
                    if(it < Date().time) {
                        Snackbar.make(view, getString(R.string.date_early_error), Snackbar.LENGTH_SHORT).show()
                        binding.fieldDateTo.setText(today)
                    }
                }
            }
    }
}