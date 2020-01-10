package com.feedbacktower.ui.posts


import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.feedbacktower.App
import com.feedbacktower.R
import com.feedbacktower.adapters.MyPostListAdapter
import com.feedbacktower.util.callbacks.ScrollListener
import com.feedbacktower.data.models.Post
import com.feedbacktower.databinding.FragmentTimelineBinding
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.GetPostsResponse
import com.feedbacktower.network.models.LikeUnlikeResponse
import com.feedbacktower.ui.home.post.text.TextPostActivity
import com.feedbacktower.ui.base.BaseViewFragmentImpl
import com.feedbacktower.util.Constants.PAGE_SIZE
import com.feedbacktower.util.launchActivity
import kotlinx.android.synthetic.main.dialog_edit_caption.view.*
import org.jetbrains.anko.toast
import javax.inject.Inject


class PostsFragment : BaseViewFragmentImpl(), PostsContract.View {
    private lateinit var binding: FragmentTimelineBinding
    @Inject
    lateinit var presenter: PostsPresenter
    private val args: PostsFragmentArgs by navArgs()
    private lateinit var postAdapter: MyPostListAdapter
    private val list: ArrayList<Post> = ArrayList()
    private var listOver = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().applicationContext as App).appComponent.accountComponent().create().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTimelineBinding.inflate(inflater, container, false)
        presenter.attachView(this)
        initUi()
        return binding.root
    }

    private fun initUi() {
        //setup list
        val layoutManager = LinearLayoutManager(context)
        binding.feedListView.layoutManager = layoutManager
        binding.feedListView.itemAnimator = DefaultItemAnimator()
        binding.feedListView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.HORIZONTAL
            )
        )
        postAdapter = MyPostListAdapter(list, requireActivity(), listener)
        binding.feedListView.adapter = postAdapter
        binding.feedListView.addOnScrollListener(ScrollListener {
            if (listOver) return@ScrollListener
            val lastItemPosition = layoutManager.findLastVisibleItemPosition()
            if (list.size == lastItemPosition + 1) {
                if (lastItemPosition < list.size) {
                    val item = list[list.size - 1]
                    fetchPosts(item.createdAt)
                }
            }
        })
        binding.swipeRefresh.setOnRefreshListener { fetchPosts() }
        fetchPosts()
    }

    private fun fetchPosts(timestamp: String? = null) {
        presenter.fetchBusinessPosts(args.businessId, timestamp)
    }

    override fun onBusinessPostsFetched(response: GetPostsResponse?, timestamp: String?) {
        response?.posts?.let {
            if (timestamp == null) {
                list.clear()
                binding.noPosts = it.isEmpty()
            }
            listOver = it.size < PAGE_SIZE
            list.addAll(it)
            postAdapter.notifyDataSetChanged()
        }
    }

    override fun showProgress() {
        super.showProgress()
        binding.isLoading = true
    }

    override fun dismissProgress() {
        super.dismissProgress()
        binding.isLoading = false
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
                                requireActivity().launchActivity<TextPostActivity> {
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
