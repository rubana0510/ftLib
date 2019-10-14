package com.feedbacktower.ui.posts


import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.feedbacktower.R
import com.feedbacktower.adapters.MyPostListAdapter
import com.feedbacktower.callbacks.ScrollListener
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.data.models.Post
import com.feedbacktower.databinding.FragmentTimelineBinding
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.GetPostsResponse
import com.feedbacktower.network.models.LikeUnlikeResponse
import com.feedbacktower.ui.PostTextScreen
import com.feedbacktower.ui.base.BaseViewFragmentImpl
import com.feedbacktower.util.Constants.PAGE_SIZE
import com.feedbacktower.util.launchActivity
import kotlinx.android.synthetic.main.dialog_edit_caption.view.*
import org.jetbrains.anko.toast


class MyPostsFragment : BaseViewFragmentImpl(), PostsContract.View {
    private lateinit var presenter: PostsPresenter
    private lateinit var feedListView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var message: TextView
    private lateinit var postAdapter: MyPostListAdapter
    private var isLoading: Boolean? = false
    private var noPosts: Boolean? = false
    private val list: ArrayList<Post> = ArrayList()
    private var listOver = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentTimelineBinding.inflate(inflater, container, false)
        presenter = PostsPresenter()
        presenter.attachView(this)
        initUi(binding)
        return binding.root
    }

    private fun initUi(binding: FragmentTimelineBinding) {
        feedListView = binding.feedListView
        swipeRefresh = binding.swipeRefresh
        message = binding.message

        //setup list
        val layoutManager = LinearLayoutManager(context)
        feedListView.layoutManager = layoutManager
        feedListView.itemAnimator = DefaultItemAnimator()
        feedListView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL))
        postAdapter = MyPostListAdapter(list, requireActivity(), listener)
        feedListView.adapter = postAdapter
        feedListView.addOnScrollListener(ScrollListener {
            if (listOver) return@ScrollListener

            val lastItemPosition = layoutManager.findLastVisibleItemPosition()
            if (list.size == lastItemPosition + 1) {
                if (lastItemPosition < list.size) {
                    val item = list[list.size - 1]
                    fetchPosts(item.createdAt)
                }
            }
        })
        isLoading = binding.isLoading
        noPosts = binding.noPosts
        fetchPosts()
    }

    private fun fetchPosts(timestamp: String? = null) {
        val businessId = AppPrefs.getInstance(requireContext()).user?.business?.id!!
        presenter.fetchBusinessPosts(businessId, timestamp)
    }

    override fun onBusinessPostsFetched(response: GetPostsResponse?, timestamp: String?) {
        response?.posts?.let {
            listOver = it.size < PAGE_SIZE
            list.addAll(it)
            postAdapter.notifyDataSetChanged()
        }
    }

    override fun showProgress() {
        super.showProgress()

    }

    override fun dismissProgress() {
        super.dismissProgress()

    }

    override fun showNetworkError(error: ApiResponse.ErrorModel) {
        super.showNetworkError(error)
        requireContext().toast(error.message)
    }


    private val listener = object : MyPostListAdapter.Listener {
        override fun onMoreClick(item: Post, position: Int) {
            AlertDialog.Builder(requireContext())
                .setTitle("Manage Post")
                .setItems(arrayOf("EDIT", "DELETE")) { _, index ->
                    when (index) {
                        0 -> {
                            if (item.type == "TEXT") {
                                requireActivity().launchActivity<PostTextScreen> {
                                    putExtra("TEXT", item.text)
                                    putExtra("POST_ID", item.id)
                                }
                            } else if (item.type == "PHOTO" || item.type == "VIDEO") {
                                editMediaCaption(item)
                            }
                        }
                        1 -> {
                            AlertDialog.Builder(requireContext())
                                .setTitle("Delete post?")
                                .setMessage("Are you sure you want to delete post?")
                                .setPositiveButton("DELETE", { _, _ -> deletePost(item.id) })
                                .setNegativeButton("CANCEL", null)
                                .show()
                        }
                    }
                }.show()
        }

        override fun onLikeClick(item: Post, position: Int) {
            likeUnlikePost(item, position)
        }

        override fun onVideoClick(item: Post, position: Int) {
            likeUnlikePost(item, position)
        }
    }

    private fun editMediaCaption(post: Post) {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_caption, null)
        val alert = AlertDialog.Builder(requireContext())
            .setTitle("Edit Post Caption")
            .setView(view)
            .show()

        val caption: EditText = view.editText
        val edit: Button = view.actionButton

        caption.setText(post.text)
        edit.setOnClickListener {
            val captionText = caption.text.toString().trim()
            if (!captionText.isEmpty()) {
                editPost(post.id, captionText)
                alert.dismiss()
            }
        }
    }

    private fun deletePost(postId: String) {
        presenter.deletePost(postId)
    }

    override fun onPostDeleted(postId: String) {
        fetchPosts()
        requireContext().toast("Post deleted")
    }

    private fun editPost(postId: String, text: String) {
        presenter.onEditPostCaption(postId, text)
    }

    override fun onPostEdited(postId: String, caption: String) {
        requireContext().toast("Post updated")
        fetchPosts()
    }

    private fun likeUnlikePost(item: Post, position: Int) {
        presenter.onLikeDislike(item.id, position)
    }

    override fun onLikedDisliked(response: LikeUnlikeResponse?, position: Int) {
        if (response?.liked != null)
            postAdapter.updateLike(position, response.liked)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroyView()
    }

}
