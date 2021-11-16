package net.d3b8g.landbord.customComponentsUI

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import net.d3b8g.landbord.R

@SuppressLint("CustomViewStyleable", "Recycle")
class FragmentHeader @JvmOverloads constructor(
    context: Context ,
    attrs: AttributeSet? = null ,
    defStyle: Int = 0 ,
) : LinearLayout(context, attrs, defStyle) {

    private val titleView: TextView
    private val leftButton: ImageButton
    private val rightButton: ImageButton

    init {
        inflate(context, R.layout.fragment_header, this)
        orientation = VERTICAL

        titleView = findViewById(R.id.fragment_header_title)
        leftButton = findViewById(R.id.fragment_header_left_button)
        rightButton = findViewById(R.id.fragment_header_right_button)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.fragmentsHeader)
            val headerTitle = typedArray.getString(R.styleable.fragmentsHeader_headerTitle)
            titleView.text = headerTitle
        }
    }

    fun setTitleText(title: String) {
        titleView.text = title
    }

    fun setRightButtonIcon(drawable: Drawable, onClick: OnClickListener?) {
        rightButton.visibility = View.VISIBLE
        rightButton.background = drawable
        rightButton.setOnClickListener(onClick)
    }

    fun setLeftButtonIcon(drawable: Drawable, onClick: OnClickListener?) {
        leftButton.visibility = View.VISIBLE
        leftButton.background = drawable
        leftButton.setOnClickListener(onClick)
    }

}