package net.d3b8g.landbord.ui.widgets.booking

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
import net.d3b8g.landbord.components.Converter.convertStringToDate
import net.d3b8g.landbord.components.Converter.parseDateToModel
import net.d3b8g.landbord.database.Booking.BookingDatabase
import net.d3b8g.landbord.databinding.WidgetBookingInfoBinding

class BookingInfoFragment : Fragment(R.layout.widget_booking_info) {

    private lateinit var binding: WidgetBookingInfoBinding
    private val model: BookingInfoViewModel by activityViewModels()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = WidgetBookingInfoBinding.bind(view)

        with(binding) {
            model.widgetModel.observe(viewLifecycleOwner, { bookingInfoModel ->
                //Set for info layout
                biDate.text = convertDate(parseDateToModel(bookingInfoModel.date).month.toInt(), parseDateToModel(bookingInfoModel.date).day.toInt())
                biUser.text = "${getString(R.string.booked_by)}: ${bookingInfoModel.bookedBy}"
                biPhoneTitle.text = "${getString(R.string.phone)}: "
                biPhone.text = bookingInfoModel.phone.toString()
                biDeposit.text = "${getString(R.string.deposit)}: ${bookingInfoModel.deposit}"

                //Set for editable fields
                filledUsernameFieldInfo.hint = "${getString(R.string.booked_by)}: ${bookingInfoModel.bookedBy}"
                filledPhoneFieldInfo.hint = "${getString(R.string.phone)}: ${bookingInfoModel.phone}"
                filledDepositFieldInfo.hint = "${getString(R.string.deposit)}: ${bookingInfoModel.deposit}"

                biPhone.setOnClickListener {
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

                deleteLeast.setOnClickListener {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val bookingBase = BookingDatabase.getInstance(requireContext()).bookedDatabaseDao
                        bookingBase.deleteBookingUser(bookingInfoModel.id)
                    }
                    model.deleteUserBooking.value = true
                    //Remove cache
                    bookingInfoModel.date.convertStringToDate().deleteBookingInfoByDate()
                }
            })

            updateInfo.setOnClickListener {
                viewInfoLayout.visibility = View.GONE
                changeViewInfoLayout.visibility = View.VISIBLE
                it.visibility = View.GONE
            }

            saveInfo.setOnClickListener {
                viewInfoLayout.visibility = View.VISIBLE
                changeViewInfoLayout.visibility = View.GONE
                updateInfo.visibility = View.VISIBLE
            }
        }
    }
}