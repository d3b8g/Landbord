package net.d3b8g.landbord.ui.checklist

interface CheckListInterface {

    fun updateRecyclerViewList()

    fun openAddItemModalView(modalState: CheckListModalViewStates)
}