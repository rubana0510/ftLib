package com.feedbacktower.ui.profile.setup1

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.feedbacktower.App
import com.feedbacktower.data.models.BusinessCategory
import com.feedbacktower.data.models.User
import com.feedbacktower.databinding.FragmentBusinessSetup1Binding
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.ui.base.BaseViewFragmentImpl
import com.feedbacktower.ui.category.SelectCategoryFragment
import com.feedbacktower.util.disable
import com.feedbacktower.util.enable
import org.jetbrains.anko.toast
import javax.inject.Inject

class BusinessSetup1Fragment : BaseViewFragmentImpl(), BusinessSetup1Contract.View {

    @Inject
    lateinit var presenter: BusinessSetup1Presenter

    private lateinit var binding: FragmentBusinessSetup1Binding
    private var selectedCatId: String? = null
    private var selectedMasterCatId: String? = null

    private val args: BusinessSetup1FragmentArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as App).appComponent.accountComponent().create().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBusinessSetup1Binding.inflate(inflater, container, false)
        presenter.attachView(this)
        initUi()
        return binding.root
    }

    private fun initUi() {

        val business = presenter.user.business
        binding.business = business
        selectedCatId = business?.businessCategory?.id

        binding.businessCatInput.inputType = InputType.TYPE_NULL

        binding.businessCatLayout.isEnabled = !args.edit

        binding.onContinueClick = View.OnClickListener {
            if (valid(
                    binding.businessNameInput.text.toString().trim(),
                    binding.estRegNoInput.text.toString().trim(),
                    selectedCatId
                )
            ) {
                presenter.updateDetails(
                    binding.businessNameInput.text.toString().trim(),
                    binding.estRegNoInput.text.toString().trim(),
                    selectedCatId
                )
            }
        }

        binding.businessCatInput.setOnClickListener {
            val fragment = SelectCategoryFragment.getInstance()
            fragment.listener = object : SelectCategoryFragment.CategorySelectListener {
                override fun onSelect(category: BusinessCategory) {
                    binding.businessCatInput.setText(category.name)
                    selectedCatId = category.id
                    selectedMasterCatId = category.masterBusinessCategoryId
                    presenter.saveMasterCategory(selectedMasterCatId)
                }
            }
            fragment.show(fragmentManager!!, "select_category")
        }
    }

    private fun valid(name: String, regNo: String, category: String?): Boolean {
        binding.businessNameLayout.error = null
        binding.estRegNoLayout.error = null
        binding.businessCatLayout.error = null

        return when {
            name.isEmpty() -> {
                binding.businessNameLayout.error = "Enter valid business name"
                false
            }
            /*  regNo.isEmpty() -> {
                  regLayout.error = "Enter valid registration number"
                  false
              }*/
            category.isNullOrEmpty() -> {
                binding.businessCatLayout.error = "Select valid category"
                false
            }
            else -> true
        }
    }

    private fun navigateNext() {
        if (!args.edit) {
            BusinessSetup1FragmentDirections.actionBusinessSetup1FragmentToPointOnMapFragment()
                .let {
                    findNavController().navigate(it)
                }
            return
        }
        findNavController().navigateUp()
    }

    override fun showProgress() {
        super.showProgress()
        binding.continueButton.disable()
    }

    override fun dismissProgress() {
        super.dismissProgress()
        binding.continueButton.enable()
    }

    override fun onDetailsUpdated(name: String, regNo: String, categoryId: String?) {
        navigateNext()
    }

    override fun showNetworkError(error: ApiResponse.ErrorModel) {
        super.showNetworkError(error)
        requireActivity().toast(error.message)
    }

    override fun onDestroy() {
        presenter.destroyView()
        super.onDestroy()
    }
}
