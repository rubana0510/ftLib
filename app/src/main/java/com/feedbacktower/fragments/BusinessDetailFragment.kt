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
import com.feedbacktower.databinding.FragmentBusinessDetailBinding
import com.feedbacktower.network.manager.PostManager
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.network.manager.ReviewsManager
import com.feedbacktower.ui.map.MapScreen
import com.feedbacktower.ui.videoplayer.VideoPlayerScreen
import com.feedbacktower.util.Constants
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentBusinessDetailBinding.inflate(inflater, container, false)
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

            val d = SendSuggestionDialog()
            d.arguments = Bundle().apply { putString("businessId", businessId) }
            d.show(fragmentManager, "suggestion")
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

            val d = RateReviewDialog()
            d.arguments = Bundle().apply { putString("businessId", businessId) }
            d.show(fragmentManager, "review")
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
        reviewAdapter = ReviewListAdapter()
        reviewListView.adapter = reviewAdapter

        postListView.setVertical(requireContext())
        postAdapter = ProfilePostListAdapter(listener)
        postListView.adapter = postAdapter

        fetchReviews(binding)
        fetchPosts(binding)
        fetchBusinessDetails(binding)
    }

    private fun fetchBusinessDetails(binding: FragmentBusinessDetailBinding) {
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

    private fun fetchReviews(binding: FragmentBusinessDetailBinding) {
        binding.reviewsLoading = true
        ReviewsManager.getInstance().getBusinessReviews(businessId, "") { response, error ->
            if (error == null) {
                reviewAdapter.submitList(response?.review)
                if (response?.review == null || response.review.isEmpty()) return@getBusinessReviews
                binding.reviewsLoading = false
            } else {
                requireContext().toast("Failed to load reviews")
            }
        }
    }

    private fun fetchPosts(binding: FragmentBusinessDetailBinding) {
        binding.timelineLoading = true
        PostManager.getInstance().getBusinessPosts(businessId, "", "OLD") { response, error ->
            if (error == null) {
                postAdapter.submitList(response?.posts)
                if (response?.posts == null || response.posts.isEmpty()) return@getBusinessPosts
                binding.timelineLoading = false
            } else {
                requireContext().toast("Failed to load posts")
            }
        }
    }

    private val listener = object : ProfilePostListAdapter.Listener {
        override fun onLikeClick(item: Post, position: Int) {
            likeUnlikePost(item, position)
        }

        override fun onVideoClick(item: Post, position: Int) {
            requireActivity().launchActivity<VideoPlayerScreen> {
                putExtra(VideoPlayerScreen.URI_KEY, Constants.Service.Secrets.BASE_URL + "/posts/${item.media}")
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

}
