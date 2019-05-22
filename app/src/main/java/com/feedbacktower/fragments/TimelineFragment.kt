package com.feedbacktower.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.feedbacktower.R
import com.feedbacktower.adapters.PostListAdapter
import com.feedbacktower.data.models.Post
import com.feedbacktower.databinding.FragmentTimelineBinding
import com.feedbacktower.network.manager.PostManager


class TimelineFragment : Fragment() {
    private lateinit var feedListView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var message: TextView
    private lateinit var postAdapter: PostListAdapter
    private var isLoading: Boolean? = false
    private var noPosts: Boolean? = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentTimelineBinding.inflate(inflater, container, false)
        initUi(binding)
        return binding.root
    }

    private fun initUi(binding: FragmentTimelineBinding) {
        binding.toolbar.title = getString(R.string.app_name)
        feedListView = binding.feedListView
        swipeRefresh = binding.swipeRefresh
        message = binding.message

        //setup list
        feedListView.layoutManager = LinearLayoutManager(requireContext())
        feedListView.itemAnimator = DefaultItemAnimator()
        feedListView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL))
        postAdapter = PostListAdapter(listener)
        feedListView.adapter = postAdapter
        isLoading = binding.isLoading
        noPosts = binding.noPosts
       // fetchPostList()
    }
    private val listener = object : PostListAdapter.Listener {
        override fun onLikeClick(item: Post, position: Int) {
            likeUnlikePost(item, position)
        }
        override fun onVideoClick(item: Post, position: Int) {
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
