package net.d3b8g.landbord.ui.checklist

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.*
import net.d3b8g.landbord.R
import net.d3b8g.landbord.components.appLog
import net.d3b8g.landbord.customComponentsUI.FragmentHeader
import net.d3b8g.landbord.customComponentsUI.InputDatePicker
import net.d3b8g.landbord.database.Checklists.CheckListData
import net.d3b8g.landbord.database.Checklists.CheckListDatabase

class CheckListModalPage @JvmOverloads constructor(
    context: Context ,
    attrs: AttributeSet? = null ,
    defStyle: Int = 0 ,
) : LinearLayout(context, attrs, defStyle) {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    init {
        inflate(context, R.layout.modal_page_check_list, this)

        orientation = VERTICAL
        gravity = Gravity.BOTTOM

        val header = findViewById<FragmentHeader>(R.id.checkListAddHeader)
        header.setRightButtonIcon(
           ContextCompat.getDrawable(context, R.drawable.ic_close)!!
        ) {
            val animationClose = TranslateAnimation(0F,0F,0F, this.height.toFloat()).apply {
                duration = 300
                fillAfter = true
            }
            this.startAnimation(animationClose)
        }

        val nameCL = findViewById<TextInputEditText>(R.id.fieldCheckListTitle)
        val nameLayout = findViewById<TextInputLayout>(R.id.filledCheckListTitle)
        val inputDate = findViewById<InputDatePicker>(R.id.checkListRepeatDate)
        val isRepeatable = findViewById<CheckBox>(R.id.checkListIsRepeatable)

        findViewById<Button>(R.id.checkListSaveNew).setOnClickListener {
            if (nameCL.text!!.length < 3) {
                nameLayout.error = resources.getString(R.string.error_name_check_list)
            }

            if (inputDate.isDateCurrent() && nameCL.text!!.length < 3) {
                val checkListDatabase = CheckListDatabase.getInstance(context).checkListDatabaseDao

                scope.launch {
                    checkListDatabase.selectSimilarItem(nameCL.text.toString()).also {
                        val clModel = CheckListData(
                            id = it,
                            title = nameCL.text.toString(),
                            nextRepeat = inputDate.pickedDateString,
                            isRepeatable = isRepeatable.isChecked
                        )

                        if (it > 0) checkListDatabase.update(clModel)
                        else checkListDatabase.insert(clModel)
                    }
                }
            }
        }
    }

    fun slideUp() {
        val animationOpen = TranslateAnimation(0F,0F, this.height.toFloat(), 0f).apply {
            duration = 1500
            fillAfter = true
        }
        this.startAnimation(animationOpen)
    }
}