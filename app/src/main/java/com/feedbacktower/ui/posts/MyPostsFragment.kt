package com.feedbacktower.ui.posts


import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
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
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.data.models.Post
import com.feedbacktower.databinding.FragmentTimelineBinding
import com.feedbacktower.network.manager.PostManager
import com.feedbacktower.ui.PostTextScreen
import com.feedbacktower.util.launchActivity
import kotlinx.android.synthetic.main.dialog_edit_caption.view.*
import org.jetbrains.anko.toast


class MyPostsFragment : Fragment() {
    private lateinit var feedListView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var message: TextView
    private lateinit var postAdapter: MyPostListAdapter
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
        feedListView = binding.feedListView
        swipeRefresh = binding.swipeRefresh
        message = binding.message

        //setup list
        feedListView.layoutManager = LinearLayoutManager(requireContext())
        feedListView.itemAnimator = DefaultItemAnimator()
        feedListView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL))
        postAdapter = MyPostListAdapter(requireActivity(), listener)
        feedListView.adapter = postAdapter
        isLoading = binding.isLoading
        noPosts = binding.noPosts
        fetchPosts()
    }

    private fun fetchPosts() {
        val businessId = AppPrefs.getInstance(requireContext()).user?.business?.id!!
        PostManager.getInstance().getBusinessPosts(businessId, "", "OLD") { response, error ->
            if (error == null) {
                postAdapter.submitList(response?.posts!!)
            } else {
                requireContext().toast("Failed to load posts")
            }
        }
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
        PostManager.getInstance()
            .deletePost(postId) { _, _ ->
                fetchPosts()
                requireContext().toast("Post deleted")
            }
    }

    private fun editPost(postId: String, text: String) {
        PostManager.getInstance()
            .editTextPost(postId, text) { response, error ->
                if (error != null) {
                    requireContext().toast(error.message ?: getString(R.string.default_err_message))
                    return@editTextPost
                }
                requireContext().toast("Post updated")
                fetchPosts()
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
