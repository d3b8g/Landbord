package net.d3b8g.landbord.ui.checklist

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.marginLeft
import net.d3b8g.landbord.R
import net.d3b8g.landbord.customComponentsUI.FragmentHeader

class CheckListModalPage @JvmOverloads constructor(
    context: Context ,
    attrs: AttributeSet? = null ,
    defStyle: Int = 0 ,
) : LinearLayout(context, attrs, defStyle) {

    init {
        inflate(context, R.layout.modal_page_check_list, this)

        orientation = VERTICAL
        gravity = Gravity.BOTTOM

        val header = findViewById<FragmentHeader>(R.id.checkListAddHeader)
        header.setRightButtonIcon(
           ContextCompat.getDrawable(context, R.drawable.ic_close)!!
        ) {
            val animation = TranslateAnimation(0F,0F,0F, this.height.toFloat()).apply {
                duration = 300
                fillAfter = true
            }
            this.startAnimation(animation)
        }
    }

}