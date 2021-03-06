package com.feedbacktower.ui.account.business

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.App
import com.feedbacktower.R
import com.feedbacktower.adapters.AccountOptionsAdapter
import com.feedbacktower.adapters.CountAdapter
import com.feedbacktower.data.ApplicationPreferences
import com.feedbacktower.data.local.models.AccountOption
import com.feedbacktower.data.local.models.Count
import com.feedbacktower.data.models.User
import com.feedbacktower.databinding.FragmentBusinessAccountBinding
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.MyBusinessResponse
import com.feedbacktower.ui.base.BaseViewFragmentImpl
import com.feedbacktower.ui.location.live.MapTrackingFragment
import com.feedbacktower.ui.location.live.TrackerService
import com.feedbacktower.ui.myplan.MyPlanScreen
import com.feedbacktower.ui.qrtransfer.receiver.ReceiverActivity
import com.feedbacktower.ui.splash.SplashScreen
import com.feedbacktower.util.launchActivity
import com.feedbacktower.util.noZero
import com.feedbacktower.util.showAppInStore
import org.jetbrains.anko.toast
import java.lang.IllegalStateException
import javax.inject.Inject


class AccountFragment : BaseViewFragmentImpl(), AccountContract.View {
    private val TAG = "AccountFragment"

    @Inject
    lateinit var presenter: AccountPresenter

