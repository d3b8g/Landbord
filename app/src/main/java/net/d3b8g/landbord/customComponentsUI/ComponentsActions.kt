package net.d3b8g.landbord.customComponentsUI

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View

object ComponentsActions {

    fun View.vibrateDevice() {
        val vibrateMills = 100L
        when (true) {
            Build.VERSION.SDK_INT > 30 -> {
                val vibratorManager = context?.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                val vibrator = vibratorManager.defaultVibrator
                vibrator.vibrate(VibrationEffect.createOneShot(vibrateMills, VibrationEffect.DEFAULT_AMPLITUDE))
            }
            else -> {
                val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(vibrateMills, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    vibrator.vibrate(vibrateMills)
                }
            }
        }
    }
}