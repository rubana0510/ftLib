package com.feedbacktower.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.adapters.PostListAdapter
import com.feedbacktower.adapters.ReviewListAdapter
import com.feedbacktower.data.models.Post
import com.feedbacktower.databinding.FragmentBusinessDetailBinding
import com.feedbacktower.network.manager.PostManager
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.network.manager.ReviewsManager
import com.feedbacktower.util.isCurrentBusiness
import com.feedbacktower.util.setVertical
import org.jetbrains.anko.toast
import java.lang.IllegalArgumentException


class BusinessDetailFragment : Fragment() {
    private lateinit var reviewListView: RecyclerView
    private lateinit var postListView: RecyclerView
    private lateinit var reviewAdapter: ReviewListAdapter
    private lateinit var postAdapter: PostListAdapter
    private lateinit var businessId: String
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
        binding.onSendSuggestionClicked = View.OnClickListener {
            if(isCurrentBusiness(businessId, requireContext())){
                requireContext().toast("You cannot Send suggestion to yourself!")
                return@OnClickListener
            }

            val d = SendSuggestionDialog()
            d.arguments = Bundle().apply { putString("businessId", businessId) }
            d.show(fragmentManager, "suggestion")
        }


        binding.sendReviewButtonClicked = View.OnClickListener {
            if(isCurrentBusiness(businessId, requireContext())){
                requireContext().toast("You cannot Rate or Review yourself!")
                return@OnClickListener
            }

            val d = RateReviewDialog()
            d.arguments = Bundle().apply { putString("businessId", businessId) }
            d.show(fragmentManager, "review")
        }
        postListView = binding.postListView
        reviewListView = binding.reviewListView

        //setup list
        reviewListView.setVertical(requireContext())
        reviewAdapter = ReviewListAdapter()
        reviewListView.adapter = reviewAdapter

        postListView.setVertical(requireContext())
        postAdapter = PostListAdapter(likeListener)
        postListView.adapter = postAdapter

        fetchReviews()
        fetchPosts()
        fetchBusinessDetails(binding)
    }

    private fun fetchBusinessDetails(binding: FragmentBusinessDetailBinding) {
        ProfileManager.getInstance()
            .getBusinessDetails(businessId) { response, error ->
                if (error == null && response?.business != null) {
                    binding.business = response.business
                }
            }
    }

    private fun fetchReviews() {
        ReviewsManager.getInstance().getBusinessReviews(businessId, "") { response, error ->
            if (error == null) {
                reviewAdapter.submitList(response?.review!!)
            } else {
                requireContext().toast("Failed to load reviews")
            }
        }
    }

    private fun fetchPosts() {
        PostManager.getInstance().getBusinessPosts(businessId, "", "OLD") { response, error ->
            if (error == null) {
                postAdapter.submitList(response?.posts!!)
            } else {
                requireContext().toast("Failed to load posts")
            }
        }
    }

    private val likeListener = object : PostListAdapter.LikeListener {
        override fun onClick(item: Post, position: Int) {
            likeUnlikePost(item, position)
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
