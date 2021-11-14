package net.d3b8g.landbord.ui.notifications

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.d3b8g.landbord.R
import net.d3b8g.landbord.customComponentsUI.ComponentsActions.vibrateDevice
import net.d3b8g.landbord.databinding.FragmentNotificationsBinding
import net.d3b8g.landbord.notification.*
import net.d3b8g.landbord.notification.NotificationHelper.delayedNotificationAlarm
import java.text.SimpleDateFormat
import java.util.*

class NotificationFragment : Fragment(R.layout.fragment_notifications) {

    private lateinit var binding: FragmentNotificationsBinding

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        binding = FragmentNotificationsBinding.bind(view)

        binding.notificationsRcv.apply {
            getNotificationsJson(requireContext())?.let {
                adapter = NotificationsAdapter(it)

                if (it.notificationsList.isEmpty()) {
                    binding.notificationRcvPlug.visibility = View.VISIBLE
                }
            }
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            setHasFixedSize(false)
        }

        binding.notificationHeader.apply {
            setTitleText(getString(R.string.notifications))
            setLeftButtonIcon(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_back_24)!!
            ) {
                findNavController().popBackStack()
            }
        }

        notificationEnabledCenter()

        lifecycleScope.launch { reproduceAnimation() }

        binding.notificationTimer.text = getNotificationDelay(requireContext())
        binding.notificationTimer.apply {
            setOnClickListener {
                val cal = Calendar.getInstance()
                val timeSetListener = TimePickerDialog.OnTimeSetListener { _ , hour , minute ->
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minute)
                    SimpleDateFormat("HH:mm").format(cal.time).also {
                        text = it
                        setNotificationDelay(requireContext(), it)
                        requireContext().delayedNotificationAlarm()
                    }
                }
                TimePickerDialog(
                    requireContext(),
                    timeSetListener,
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    true
                ).show()
            }
        }
    }

    private fun notificationEnabledCenter() {
        val isNotificationEnable = getNotificationStatus(requireContext())

        if (!isNotificationEnable) {

            val drawableIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_notifications_off_24)!!
            DrawableCompat.setTint(drawableIcon, ContextCompat.getColor(requireContext(), R.color.colorRed))

            binding.notificationHeader.setRightButtonIcon(drawableIcon) {
                setNotificationStatus(requireContext(), true)
                notificationEnabledCenter()
                Snackbar.make(binding.notificationHeader, getString(R.string.notification_enable), Snackbar.LENGTH_SHORT).show()
            }
        } else {
            binding.notificationHeader.setRightButtonIcon(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_notifications_black_24dp)!!
            ) { buttonView ->
                setNotificationStatus(requireContext(), false)
                notificationEnabledCenter()
                Snackbar.make(binding.notificationHeader, getString(R.string.notification_disable), Snackbar.LENGTH_SHORT).show()
                buttonView.vibrateDevice()

                //Rotation Animate
                val valueAnimator = ValueAnimator.ofFloat(-45f, 45f)
                valueAnimator.addUpdateListener {
                    val value = it.animatedValue as Float
                    buttonView.rotation = value
                }
                valueAnimator.interpolator = LinearInterpolator()
                valueAnimator.repeatCount = 2
                valueAnimator.repeatMode = ValueAnimator.REVERSE
                valueAnimator.duration = 80L
                valueAnimator.doOnEnd {
                    buttonView.rotation = 0f
                }
                valueAnimator.start()

                // End of setup animate
            }
        }
    }

    private suspend fun reproduceAnimation() = withContext(Dispatchers.Main) {
        while (true) {
            binding.notificationTimer.animate().alpha(1.0f).duration = 1500L
            delay(2900L)
            binding.notificationTimer.animate().alpha(0.0f).duration = 1500L
            delay(1000L)
        }
    }
}