package net.d3b8g.landbord.ui.checklist

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
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
    private var modalState: CheckListModalViewStates = CheckListModalViewStates.ADD_NEW

    private val header: FragmentHeader
    private val nameCL: TextInputEditText
    private val nameLayout: TextInputLayout
    private val inputDate: InputDatePicker
    private val isRepeatable: CheckBox

    init {
        inflate(context, R.layout.modal_page_check_list, this)
        orientation = VERTICAL

        header = findViewById(R.id.checkListAddHeader)
        nameCL = findViewById(R.id.fieldCheckListTitle)
        nameLayout = findViewById(R.id.filledCheckListTitle)
        inputDate = findViewById(R.id.checkListRepeatDate)
        isRepeatable = findViewById(R.id.checkListIsRepeatable)

        findViewById<Button>(R.id.checkListSaveNew).setOnClickListener {
            if (nameCL.text!!.length < 3) {
                nameLayout.error = resources.getString(R.string.error_name_check_list)
            }

            if (inputDate.isDateCurrent() && nameCL.text!!.length > 3) {
                val checkListDatabase = CheckListDatabase.getInstance(context).checkListDatabaseDao

                scope.launch {
                    checkListDatabase.selectSimilarItem(nameCL.text.toString()).also {
                        val clModel = CheckListData(
                            id = it,
                            title = nameCL.text.toString(),
                            nextRepeat = inputDate.pickedDateString,
                            isRepeatable = isRepeatable.isChecked
                        )

                        if (it > 0 || modalState == CheckListModalViewStates.EDIT_ITEM) {
                            checkListDatabase.update(clModel)
                            appLog(this, "${checkListDatabase.update(clModel)} acad ${clModel}")
                        }
                        else checkListDatabase.insert(clModel)
                    }
                }
            }
        }

    }

    fun setupModalPageState(state: CheckListModalViewStates) {
        modalState = when(state) {
            CheckListModalViewStates.ADD_NEW -> {
                header.setTitleText(resources.getString(R.string.add_new_check_list))
                state
            }
            CheckListModalViewStates.EDIT_ITEM -> {
                header.setTitleText(resources.getString(R.string.edit_item))
                state
            }
        }
    }

    fun onCloseModalView(onClickListener: OnClickListener) {
        header.setRightButtonIcon(ContextCompat.getDrawable(context, R.drawable.ic_close)!!, onClickListener)

        nameCL.setText("")
        nameCL.clearFocus()
        nameLayout.error = null
        isRepeatable.isChecked = false
        inputDate.clearInput()
    }

    fun slideUp() {
        val animationOpen = TranslateAnimation(0F,0F, this.height.toFloat(), 0f).apply {
            duration = 200
            fillAfter = true
        }
        this.startAnimation(animationOpen)
    }

}