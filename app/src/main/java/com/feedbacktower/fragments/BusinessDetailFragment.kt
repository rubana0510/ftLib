package com.feedbacktower.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.adapters.PostListAdapter
import com.feedbacktower.data.models.Post
import com.feedbacktower.databinding.FragmentBusinessDetailBinding
import com.feedbacktower.network.models.BusinessDetails
import com.feedbacktower.util.enableSeparator
import com.feedbacktower.util.setItemAnimator
import com.feedbacktower.util.setLinearLayoutManager
import com.feedbacktower.util.setVertical


class BusinessDetailFragment : Fragment() {
    private lateinit var postListView: RecyclerView
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

        postListView = binding.postListView

        //setup list
        postListView.setVertical(requireContext())
        postAdapter = PostListAdapter()
        postListView.adapter = postAdapter
        fetchTimeline()
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
