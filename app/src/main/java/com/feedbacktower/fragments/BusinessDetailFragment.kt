package com.feedbacktower.fragments


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.adapters.ProfilePostListAdapter
import com.feedbacktower.adapters.ReviewListAdapter
import com.feedbacktower.data.models.Business
import com.feedbacktower.data.models.Post
import com.feedbacktower.data.models.Review
import com.feedbacktower.databinding.FragmentBusinessDetailBinding
import com.feedbacktower.network.env.Environment
import com.feedbacktower.network.manager.PostManager
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.network.manager.ReviewsManager
import com.feedbacktower.ui.map.MapScreen
import com.feedbacktower.ui.videoplayer.VideoPlayerScreen
import com.feedbacktower.util.isCurrentBusiness
import com.feedbacktower.util.launchActivity
import com.feedbacktower.util.setVertical
import org.jetbrains.anko.toast


class BusinessDetailFragment : Fragment() {
    private lateinit var reviewListView: RecyclerView
    private lateinit var postListView: RecyclerView
    private lateinit var reviewAdapter: ReviewListAdapter
    private lateinit var postAdapter: ProfilePostListAdapter
    private lateinit var businessId: String
    private var business: Business? = null
    private val list: ArrayList<Review> = ArrayList()
    private val postList: ArrayList<Post> = ArrayList()
    private lateinit var binding: FragmentBusinessDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBusinessDetailBinding.inflate(inflater, container, false)
        businessId = arguments?.getString("businessId") ?: throw  IllegalArgumentException("Invalid args")
        initUi(binding, businessId)
        return binding.root
    }

    private fun initUi(binding: FragmentBusinessDetailBinding, businessId: String) {

        binding.onViewReviewsClicked = View.OnClickListener {
            val d = BusinessDetailFragmentDirections.actionNavigationBusinessDetailToNavigationReview(businessId)
            findNavController().navigate(d)
        }
        binding.onViewPostsClicked = View.OnClickListener {
            val d = BusinessDetailFragmentDirections.actionNavigationBusinessDetailToNavigationTimeline(businessId)
            findNavController().navigate(d)
        }
        binding.onSendSuggestionClicked = View.OnClickListener {
            if (isCurrentBusiness(businessId, requireContext())) {
                requireContext().toast("You cannot Send suggestion to yourself!")
                return@OnClickListener
            }

            val d = SendSuggestionDialog(updateListener)
            d.arguments = Bundle().apply { putString("businessId", businessId) }
            d.show(fragmentManager!!, "suggestion")
        }
        binding.onCurrentLocationClicked = View.OnClickListener {
            if (business == null || business?.currentLocation == null) {
                requireContext().toast("No location found")
                return@OnClickListener
            }
            business?.let {
                requireActivity().launchActivity<MapScreen> {
                    putExtra("LOCATION", it.currentLocation)
                    putExtra("TITLE", "Last Seen At")
                }
            }

        }


        binding.sendReviewButtonClicked = View.OnClickListener {
            if (isCurrentBusiness(businessId, requireContext())) {
                requireContext().toast("You cannot Rate or Review yourself!")
                return@OnClickListener
            }

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
        fetchReviews()
        fetchPosts()
        fetchBusinessDetails()
    }

    private fun fetchBusinessDetails() {
        binding.detailsLoading = true
        ProfileManager.getInstance()
            .getBusinessDetails(businessId) { response, error ->
                if (error == null && response?.business != null) {
                    binding.business = response.business
                    business = response.business
                    binding.detailsLoading = false
                }
            }
    }

    private fun fetchReviews() {
        binding.reviewsLoading = true
        ReviewsManager.getInstance().getBusinessReviews(businessId, "") { response, error ->
            if (error != null) {
                requireContext().toast("Failed to load reviews")
                return@getBusinessReviews
            }
            response?.review?.let {
                if (it.isEmpty()) return@getBusinessReviews
                list.clear()
                list.addAll(it)
                reviewAdapter.notifyDataSetChanged()
                binding.reviewsLoading = false
            }
        }
    }

    private fun fetchPosts(timestamp: String = "") {
        binding.timelineLoading = true
        PostManager.getInstance().getBusinessPosts(businessId, timestamp) { response, error ->
            if (error != null) {
                requireContext().toast("Failed to load posts")
                return@getBusinessPosts
            }

            response?.posts?.let {
                if (it.isEmpty()) return@getBusinessPosts
                list.clear()
                postList.addAll(it)
                postAdapter.notifyDataSetChanged()
                binding.timelineLoading = false
            }
        }
    }

    private val listener = object : ProfilePostListAdapter.Listener {
        override fun onLikeClick(item: Post, position: Int) {
            likeUnlikePost(item, position)
        }

        override fun onVideoClick(item: Post, position: Int) {
            requireActivity().launchActivity<VideoPlayerScreen> {
                putExtra(VideoPlayerScreen.URI_KEY, Environment.S3_BASE_URL + "${item.media}")
            }
        }
    }

    private fun likeUnlikePost(item: Post, position: Int) {
        PostManager.getInstance()
            .likePost(item.id) { response, _ ->
                if (response?.liked != null)
                    postAdapter.updateLike(position, response.liked)
            }
    }

    private val updateListener = object: UpdateListener{
        override fun update() {
            fetchReviews()
        }
    }

    interface UpdateListener {
        fun update()
    }
}
