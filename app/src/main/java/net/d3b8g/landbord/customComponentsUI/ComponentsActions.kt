package net.d3b8g.landbord.customComponentsUI

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getSystemService
import net.d3b8g.landbord.R
import net.d3b8g.landbord.customComponentsUI.ComponentsActions.setBackgroundTransparent

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

    fun View.closeKeyBoard() {
        val inputMethodManager: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputMethodManager.isAcceptingText) {
            inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
        }
    }

    fun View.setBackgroundTransparent() {
        alpha = 0.15f

        setBackgroundColor(this.context.getColor(R.color.colorMoreGray))
    }

    fun View.setBackgroundTransparentVisible() {
        alpha = 1.0f
        setBackgroundColor(this.context.getColor(R.color.layoutBackground))
    }
}