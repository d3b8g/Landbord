package net.d3b8g.landbord.ui.add

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.core.content.edit
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.d3b8g.landbord.R
import net.d3b8g.landbord.components.Converter.getTodayDate
import net.d3b8g.landbord.components.Converter.getTodayUnix
import net.d3b8g.landbord.components.Converter.parseDateToModel
import net.d3b8g.landbord.database.Booking.BookingData
import net.d3b8g.landbord.database.Booking.BookingDatabase
import net.d3b8g.landbord.database.Flat.FlatData
import net.d3b8g.landbord.database.Flat.FlatDatabase
import net.d3b8g.landbord.databinding.FragmentAddBinding
import net.d3b8g.landbord.notification.appLog

class AddFragment : Fragment(R.layout.fragment_add) {

    private val addViewModel: AddViewModel by activityViewModels()
    private lateinit var binding: FragmentAddBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentAddBinding.bind(view)

        val nameFlat = binding.fieldName
        val addressFlat = binding.fieldAddress
        val linkFlat = binding.fieldLink

        nameFlat.doOnTextChanged { _ , _ , _ , _ ->
            canShowButton()
        }

        addressFlat.doOnTextChanged { _ , _ , _ , _ ->
            canShowButton()
        }

        when(addViewModel.state.value) {
            AddViewState.ADD_NEW_FLAT -> {
                binding.addInfo.visibility = View.GONE
                binding.generateDemo.visibility = View.GONE
                binding.closeAddFragment.visibility = View.VISIBLE
            }
            AddViewState.NEW_USER -> {
                val limitDate = getTodayUnix() + ((86400 * 7) * 1000)
                PreferenceManager.getDefaultSharedPreferences(requireContext()).edit {
                    putLong("ads_limit", limitDate)
                }
            }
        }

        binding.addNewFlat.setOnClickListener {
            if(linkFlat.text!!.isNotEmpty() && !linkFlat.text!!.matches(Patterns.WEB_URL.toRegex())) {
                binding.filledLinkField.error = getText(R.string.incorrect_link)
            } else {
                correctRedirect()
            }
        }

        binding.addInfo.setOnClickListener { showDialogInfo() }
        binding.generateDemo.setOnClickListener { GenerateDemoApp().generateDemo() }
        binding.closeAddFragment.setOnClickListener { requireActivity().onBackPressed() }
    }

    private fun showDialogInfo() {
        val dialogView = MaterialAlertDialogBuilder(binding.root.context)
        dialogView.setMessage(R.string.add_info_dialog)
        dialogView.setTitle(R.string.info)
        dialogView.setPositiveButton("OK") { _, _ -> dialogView.setCancelable(true) }
        dialogView.show()
    }

    private fun canShowButton() {
        if (binding.fieldName.text!!.length > 3
            && binding.fieldAddress.text!!.length > 12) {
            binding.addNewFlat.visibility = View.VISIBLE
            binding.generateDemo.visibility = View.GONE
        } else {
            binding.addNewFlat.visibility = View.GONE
            if (addViewModel.state.value != null && addViewModel.state.value == AddViewState.NEW_USER)
                binding.generateDemo.visibility = View.VISIBLE
        }
    }

    private fun correctRedirect() {
        PreferenceManager.getDefaultSharedPreferences(binding.root.context).edit {
            putBoolean("have_flats", true)
        }
        lifecycleScope.launch {
            addNewFlat()
        }
        val navigation = AddFragmentDirections.actionNavigationAddToNavigationHome()
        findNavController().navigate(navigation)
    }

    private suspend fun addNewFlat() = withContext(Dispatchers.IO) {
        val db = FlatDatabase.getInstance(binding.root.context).flatDatabaseDao
        db.insert(
            FlatData(
                flatId = 0,
                flatName = binding.fieldName.text!!.toString(),
                flatAddress = binding.fieldAddress.text!!.toString(),
                flatLink = binding.fieldLink.text!!.toString(),
                flatNotes = binding.fieldNotes.text!!.toString()
            )
        )
    }

    inner class GenerateDemoApp {

        fun generateDemo() {
            lifecycleScope.launch {
                createFlatDemo()
                createMockBooking()
            }
            val navigation = AddFragmentDirections.actionNavigationAddToNavigationHome()
            findNavController().navigate(navigation)
        }

        private suspend fun createFlatDemo() = withContext(Dispatchers.IO) {
            val db = FlatDatabase.getInstance(binding.root.context).flatDatabaseDao
            db.insert(
                FlatData(
                    flatId = 0,
                    flatName = "DemoFlat",
                    flatAddress = "131 70th Street, New York",
                    flatLink = "https://www.avito.ru/volgograd/kvartiry/2-k._kvartira_568_m_516_et._2201547501",
                    flatNotes = "Dont smoke, older than 21, no for party and same event, 3 days +"
                )
            )
        }

        private suspend fun createMockBooking() = withContext(Dispatchers.IO) {

            val dataToday = parseDateToModel(getTodayDate())
            val db = BookingDatabase.getInstance(binding.root.context).bookedDatabaseDao
            val yearMonth = "${dataToday.year}-${dataToday.month}"
            if (dataToday.day.toInt() < 28 && dataToday.month.toInt() != 2) {
                for (i in dataToday.day.toInt()..dataToday.day.toInt() + 2) {
                    db.insert(
                        BookingData(
                            id = 0 ,
                            flatId = 1,
                            bookingDate = "${yearMonth}-$i",
                            deposit = (1000..2000).random(),
                            username = "Vasya Ivanov $i",
                            userPhone = 89116487019,
                            bookingEnd = "${yearMonth}-${i+0}",
                            bookingChatLink = ""
                        )
                    )
                }
            } else {
                for (i in 16..19) {
                    db.insert(
                        BookingData(
                            id = 0 ,
                            flatId = 1,
                            bookingDate = "${yearMonth}-$i",
                            deposit = (1000..2000).random(),
                            username = "Vasya Ivanov $i",
                            userPhone = 89116487019,
                            bookingEnd = "${yearMonth}-${i+0}",
                            bookingChatLink = ""
                        )
                    )
                }
            }
        }
    }
}