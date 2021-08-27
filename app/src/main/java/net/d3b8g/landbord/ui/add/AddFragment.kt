package net.d3b8g.landbord.ui.add

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.d3b8g.landbord.R
import net.d3b8g.landbord.database.Flat.FlatData
import net.d3b8g.landbord.database.Flat.FlatDatabase
import net.d3b8g.landbord.databinding.FragmentAddBinding
import java.net.URL

class AddFragment : Fragment() {
    private lateinit var addViewModel: AddViewModel
    private var _binding: FragmentAddBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        addViewModel = ViewModelProvider(this).get(AddViewModel::class.java)

        _binding = FragmentAddBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val nameFlat = binding.fieldName
        val addressFlat = binding.fieldAddress
        val linkFlat = binding.fieldLink

        nameFlat.doOnTextChanged { _, _, _, count ->
            if(count > 3) canShowButton()
        }

        addressFlat.doOnTextChanged { _, _, _, count ->
            if (count > 12) canShowButton()
        }

        binding.addNewFlat.setOnClickListener {
            if(linkFlat.text!!.isNotEmpty() && !linkFlat.text!!.matches(Patterns.WEB_URL.toRegex())) {
                binding.filledLinkField.error = getText(R.string.incorrect_link)
            } else {
                correctRedirect()
            }
        }

        binding.addInfo.setOnClickListener {
            showDialogInfo()
        }

        return root
    }

    private fun showDialogInfo() {
        val dialogView = MaterialAlertDialogBuilder(binding.root.context)
        dialogView.setMessage(R.string.add_info_dialog)
        dialogView.setTitle(R.string.info)
        dialogView.setPositiveButton("OK") { _, _ -> dialogView.setCancelable(true) }
        dialogView.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun canShowButton() {
        if(binding.fieldName.text!!.isNotEmpty()
            && binding.fieldAddress.text!!.isNotEmpty())
            binding.addNewFlat.visibility = View.VISIBLE
        else
            binding.addNewFlat.visibility = View.GONE
    }

    private fun correctRedirect() {
        PreferenceManager.getDefaultSharedPreferences(binding.root.context).edit {
            putBoolean("have_flats", true)
        }
        lifecycleScope.launch {
            addNewFlat()
        }
        val navigation = AddFragmentDirections.actionNavigationAddToNavigationHome()
        findNavController().navigate(navigation)
    }

    private suspend fun addNewFlat() = withContext(Dispatchers.IO) {
        val db = FlatDatabase.getInstance(binding.root.context).flatDatabaseDao
        db.insert(
            FlatData(
                flatId = 0,
                flatName = binding.fieldName.text!!.toString(),
                flatAddress = binding.fieldAddress.text!!.toString(),
                flatLink = binding.fieldLink.text!!.toString(),
                flatNotes = binding.fieldNotes.text!!.toString()
            )
        )
    }
}