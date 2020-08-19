package com.feedbacktower.ui.search


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.App
import com.feedbacktower.adapters.CategoryListAdapter
import com.feedbacktower.adapters.SearchBusinessAdapter
import com.feedbacktower.data.models.BusinessCategory
import com.feedbacktower.databinding.FragmentSearchBinding
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.GetCategoriesResponse
import com.feedbacktower.network.models.SearchBusiness
import com.feedbacktower.network.models.SearchBusinessResponse
import com.feedbacktower.ui.account.find_customer.FindCustomerActivity
import com.feedbacktower.ui.base.BaseViewFragmentImpl
import com.feedbacktower.util.*
import com.feedbacktower.util.setVertical
import org.jetbrains.anko.toast
import javax.inject.Inject


class SearchFragment : BaseViewFragmentImpl(), SearchContract.View, SearchBusinessAdapter.Listener,
    CategoryListAdapter.OnItemTappedListener {
    private val TAG = "SearchFragment"

    @Inject
    lateinit var presenter: SearchPresenter
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchListView: RecyclerView
    private lateinit var message: TextView
    private lateinit var searchBusinessAdapter: SearchBusinessAdapter
    private var isBusinessListEmpty: Boolean? = false
    private var isBusinessLoading: Boolean? = false
    private lateinit var clearButton: ImageButton
    private lateinit var queryInput: EditText
    private var list: ArrayList<SearchBusiness> = ArrayList()
    private var fetching = false
    private var categoriesOver: Boolean = false
    private lateinit var categoryListView: RecyclerView
    private var categoryList: ArrayList<BusinessCategory> = ArrayList()
    private lateinit var categoryAdapter: CategoryListAdapter
    private var categoryDynamicPageSize = Constants.CATEGORY_PAGE_SIZE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().applicationContext as App).appComponent.accountComponent().create()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        presenter.attachView(this)
        initUi(binding)
        return binding.root
    }

    private fun initUi(binding: FragmentSearchBinding) {
        searchListView = binding.searchListView
        categoryListView = binding.categoryListView
        message = binding.message
        queryInput = binding.queryInput

        //setup list
        searchListView.setVertical(requireContext())

        val layoutManager = LinearLayoutManager(context)
        searchListView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        searchListView.layoutManager = layoutManager
        searchListView.itemAnimator = DefaultItemAnimator()
        searchListView.setHasFixedSize(true)
        searchBusinessAdapter = SearchBusinessAdapter(list, this)
        searchListView.adapter = searchBusinessAdapter

        val catLayoutManager = LinearLayoutManager(context)
        categoryListView.layoutManager = catLayoutManager
        categoryListView.setHasFixedSize(true)
        categoryAdapter =
            CategoryListAdapter(categoryList, this, mode = CategoryListAdapter.Mode.SELECT)
        categoryListView.adapter = categoryAdapter


        isBusinessLoading = binding.isBusinessLoading
        isBusinessListEmpty = binding.isListEmpty
        clearButton = binding.clearButton
        binding.onClearClick = View.OnClickListener {
            binding.queryInput.text = null
            requireActivity().hideKeyBoard()
        }

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
                binding.goBackLay.gone()
                if (query.isNullOrEmpty()) {
                    categoryListView.isVisible = true
                    searchListView.isVisible = false
                    clearButton.isVisible = false
                } else {
                    categoryListView.isVisible = false
                    searchListView.isVisible = true
                    clearButton.isVisible = true
                }

                if (query.isNullOrEmpty() || query.length < 2) {
                    list.clear()
                    searchBusinessAdapter.notifyDataSetChanged()
                    return
                }

                search(s.toString().trim())
            }

        })
        //binding.queryInput.requestFocus()
        //requireActivity().showKeyboard()
        binding.queryInput.setOnEditorActionListener { textView, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                || actionId == EditorInfo.IME_ACTION_DONE
                || event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
            ) {
                binding.queryInput.clearFocus()
                requireActivity().hideKeyBoard()
                true
            }
            false
        }
        categoryListView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (categoriesOver || categoryList.isEmpty()) return

                val lastPostPosition = catLayoutManager.findLastVisibleItemPosition()
                if (categoryList.size == lastPostPosition + 1 && !presenter.isCategoriesLoading) {
                    presenter.fetchCategories(offset = categoryList.size)
                }
            }
        })

        binding.btnOkay.setOnClickListener {
            showCategoryList()
            binding.goBackLay.gone()
        }

        if (list.isNotEmpty() || !queryInput.text.isNullOrEmpty()) {
            showBusinessList()
        } else {
            showCategoryList()
            presenter.fetchCategories(offset = 0)
        }
    }

    override fun onPause() {
        super.onPause()
        requireActivity().hideKeyBoard()
    }


    private fun search(s: String) {
        if (fetching) return
        presenter.fetch(s)
    }

    override fun onItemClick(item: SearchBusiness, view: View) {
        SearchFragmentDirections.actionNavigationSearchToNavigationBusinessDetail(item.businessId)
            .navigate(view)
    }

    override fun showProgress() {
        super.showProgress()
        //isBusinessLoading = true
        binding.isBusinessLoading = true
        fetching = true
    }

    override fun dismissProgress() {
        super.dismissProgress()
        //isBusinessLoading = false
        binding.isBusinessLoading = false
        fetching = false
    }

    override fun onFetched(response: SearchBusinessResponse?) {
        response?.businesses?.let {
            categoryListView.gone()
            binding.goBackLay.isVisible = it.isEmpty()
            list.clear()
            list.addAll(it)
            searchBusinessAdapter.notifyDataSetChanged()
        }
    }

    override fun onCategoriesFetched(response: GetCategoriesResponse?) {
        response?.featured?.let {
            if (it.size > categoryDynamicPageSize) {
                categoryDynamicPageSize = it.size
            }
            categoryList.addAll(it)
            categoryAdapter.notifyDataSetChanged()
            categoriesOver = it.size < categoryDynamicPageSize
        }
    }

    private fun showBusinessList() {
        searchListView.visible()
        categoryListView.gone()
    }

    private fun showCategoryList() {
        searchListView.gone()
        categoryListView.visible()
        binding.goBackLay.gone()
    }

    override fun showNetworkError(error: ApiResponse.ErrorModel) {
        super.showNetworkError(error)
        requireContext().toast(error.message)
    }

    override fun onDestroy() {
        presenter.destroyView()
        super.onDestroy()
    }

    override fun onTapped(item: BusinessCategory) {
        presenter.fetch(keyword = queryInput.text.trim().toString(), categoryId = item.id)
        showBusinessList()
    }
}
