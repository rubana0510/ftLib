package com.feedbacktower.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.navArgs
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
import com.feedbacktower.util.Constants
import org.jetbrains.anko.toast


class TimelineFragment : Fragment() {
    private lateinit var feedListView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var message: TextView
    private lateinit var postAdapter: PostListAdapter
    private var isLoading: Boolean? = false
    private var noPosts: Boolean? = false
    private lateinit var businessId: String
    private val posts: ArrayList<Post> = ArrayList()
    private var postsOver: Boolean = false
    private var isPostsLoading: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentTimelineBinding.inflate(inflater, container, false)
        val args: TimelineFragmentArgs? by navArgs()
        businessId = args?.businessId ?: throw IllegalArgumentException("Invalid business")

        initUi(binding)
        return binding.root
    }

    private fun initUi(binding: FragmentTimelineBinding) {
        feedListView = binding.feedListView
        swipeRefresh = binding.swipeRefresh
        message = binding.message

        //setup list
        val layoutManager = LinearLayoutManager(requireContext())
        feedListView.layoutManager = layoutManager
        feedListView.itemAnimator = DefaultItemAnimator()
        feedListView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL))
        postAdapter = PostListAdapter(posts, requireActivity(), listener)
        feedListView.adapter = postAdapter
        feedListView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (postsOver) return

                val lastPostPosition = layoutManager.findLastVisibleItemPosition()
                if (posts.size == lastPostPosition + 1) {
                    if (lastPostPosition < posts.size) {
                        val post = posts[posts.size - 1]
                        fetchPosts(post.createdAt)
                    }
                }
            }
        })
        isLoading = binding.isLoading
        noPosts = binding.noPosts
        fetchPosts()
    }

    private fun fetchPosts(timestamp: String = "") {
        if (isPostsLoading) return
        swipeRefresh.isRefreshing = true
        isPostsLoading = true
        PostManager.getInstance()
            .getBusinessPosts(businessId, timestamp) { response, error ->
                swipeRefresh.isRefreshing = false
                isPostsLoading = false
                if (error != null) {
                    requireContext().toast(error.message ?: getString(R.string.default_err_message))
                    return@getBusinessPosts
                }
                response?.posts?.let {
                    postsOver = it.size < Constants.PAGE_SIZE
                    val oldCount = posts.size
                    posts.addAll(it)
                    //postAdapter.notifyItemRangeInserted(oldCount - 1, posts.size)
                    postAdapter.notifyDataSetChanged()
                }
            }
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
