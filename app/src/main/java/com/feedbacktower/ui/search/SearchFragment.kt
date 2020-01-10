package com.feedbacktower.ui.search


import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.App
import com.feedbacktower.adapters.SearchBusinessAdapter
import com.feedbacktower.databinding.FragmentSearchBinding
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.SearchBusiness
import com.feedbacktower.network.models.SearchBusinessResponse
import com.feedbacktower.ui.business_detail.BusinessDetailsActivity
import com.feedbacktower.ui.account.find_customer.FindCustomerActivity
import com.feedbacktower.ui.base.BaseViewFragmentImpl
import com.feedbacktower.util.launchActivity
import com.feedbacktower.util.setVertical
import org.jetbrains.anko.toast
import javax.inject.Inject


class SearchFragment : BaseViewFragmentImpl(), SearchContract.View, SearchBusinessAdapter.Listener {
    @Inject
    lateinit var presenter: SearchPresenter
    private lateinit var searchListView: RecyclerView
    private lateinit var message: TextView
    private lateinit var searchBusinessAdapter: SearchBusinessAdapter
    private var isListEmpty: Boolean? = false
    private var isLoading: Boolean? = false
    private lateinit var clearButton: ImageButton
    private lateinit var queryInput: EditText
    private var list: ArrayList<SearchBusiness> = ArrayList()
    private var fetching = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().applicationContext as App).appComponent.accountComponent().create().inject(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentSearchBinding.inflate(inflater, container, false)
        presenter.attachView(this)
        initUi(binding)
        return binding.root
    }

    private fun initUi(binding: FragmentSearchBinding) {
        searchListView = binding.searchListView
        message = binding.message
        queryInput = binding.queryInput

        //setup list
        searchListView.setVertical(requireContext())

        val layoutManager = LinearLayoutManager(context)
        searchListView.layoutManager = layoutManager
        searchListView.itemAnimator = DefaultItemAnimator()
        searchListView.setHasFixedSize(true)
        searchBusinessAdapter = SearchBusinessAdapter(list, this)
        searchListView.adapter = searchBusinessAdapter
        isLoading = binding.isLoading
        isListEmpty = binding.isListEmpty
        clearButton = binding.clearButton
        binding.onClearClick = View.OnClickListener { binding.queryInput.text = null }

        binding.onScanClick = View.OnClickListener {
            requireActivity().launchActivity<FindCustomerActivity> { }
        }
        binding.queryInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val query: String? = s?.toString()
                clearButton.isVisible = !query.isNullOrEmpty()

                if (query.isNullOrEmpty() || query.length < 2) {
                    list.clear()
                    searchBusinessAdapter.notifyDataSetChanged()
                    return
                }

                search(s.toString().trim())
            }

        })
        binding.queryInput.requestFocus()
        showKeyboard()
        binding.queryInput.setOnEditorActionListener { textView, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                || actionId == EditorInfo.IME_ACTION_DONE
                || event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
            ) {
                binding.queryInput.clearFocus()
                true
            }
            false
        }
        //fetchBusinessList()
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard()
    }

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.let { it.hideSoftInputFromWindow(queryInput.windowToken, 0) }
    }

    private fun showKeyboard() {
        val imgr = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    private fun search(s: String) {
        if (fetching) return
        presenter.fetch(s)
    }

    override fun onItemClick(item: SearchBusiness) {
        requireActivity().launchActivity<BusinessDetailsActivity> {
            putExtra("businessId", item.businessId)
        }
    }

    override fun showProgress() {
        super.showProgress()
        isLoading = true
        fetching = true
    }

    override fun dismissProgress() {
        super.dismissProgress()
        isLoading = false
        fetching = false
    }

    override fun onFetched(response: SearchBusinessResponse?) {
        response?.businesses?.let {
            isListEmpty = it.isEmpty()
            list.clear()
            list.addAll(it)
            searchBusinessAdapter.notifyDataSetChanged()
        }
    }

    override fun showNetworkError(error: ApiResponse.ErrorModel) {
        super.showNetworkError(error)
        requireContext().toast(error.message)
    }
}
