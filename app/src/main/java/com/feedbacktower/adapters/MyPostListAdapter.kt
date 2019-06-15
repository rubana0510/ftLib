package com.feedbacktower.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ListAdapter
import com.ablanco.zoomy.Zoomy
import com.feedbacktower.adapters.diffcallbacks.DiffCallback
import com.feedbacktower.data.models.Post
import com.feedbacktower.databinding.ItemMyPostMediaBinding
import com.feedbacktower.databinding.ItemMyPostTextBinding
import com.feedbacktower.databinding.ItemPostMediaBinding
import com.feedbacktower.fragments.HomeFragmentDirections
import java.lang.IllegalStateException

/**
 * Created by sanket on 12-02-2019.
 */
class MyPostListAdapter(private val activity: Activity, private val listener: Listener?) :
    ListAdapter<Post, BaseViewHolder<*>>(DiffCallback<Post>()) {
    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val item = getItem(position)
        when (holder) {
            is TextPostViewHolder -> holder.bind(
                createLikeClickListener(item, position),
                null,
                createProfileClickListener(item, position),
                createMoreClickListener(item, position),
                item
            )
            is MediaPostViewHolder -> holder.bind(
                createLikeClickListener(item, position),
                createVideoClickListener(item, position),
                createProfileClickListener(item, position),
                createMoreClickListener(item, position),
                item
            )
            else -> throw IllegalArgumentException()
        }
    }

    companion object {
        const val POST_TEXT = "TEXT" //0
        const val POST_PHOTO = "PHOTO" //1
        const val POST_VIDEO = "VIDEO" //2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {

        when (viewType) {
            0 -> {
                return TextPostViewHolder(
                    ItemMyPostTextBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            1 -> {
                    return MediaPostViewHolder(
                        activity,
                        ItemMyPostMediaBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                    )

            }
            else -> throw  IllegalStateException()
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).type) {
            POST_TEXT -> 0
            POST_PHOTO -> 1
            POST_VIDEO -> 1
            else -> throw IllegalArgumentException("Invalid type of data $position")
        }
    }

    private fun createLikeClickListener(item: Post, pos: Int): View.OnClickListener = View.OnClickListener {
        listener?.onLikeClick(item, pos)
    }

    private fun createProfileClickListener(item: Post, pos: Int): View.OnClickListener = View.OnClickListener { view ->
        HomeFragmentDirections.actionNavigationHomeToBusinessDetailsActivity(item.businessId).let {
            view.findNavController().navigate(it)
        }
    }

    private fun createVideoClickListener(item: Post, pos: Int): View.OnClickListener = View.OnClickListener { view ->
        listener?.onVideoClick(item, pos)
    }

    private fun createMoreClickListener(item: Post, pos: Int): View.OnClickListener = View.OnClickListener { view ->
        listener?.onMoreClick(item, pos)
    }


    fun getItemAtPos(position: Int): Post = getItem(position)

    fun updateLike(position: Int, liked: Boolean) {
        getItem(position).liked = if (liked) 1 else 0
        getItemAtPos(position).totalLikes =
            if (liked)
                getItemAtPos(position).totalLikes + 1
            else
                getItemAtPos(position).totalLikes - 1

        notifyItemChanged(position)
    }

    class MediaPostViewHolder(
        private val ctx: Activity,
        val binding: ItemMyPostMediaBinding
    ) : BaseViewHolder<Post>(binding.root) {
        override fun bind(
            listener: View.OnClickListener,
            videoClickListener: View.OnClickListener?,
            profileListener: View.OnClickListener,
            moreListener: View.OnClickListener?,
            item: Post
        ) {
            binding.apply {
                val builder = Zoomy.Builder(ctx).target(postImageView)
                if (item.isPhoto)
                    builder.register()
                else
                    Zoomy.unregister(postImageView)
                post = item
                videoClick = videoClickListener
                likeClickListener = listener
                openProfileListener = profileListener
                moreClickListener = moreListener
                executePendingBindings()
            }
        }
    }

    class TextPostViewHolder(
        val binding: ItemMyPostTextBinding
    ) : BaseViewHolder<Post>(binding.root) {
        override fun bind(
            listener: View.OnClickListener,
            videoClickListener: View.OnClickListener?,
            profileListener: View.OnClickListener,
            moreListener: View.OnClickListener?,
            item: Post
        ) {
            binding.apply {
                post = item
                likeClickListener = listener
                openProfileListener = profileListener
                moreClickListener = moreListener
                executePendingBindings()
            }
        }
    }

    interface Listener {
        fun onLikeClick(item: Post, position: Int)
        fun onVideoClick(item: Post, position: Int)
        fun onMoreClick(item: Post, position: Int)
    }
}