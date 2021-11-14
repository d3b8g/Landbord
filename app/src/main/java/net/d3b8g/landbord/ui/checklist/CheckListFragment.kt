package net.d3b8g.landbord.ui.checklist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.d3b8g.landbord.R
import net.d3b8g.landbord.databinding.FragmentCheckListBinding
import net.d3b8g.landbord.models.CheckListItemModel

class CheckListFragment : Fragment(R.layout.fragment_check_list) {

    private lateinit var checklistViewModel: CheckListViewModel
    private lateinit var binding: FragmentCheckListBinding
    private lateinit var adapter: CheckListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentCheckListBinding.bind(view)
        checklistViewModel = ViewModelProvider(this).get(CheckListViewModel::class.java)

        adapter = CheckListAdapter()
        binding.checkListRcv.apply {
            adapter = adapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            setHasFixedSize(false)
        }

        checklistViewModel.checkList(requireContext()).observe(viewLifecycleOwner, {
            if (it.size > 0 && binding.checklistBlockAssert.visibility == View.VISIBLE) {
                binding.checklistBlockAssert.visibility = View.GONE
            }
            adapter.updateList(it)
        })
    }

    private fun convertArrayToJSONString(list: ArrayList<CheckListItemModel>): String {
        //putString("check_list_prefs", convertArrayToJSONString(aboab))
        val correctString = StringBuilder()
        list.forEachIndexed { index, checkListItemModel ->
            correctString.append("{")
            correctString.append("\"title\":\"${checkListItemModel.title}\",")
            correctString.append("\"isCompleted\":${checkListItemModel.isCompleted},")
            correctString.append("\"isRepeatable\":${checkListItemModel.isRepeatable},")
            correctString.append("\"nextRepeat\":\"${checkListItemModel.nextRepeat}\"")
            if (index != list.size - 1) correctString.append("},")
            else correctString.append("}")
        }
        return "[$correctString]"
    }
}