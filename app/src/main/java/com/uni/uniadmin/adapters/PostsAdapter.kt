package com.uni.uniteaching.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uni.uniadmin.R
import com.uni.uniadmin.classes.PostData

import com.uni.uniadmin.classes.Posts

class PostsAdapter(
    val context: Context,
    var postList: MutableList<PostData>,
    val onItemClicked: (Int, PostData) -> Unit,
    val onComment: (Int, PostData) -> Unit,
    val deletePost: (PostData) -> Unit


) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    companion object {
        const val WITH_IMAGE = 1
        const val WITHOUT_IMAGE = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (postList[position].type == WITH_IMAGE) WITH_IMAGE else WITHOUT_IMAGE
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == WITH_IMAGE) {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.item_post_with_image, parent, false)
            ViewHolder1(view)
        } else {
            val view: View = LayoutInflater.from(context)
                .inflate(R.layout.item_post_without_image, parent, false)
            ViewHolder2(view)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = postList[position]

        if (currentItem.type == WITHOUT_IMAGE) {
            (holder as ViewHolder2)
            holder.auth.text = currentItem.authorName
            holder.audience.text = currentItem.audience
            holder.text.text = currentItem.description


            if (!currentItem.myPost) {
                holder.deletePost_bt.visibility = View.INVISIBLE
            }

        } else {
            (holder as ViewHolder1)
            /* Glide.with(context)
                 .load(currentItem.imageUrl)
                 .into(holder.image)
 */
            holder.auth.text = currentItem.authorName
            holder.audience.text = currentItem.audience
            holder.text.text = currentItem.description
            if (!currentItem.myPost) {
                holder.deletePost_bt.visibility = View.INVISIBLE
            }

        }

    }


    override fun getItemCount(): Int {
        return postList.size
    }

    fun update(list: MutableList<PostData>) {
        this.postList = list
        notifyDataSetChanged()
    }

    inner class ViewHolder1(item: View) : RecyclerView.ViewHolder(item) {
        val image = item.findViewById<ImageView>(R.id.post_image)

        val auth = item.findViewById<TextView>(R.id.auth_with)
        val audience = item.findViewById<TextView>(R.id.audience_with)
        val text = item.findViewById<TextView>(R.id.text_with)
        val addComment = item.findViewById<ImageView>(R.id.comments_with_Img)
        val recyItem = item.findViewById<ConstraintLayout>(R.id.post_item_with)
        val deletePost_bt = item.findViewById<ImageView>(R.id.delete_with_Img)

        init {
            deletePost_bt.setOnClickListener {
                deletePost.invoke(postList[adapterPosition])
            }
            recyItem.setOnClickListener {
                onItemClicked.invoke(adapterPosition, postList[adapterPosition])
            }
            addComment.setOnClickListener {
                onComment.invoke(adapterPosition, postList[adapterPosition])
            }

        }


    }

    private inner class ViewHolder2(item: View) :
        RecyclerView.ViewHolder(item) {
        val auth = item.findViewById<TextView>(R.id.auth_without)
        val audience = item.findViewById<TextView>(R.id.audience_without)
        val text = item.findViewById<TextView>(R.id.text_without)
        val addComment = item.findViewById<ImageButton>(R.id.comments_without_Img)
        val recyItem = item.findViewById<ConstraintLayout>(R.id.post_item_without)
        val deletePost_bt = item.findViewById<ImageButton>(R.id.delete_without_Img)

        init {
            deletePost_bt.setOnClickListener {
                deletePost.invoke(postList[adapterPosition])
            }
            recyItem.setOnClickListener {
                onItemClicked.invoke(adapterPosition, postList[adapterPosition])
            }
            addComment.setOnClickListener {
                onComment.invoke(adapterPosition, postList[adapterPosition])
            }

        }
    }

}


