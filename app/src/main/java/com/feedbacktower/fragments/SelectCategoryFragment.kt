package com.feedbacktower.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.R
import com.feedbacktower.adapters.CategoryListAdapter
import com.feedbacktower.data.models.BusinessCategory
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.util.gone
import com.feedbacktower.util.setVertical
import com.feedbacktower.util.visible
import kotlinx.android.synthetic.main.dialog_select_category.view.*
import org.jetbrains.anko.toast


class SelectCategoryFragment : DialogFragment() {


    private lateinit var adapter: CategoryListAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var categoryList: RecyclerView
    private lateinit var queryInput: EditText
    private lateinit var clearButton: ImageButton
    var listener: CategorySelectListener? = null

    companion object {
        fun getInstance(): SelectCategoryFragment {
            val fragment = SelectCategoryFragment()
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_select_category, container, false)

        val toolbar = view.toolbar
        toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp)
        toolbar.setNavigationOnClickListener { dismiss() }
        toolbar.title = "Select Your Category"
        adapter = CategoryListAdapter(toggleListener)
        categoryList = view.categoryGridView
        queryInput = view.queryInput
        clearButton = view.clearButton
        progressBar = view.progressBar
        categoryList.setVertical(requireContext())
        categoryList.adapter = adapter
        setTextChangeListener()
        getCategories()
        return view
    }

    private fun setTextChangeListener() {
        queryInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                if (text.isNullOrEmpty()) {
                    getCategories()
                    return
                }

                val keyword = text.toString().trim()

                getCategories(keyword)

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }
    }

    private val toggleListener = object : CategoryListAdapter.ToggleListener {
        override fun categoryToggled(item: BusinessCategory) {
            listener?.onSelect(item)
            dismiss()
        }
    }

    private fun getCategories(keyword: String = "") {
        progressBar.visible()
        ProfileManager.getInstance()
            .getAllCategories(keyword) { CategoriesResponse, error ->
                progressBar.gone()
                if (error != null) {
                    requireContext().toast(error.message ?: getString(R.string.default_err_message))
                    return@getAllCategories
                }
                adapter.submitList(CategoriesResponse?.featured)
            }
    }

    interface CategorySelectListener {
        fun onSelect(category: BusinessCategory)
    }
}
