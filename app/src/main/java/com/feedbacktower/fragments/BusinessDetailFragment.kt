package com.feedbacktower.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.adapters.PostListAdapter
import com.feedbacktower.adapters.ReviewListAdapter
import com.feedbacktower.data.models.Post
import com.feedbacktower.data.models.Review
import com.feedbacktower.databinding.FragmentBusinessDetailBinding
import com.feedbacktower.network.models.BusinessDetails
import com.feedbacktower.util.setVertical


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
        initUi(binding)
        return binding.root
    }

    private fun initUi(binding: FragmentBusinessDetailBinding) {
        binding.business = BusinessDetails(
            "id1",
            "bid1",
            "Rodreguez Tailors",
            "1234",
            "cid1",
            "Tailor",
            "https://via.placeholder.com/200",
            "http://website.com",
            233,
            "Mapusa, Goa",
            "+91-9876543210",
            "4.5",
            "APPROVED",
            "1234"
        )
        binding.onViewReviewsClicked = View.OnClickListener {
            BusinessDetailFragmentDirections.actionNavigationBusinessDetailToNavigationReview().let {
                findNavController().navigate(it)
            }
        }
        binding.onSendSuggestionClicked = View.OnClickListener {
            SendSuggestionDialog().show(fragmentManager, "suggestion")
        }/*
        binding.ratingBar.onRatingBarChangeListener {_,_,_->
            RateReviewDialog().show(fragmentManager, "review")
        }*/
        postListView = binding.postListView
        reviewListView = binding.reviewListView

        //setup list
        postListView.setVertical(requireContext())
        reviewListView.setVertical(requireContext())
        postAdapter = PostListAdapter()
        postListView.adapter = postAdapter
        reviewAdapter = ReviewListAdapter()
        reviewListView.adapter = reviewAdapter
        fetchTimeline()
        fetchReviews()
    }

    private fun fetchReviews() {
        val list = listOf(
                Review("1", "1", "Sanket Naik", "https://via.placeholder.com/50", "Best Garage1", "It was really great experience using your service. Hope we will get the same for years to come.", "1", "ss"),
                Review("1", "1", "Sanket Naik", "https://via.placeholder.com/50", "Best Garage1", "It was really great experience using your service.", "2", "ss")
            )
        reviewAdapter.submitList(list)
    }

    private fun fetchTimeline() {
        val posts = listOf(
            Post(
                "1",
                "2",
                "Magic Muncheez",
                "https://via.placeholder.com/150",
                "Try This, its super amazing!",
                "https://via.placeholder.com/500",
                PostListAdapter.POST_PHOTO,
                "today",
                5
            ),
            Post(
                "1",
                "2",
                "Magic Muncheez",
                "https://via.placeholder.com/150",
                "Try This, its super amazing!",
                "https://via.placeholder.com/500",
                PostListAdapter.POST_TEXT,
                "yesterday",
                10
            ),
            Post(
                "1",
                "2",
                "Magic Muncheez",
                "https://via.placeholder.com/150",
                "Try This, its super amazing!",
                "https://via.placeholder.com/500",
                PostListAdapter.POST_VIDEO,
                "now",
                102
            )
        )
        postAdapter.submitList(posts)
    }
}
