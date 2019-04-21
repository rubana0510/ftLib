package com.feedbacktower.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.feedbacktower.adapters.diffcallbacks.PostDiffCallback
import com.feedbacktower.data.models.Post
import com.feedbacktower.databinding.ItemPostTextBinding
import com.feedbacktower.databinding.ItemPostMediaBinding
import java.lang.IllegalStateException

/**
 * Created by sanket on 12-02-2019.
 */
class PostListAdapter : ListAdapter<Post, BaseViewHolder<*>>(PostDiffCallback()) {
    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val item = getItem(position)
        when (holder) {
            is TextPostViewHolder -> holder.bind(createClickListener(item), item)
            is MediaPostViewHolder -> holder.bind(createClickListener(item), item)
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
                    ItemPostTextBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            1 -> {
                return MediaPostViewHolder(
                    ItemPostMediaBinding.inflate(
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
        return when (getItem(position).postType) {
            POST_TEXT -> 0
            POST_PHOTO -> 1
            POST_VIDEO -> 1
            else -> throw IllegalArgumentException("Invalid type of data $position")
        }
    }

    private fun createClickListener(item: Post): View.OnClickListener = View.OnClickListener {

    }

    fun getItemAtPos(position: Int): Post = getItem(position)

    class MediaPostViewHolder(
        val binding: ItemPostMediaBinding
    ) : BaseViewHolder<Post>(binding.root) {
        override fun bind(listener: View.OnClickListener, item: Post) {
            binding.apply {
                post = item
                likeClickListener = listener
                executePendingBindings()
            }
        }
    }

    class TextPostViewHolder(
        val binding: ItemPostTextBinding
    ) : BaseViewHolder<Post>(binding.root) {
        override fun bind(listener: View.OnClickListener, item: Post) {
            binding.apply {
                post = item
                likeClickListener = listener
                executePendingBindings()
            }
        }
    }
}