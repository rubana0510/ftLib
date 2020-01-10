package com.feedbacktower.ui.business_detail


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.App
import com.feedbacktower.adapters.ProfilePostListAdapter
import com.feedbacktower.adapters.ReviewListAdapter
import com.feedbacktower.data.models.Business
import com.feedbacktower.data.models.Post
import com.feedbacktower.data.models.Review
import com.feedbacktower.data.models.User
import com.feedbacktower.databinding.FragmentBusinessDetailBinding
import com.feedbacktower.network.env.Env
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.BusinessDetailsResponse
import com.feedbacktower.network.models.GetPostsResponse
import com.feedbacktower.network.models.GetReviewsResponse
import com.feedbacktower.ui.base.BaseViewFragmentImpl
import com.feedbacktower.ui.map.MapScreen
import com.feedbacktower.ui.reviews.send.RateReviewDialog
import com.feedbacktower.ui.suggestions.send.SendSuggestionDialog
import com.feedbacktower.ui.videoplayer.VideoPlayerScreen
import com.feedbacktower.util.launchActivity
import com.feedbacktower.util.setVertical
import org.jetbrains.anko.toast
import javax.inject.Inject


class BusinessDetailFragment : BaseViewFragmentImpl(), BusinessDetailContract.View {
    @Inject
    lateinit var presenter: BusinessDetailPresenter
    lateinit var user: User
    private lateinit var binding: FragmentBusinessDetailBinding
    private lateinit var reviewListView: RecyclerView
    private lateinit var postListView: RecyclerView
    private lateinit var reviewAdapter: ReviewListAdapter
    private lateinit var postAdapter: ProfilePostListAdapter
    private lateinit var businessId: String
    private var business: Business? = null
    private val list: ArrayList<Review> = ArrayList()
    private val postList: ArrayList<Post> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (requireActivity().applicationContext as App).appComponent.businessDetailComponent().create().inject(this)

        binding = FragmentBusinessDetailBinding.inflate(inflater, container, false)
        businessId = arguments?.getString("businessId") ?: throw  IllegalArgumentException("Invalid args")
        initUi()
        presenter.attachView(this)
        user = presenter.user ?: throw IllegalStateException("User must not be null")
        presenter.fetchBusinessDetails(businessId)
        return binding.root
    }

    private fun initUi() {

        binding.buttonLay.isGone =  user?.business?.id?.equals(businessId) == true
        binding.onViewReviewsClicked = View.OnClickListener {
            val d =
                BusinessDetailFragmentDirections.actionNavigationBusinessDetailToNavigationReview(
                    businessId
                )
            findNavController().navigate(d)
        }
        binding.onViewPostsClicked = View.OnClickListener {
            val d =
                BusinessDetailFragmentDirections.actionNavigationBusinessDetailToNavigationTimeline(
                    businessId
                )
            findNavController().navigate(d)
        }
        binding.onSendSuggestionClicked = View.OnClickListener {
            val d = SendSuggestionDialog(updateListener)
            d.arguments = Bundle().apply { putString("businessId", businessId) }
            d.show(fragmentManager!!, "suggestion")
        }
        binding.onCurrentLocationClicked = View.OnClickListener {
            business?.let {
                requireActivity().launchActivity<MapScreen> {
                    putExtra("LOCATION", it.currentLocation)
                    putExtra("TITLE", "Last Seen At")
                }
            }
        }


        binding.sendReviewButtonClicked = View.OnClickListener {
            val d = RateReviewDialog(updateListener)
            d.arguments = Bundle().apply { putString("businessId", businessId) }
            d.show(fragmentManager!!, "review")
        }

        binding.onCallClicked = View.OnClickListener {
            val phone = business?.phone ?: return@OnClickListener
            startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null)))
        }
        binding.onLocationClicked = View.OnClickListener {
            requireActivity().launchActivity<MapScreen> {
                putExtra("LOCATION", business?.location)
                putExtra("TITLE", "Permanent Location")
            }
        }
        postListView = binding.postListView
        reviewListView = binding.reviewListView

        //setup list
        reviewListView.setVertical(requireContext())
        reviewAdapter = ReviewListAdapter(list)
        reviewListView.adapter = reviewAdapter

        postListView.setVertical(requireContext())
        postAdapter = ProfilePostListAdapter(postList, listener)
        postListView.adapter = postAdapter
    }

    override fun onResume() {
        super.onResume()
    }

    override fun showProgress() {
        super.showProgress()
        binding.isLoading = true
    }

    override fun dismissProgress() {
        super.dismissProgress()
        binding.isLoading = false
    }

    override fun onBusinessDetailFetched(response: BusinessDetailsResponse?) {
        response?.let {
            binding.business = response.business
            business = response.business
            presenter.fetchReviews(businessId)
        }
    }

    override fun onPostsFetched(response: GetPostsResponse?) {
        response?.posts?.let {
            binding.noTimeline = it.isNullOrEmpty()
            postList.clear()
            postList.addAll(it)
            postAdapter.notifyDataSetChanged()
        }
    }

    override fun onReviewsFetched(response: GetReviewsResponse?) {
        response?.review?.let {
            binding.noReviews = it.isNullOrEmpty()
            list.clear()
            list.addAll(it)
            reviewAdapter.notifyDataSetChanged()
            presenter.fetchPosts(businessId, null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroyView()
    }

    override fun showNetworkError(error: ApiResponse.ErrorModel) {
        super.showNetworkError(error)
        requireActivity().toast(error.message)
    }

    private val listener = object : ProfilePostListAdapter.Listener {
        override fun onLikeClick(item: Post, position: Int) {
            presenter.likePost(item.id, position)
        }

        override fun onVideoClick(item: Post, position: Int) {
            requireActivity().launchActivity<VideoPlayerScreen> {
                putExtra(VideoPlayerScreen.URI_KEY, Env.S3_BASE_URL + "${item.media}")
            }
        }
    }

    override fun onLikePostResponse(liked: Boolean, position: Int) {
        postAdapter.updateLike(position, liked)
    }


    private val updateListener = object : UpdateListener {
        override fun update() {
            presenter.fetchReviews(businessId)
        }
    }

    interface UpdateListener {
        fun update()
    }
}