    private lateinit var binding: FragmentBusinessAccountBinding
    private lateinit var counterGridView: RecyclerView
    private lateinit var accountOptionsView: RecyclerView
    private lateinit var accountOptionsAdapter: AccountOptionsAdapter
    private lateinit var countAdapter: CountAdapter
    private lateinit var user: User
    private var trackingService: TrackerService? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity().applicationContext as App).appComponent.accountComponent().create()
            .inject(this)
        binding = FragmentBusinessAccountBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).supportActionBar?.show()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter.attachView(this)
        user = presenter.user ?: throw IllegalStateException("User must not be null")
        initUi()
    }

    private fun initUi() {
        counterGridView = binding.counterGridView
        accountOptionsView = binding.accountOptionsView

        //setup counter grid list
        counterGridView.setHasFixedSize(true)
        countAdapter = CountAdapter(onCountClick)
        counterGridView.adapter = countAdapter
        submitCounts()

        //setup options list
        accountOptionsView.setHasFixedSize(true)
        accountOptionsAdapter = AccountOptionsAdapter(onItemSelected)
        accountOptionsView.adapter = accountOptionsAdapter
        submitOptions()

        binding.availabilitySwitch.setOnClickListener {
            presenter.changeAvailability()
        }
        binding.business = user.business
        binding.editProfileButtonClicked = View.OnClickListener {
            val dir =
                AccountFragmentDirections.actionNavigationAccountToEditBusinessFragment()
            findNavController().navigate(dir)
        }
        binding.onScanClicked = View.OnClickListener {
            requireActivity().launchActivity<ReceiverActivity>()
        }

    }


    override fun showAvailabilityChangeProgress() {
        binding.updatingStatus = true
    }

    override fun dismissAvailabilityChangeProgress() {
        binding.updatingStatus = false
    }

    override fun onAvailabilityChanged(availability: Boolean) {
        binding.availabilitySwitch.isChecked = availability
    }

    override fun onResume() {
        super.onResume()
        presenter.fetch()
    }

    override fun showProgress() {
        super.showProgress()

    }

    override fun dismissProgress() {
        super.dismissProgress()

    }

    override fun onPause() {
        super.onPause()
        presenter.destroyView()
    }

    override fun showNetworkError(error: ApiResponse.ErrorModel) {
        super.showNetworkError(error)
        requireActivity().toast(error.message)
    }

    override fun onFetched(response: MyBusinessResponse?) {
        submitOptions()
        val expired = response?.business?.planStatus == "EXPIRED"
        if (expired) {
            //requireActivity().launchActivity<MyPlanScreen>()
            requireActivity().toast("Your plan expired!")
        }
    }

    override fun onDestroy() {
        presenter.destroyView()
        Log.d("AccountFrag", "View Destroyed")
        super.onDestroy()
    }

    private fun submitOptions() {
        val business = presenter.user?.business ?: return
        val options = listOf(
            AccountOption(
                9,
                "Change City",
                "Your current city: ${user.city?.name}",
                R.drawable.ic_post_like_filled
            ),
            AccountOption(
                6,
                "Wallet Transactions",
                "Wallet balance ₹${business.walletAmount}, Business Index: ₹${business.discountAmount}",
                R.drawable.ic_post_like_filled
            ),
            AccountOption(2, "My Reviews", "Reviews given by you", R.drawable.ic_post_like_filled),
            AccountOption(
                3,
                "My Suggestions",
                "Suggestions given by you",
                R.drawable.ic_post_like_filled
            ),
            AccountOption(8, "My Posts", "Manage your posts", R.drawable.ic_post_like_filled),
            AccountOption(1, "Subscription", "Current plan", R.drawable.ic_post_like_filled),
            AccountOption(
                7,
                "Location tracking",
                "Update your location automatically",
                R.drawable.ic_post_like_filled
            ),
            AccountOption(4, "Help", "Help and FAQs", R.drawable.ic_post_like_filled),
            AccountOption(
                10,
                "Rate this app",
                "Review app on Play Store",
                R.drawable.ic_post_like_filled
            ),
            AccountOption(
                5,
                "Logout",
                "Logout from ${getString(R.string.app_name)}",
                R.drawable.ic_post_like_filled
            )
        )
        accountOptionsAdapter.submitList(options)
    }

    private val onCountClick = { count: Count ->
        if (count.id == 3) {
            val d =
                AccountFragmentDirections.actionNavigationAccountToNavigationReviewFragment()
            findNavController().navigate(d)
        } else if (count.id == 4) {
            val d =
                AccountFragmentDirections.actionNavigationAccountToNavigationSuggestionFragment()
            findNavController().navigate(d)
        }
    }

    private val onItemSelected = { option: AccountOption ->
        Log.d(TAG, "${option.title} selected")
        when (option.id) {
            1 -> {
                requireActivity().launchActivity<MyPlanScreen>()
            }
            2 -> {
                val d =
                    AccountFragmentDirections.actionNavigationAccountToMyReviewsFragment()
                findNavController().navigate(d)
            }
            3 -> {
                val d =
                    AccountFragmentDirections.actionNavigationAccountToMySuggestionsFragment()
                findNavController().navigate(d)
            }
            4 -> {
                val d =
                    AccountFragmentDirections.actionNavigationAccountToHelpFragment()
                findNavController().navigate(d)
            }
            5 -> {
                try {
                    if (trackingService == null) {
                        val intent = Intent(requireActivity().application, TrackerService::class.java)
                        requireActivity().application.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
                        Handler().postDelayed({
                            trackingService?.stopTracking()
                        }, 500)
                    } else {
                        trackingService?.stopTracking()
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "Error stopping service: $e")
                }
                presenter.logOut()
            }
            6 -> {
                val d =
                    AccountFragmentDirections.actionNavigationAccountToWalletTransactionFragment()
                findNavController().navigate(d)
            }
            7 -> {
                val d =
                    AccountFragmentDirections.actionNavigationAccountToMapTrackingFragment()
                findNavController().navigate(d)
            }
            8 -> {
                val businessId = user.business?.id
                if (businessId != null) {
                    AccountFragmentDirections.actionNavigationAccountToMyPostsFragment(businessId)
                        .let { d ->
                            findNavController().navigate(d)
                        }
                }
            }
            9 -> {
                val d =
                    AccountFragmentDirections.actionNavigationAccountToSelectCityFragment()
                findNavController().navigate(d)
            }
            10 -> {
                requireActivity().showAppInStore()
            }
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val name = className.className
            if (name.endsWith("TrackerService")) {
                trackingService = (service as TrackerService.LocationServiceBinder).service
            }
        }

        override fun onServiceDisconnected(className: ComponentName) {
            if (className.className == "TrackerService") {
                trackingService = null
            }
        }
    }

    private fun submitCounts() {
        user.business?.let { business ->
            val counts = listOf(
                Count(1, business.rank.noZero(), "Rank"),
                Count(2, "${business.avgRating}", "Ratings"),
                Count(3, "${business.totalReviews}", "Reviews"),
                Count(4, "${business.totalSuggestions}", "Suggestions")
            )
            countAdapter.submitList(counts)
        }
    }

    override fun onLogOut() {
        launchActivity<SplashScreen> {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
    }
}
