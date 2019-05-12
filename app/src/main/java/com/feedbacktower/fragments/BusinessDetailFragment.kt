package com.feedbacktower.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.adapters.PostListAdapter
import com.feedbacktower.adapters.ReviewListAdapter
import com.feedbacktower.data.models.Post
import com.feedbacktower.data.models.Review
import com.feedbacktower.databinding.FragmentBusinessDetailBinding
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.network.models.BusinessDetails
import com.feedbacktower.util.setVertical
import java.lang.IllegalArgumentException


class BusinessDetailFragment : Fragment() {
    private lateinit var reviewListView: RecyclerView
    private lateinit var postListView: RecyclerView
    private lateinit var reviewAdapter: ReviewListAdapter
    private lateinit var postAdapter: PostListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentBusinessDetailBinding.inflate(inflater, container, false)
        val businessId = arguments?.getString("businessId")?: throw  IllegalArgumentException("Invalid business args")
        initUi(binding, businessId)
        return binding.root
    }

    private fun initUi(binding: FragmentBusinessDetailBinding, businessId: String) {

        binding.onViewReviewsClicked = View.OnClickListener {
            val d = BusinessDetailFragmentDirections.actionNavigationBusinessDetailToNavigationReview(businessId)
            findNavController().navigate(d)
        }
        binding.onSendSuggestionClicked = View.OnClickListener {
            val d = SendSuggestionDialog()
            d.arguments = Bundle().apply { putString("businessId", businessId) }
            d.show(fragmentManager, "suggestion")
        }


        binding.sendReviewButtonClicked = View.OnClickListener {
            val d = RateReviewDialog()
            d.arguments = Bundle().apply { putString("businessId", businessId) }
            d.show(fragmentManager, "review")
        }
        postListView = binding.postListView
        reviewListView = binding.reviewListView

        //setup list
        postListView.setVertical(requireContext())
        reviewListView.setVertical(requireContext())
        postAdapter = PostListAdapter(null)
        postListView.adapter = postAdapter
        reviewAdapter = ReviewListAdapter()
        reviewListView.adapter = reviewAdapter
        //fetchReviews()
        ProfileManager.getInstance()
            .getBusinessDetails(businessId) { response, error ->
                if (error == null && response?.business != null) {
                    binding.business = response.business
                    reviewAdapter.submitList(response.business.reviews)
                }
            }
    }
}
