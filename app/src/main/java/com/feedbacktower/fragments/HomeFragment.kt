package com.feedbacktower.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.feedbacktower.R
import com.feedbacktower.adapters.PostListAdapter
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.data.models.Post
import com.feedbacktower.databinding.FragmentHomeBinding
import com.feedbacktower.network.manager.PostManager
import com.feedbacktower.ui.PostTextScreen
import com.feedbacktower.util.launchActivity
import com.feedbacktower.util.setVertical


class HomeFragment : Fragment() {
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
        val binding = FragmentHomeBinding.inflate(inflater, container, false)
        initUi(binding)
        return binding.root
    }

    private fun initUi(binding: FragmentHomeBinding) {
        binding.toolbar.title = getString(R.string.app_name)
        binding.addPostListener = View.OnClickListener { requireActivity().launchActivity<PostTextScreen>() }
        feedListView = binding.feedListView
        swipeRefresh = binding.swipeRefresh
        message = binding.message
        binding.isCustomer = AppPrefs.getInstance(requireContext()).user?.userType == "CUSTOMER"

        swipeRefresh.setOnRefreshListener {
            fetchPostList()
        }

        //setup list
        feedListView.setVertical(requireContext())
        postAdapter = PostListAdapter()
        feedListView.adapter = postAdapter
        isLoading = binding.isLoading
        noPosts = binding.noPosts
        fetchPostList()
    }

    private fun fetchPostList() {
        PostManager.getInstance()
            .getPosts("", "NEW") { response, error ->
                isLoading = false
                noPosts = response?.posts?.isEmpty()
                postAdapter.submitList(response?.posts)
            }
    }

    private fun fetchPostList2() {
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
        isLoading = false
        noPosts = posts.isEmpty()
        postAdapter.submitList(posts)
    }


}
