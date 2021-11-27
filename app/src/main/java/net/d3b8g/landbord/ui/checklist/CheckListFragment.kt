package net.d3b8g.landbord.ui.checklist

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.TranslateAnimation
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.d3b8g.landbord.R
import net.d3b8g.landbord.customComponentsUI.ComponentsActions.closeKeyBoard
import net.d3b8g.landbord.customComponentsUI.ComponentsActions.setBackgroundTransparent
import net.d3b8g.landbord.customComponentsUI.ComponentsActions.setBackgroundTransparentVisible
import net.d3b8g.landbord.database.Checklists.CheckListDatabase
import net.d3b8g.landbord.databinding.FragmentCheckListBinding

class CheckListFragment : Fragment(R.layout.fragment_check_list), CheckListInterface {

    private lateinit var binding: FragmentCheckListBinding
    private lateinit var adapterCL: CheckListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentCheckListBinding.bind(view)
        adapterCL = CheckListAdapter(this)

        with(binding) {
            checkListRcv.apply {
                adapter = adapterCL
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                setHasFixedSize(false)
            }

            checkListAddNew.setOnClickListener { openAddItemModalView(CheckListModalViewStates.ADD_NEW) }
            checkListAddNewBtn.setOnClickListener { openAddItemModalView(CheckListModalViewStates.ADD_NEW) }
        }

        updateRecyclerViewList()
    }

    override fun updateRecyclerViewList() {
        lifecycleScope.launch(Dispatchers.IO) {
            val checkListDatabase = CheckListDatabase.getInstance(requireContext()).checkListDatabaseDao
            val checkListItems = ArrayList(checkListDatabase.getAllItems())

            withContext(Dispatchers.Main) {
                if (adapterCL.updateList(checkListItems)) {
                    binding.checklistBlockAssert.visibility = View.GONE
                    binding.checkListRcv.visibility = View.VISIBLE
                    binding.checkListAddNewBtn.visibility = View.VISIBLE
                } else {
                    binding.checklistBlockAssert.visibility = View.VISIBLE
                    binding.checkListRcv.visibility = View.GONE
                    binding.checkListAddNewBtn.visibility = View.GONE
                }
            }
        }
    }

    override fun openAddItemModalView(modalState: CheckListModalViewStates) {
        with(binding) {
            checkListLayout.setBackgroundTransparent()
            addNewModal.apply {
                visibility = View.VISIBLE
                onCloseModalView {
                    val animationClose =
                        TranslateAnimation(0F, 0F, 0F, this.height.toFloat()).apply {
                            duration = 200
                            fillAfter = true
                        }

                    this.startAnimation(animationClose)
                    this.closeKeyBoard()

                    lifecycleScope.launch(Dispatchers.Main) {
                        delay(300)
                        visibility = View.GONE
                        checkListAddNewBtn.visibility = View.VISIBLE
                        checkListLayout.setBackgroundTransparentVisible()
                    }
                }
                slideUp()
            }
            checkListAddNewBtn.visibility = View.GONE
            addNewModal.setupModalPageState(modalState)
        }
    }
}