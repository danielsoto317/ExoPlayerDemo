package com.dsv.basemvvm.framework.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dsv.basemvvm.R
import com.dsv.basemvvm.databinding.ItemCommentViewBinding
import com.dsv.basemvvm.domain.models.ReplayComment

class CommentsAdapter (val clickListener: ((replayComment: ReplayComment) -> Unit)) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    var commentList: List<ReplayComment> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemCommentViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment: ReplayComment = commentList[position]
        holder.bind(comment)
        holder.itemView.setOnClickListener { clickListener.invoke(comment) }
    }

    override fun getItemCount(): Int = commentList.size

    class ViewHolder(val binding: ItemCommentViewBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: ReplayComment) {
            binding.avatarImageView.setImageDrawable(ContextCompat.getDrawable(context, if (comment.fromCoach) R.drawable.coach_comment_circle else R.drawable.padawan_comment_circle))
            binding.commentBodyTextView.text = comment.comment
        }
    }
}