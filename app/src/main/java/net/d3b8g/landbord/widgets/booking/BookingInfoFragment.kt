package net.d3b8g.landbord.widgets.booking

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.d3b8g.landbord.R
import net.d3b8g.landbord.cache.AppCache.deleteBookingInfoByDate
import net.d3b8g.landbord.components.Converter.convertDate
import net.d3b8g.landbord.components.Converter.covertStringToDate
import net.d3b8g.landbord.components.Converter.parseDateToModel
import net.d3b8g.landbord.database.Booking.BookingDatabase
import net.d3b8g.landbord.databinding.WidgetBookingInfoBinding

class BookingInfoFragment : Fragment(R.layout.widget_booking_info) {

    private lateinit var binding: WidgetBookingInfoBinding
    private val model: BookingInfoViewModel by activityViewModels()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = WidgetBookingInfoBinding.bind(view)

        model.widgetModel.observe(viewLifecycleOwner, { bookingInfoModel ->
            //Set for info layout
            binding.biDate.text = convertDate(parseDateToModel(bookingInfoModel.date).month.toInt(), parseDateToModel(bookingInfoModel.date).day.toInt())
            binding.biUser.text = "${getString(R.string.booked_by)}: ${bookingInfoModel.bookedBy}"
            binding.biPhoneTitle.text = "${getString(R.string.phone)}: "
            binding.biPhone.text = bookingInfoModel.phone.toString()
            binding.biDeposit.text = "${getString(R.string.deposit)}: ${bookingInfoModel.deposit}"

            //Set for editable fields
            binding.filledUsernameFieldInfo.hint = "${getString(R.string.booked_by)}: ${bookingInfoModel.bookedBy}"
            binding.filledPhoneFieldInfo.hint = "${getString(R.string.phone)}: ${bookingInfoModel.phone}"
            binding.filledDepositFieldInfo.hint = "${getString(R.string.deposit)}: ${bookingInfoModel.deposit}"

            binding.biPhone.setOnClickListener {
                if (ContextCompat.checkSelfPermission(requireActivity(),
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), 1013)
                } else {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.title_call_tenant)
                        .setPositiveButton(R.string.call) {_, _ ->
                            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:${bookingInfoModel.phone}"))
                            startActivity(intent)
                        }
                        .setNegativeButton(R.string.close) {d, _ ->
                            d.dismiss()
                        }
                        .show()
                }
            }

            binding.deleteLeast.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    val bookingBase = BookingDatabase.getInstance(requireContext()).bookedDatabaseDao
                    bookingBase.deleteBookingUser(bookingInfoModel.id)
                }
                model.deleteUserBooking.value = true
                //Remove cache
                bookingInfoModel.date.covertStringToDate().deleteBookingInfoByDate()
            }
        })

        binding.updateInfo.setOnClickListener {
            binding.viewInfoLayout.visibility = View.GONE
            binding.changeViewInfoLayout.visibility = View.VISIBLE
            it.visibility = View.GONE
        }

        binding.saveInfo.setOnClickListener {
            binding.viewInfoLayout.visibility = View.VISIBLE
            binding.changeViewInfoLayout.visibility = View.GONE
            binding.updateInfo.visibility = View.VISIBLE
        }


    }
}