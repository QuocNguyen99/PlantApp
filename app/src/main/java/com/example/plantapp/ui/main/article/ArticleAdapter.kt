package com.example.plantapp.ui.main.article

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.plantapp.R
import com.example.plantapp.data.model.Article
import com.example.plantapp.data.response.Plant
import com.example.plantapp.databinding.ItemArticleBinding
import com.example.plantapp.databinding.ItemCollectedPlantBinding
import com.example.plantapp.utils.format
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ArticleAdapter:
    ListAdapter<Article, ArticleAdapter.ArticleViewHolder>(ArticleDataDiff()) {

    var onClick: ((Article) -> Unit)? = null
    var onLiked: ((String, String, Boolean) -> Unit)? = null

    inner class ArticleViewHolder(private val binding: ItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            binding.apply {
                Glide.with(binding.root.context).load(article.image).into(imageArticle)
                title.text = article.title
                author.text = article.author
                time.text = article.time.format()
                val email = Firebase.auth.currentUser?.email ?: ""
                if (article.liked.contains(email)) {
                    imvLiked.setImageResource(R.drawable.ic_heart_fill)
                } else {
                    imvLiked.setImageResource(R.drawable.ic_heart_border_24)
                }

                root.setOnClickListener {
                    onClick?.invoke(article)
                }

                imvLiked.setOnClickListener {
                    val email = Firebase.auth.currentUser?.email ?: ""
                    var isLiked = false
                    if (article.liked.contains(email)) {
                        isLiked = false
                        imvLiked.setImageResource(R.drawable.ic_heart_border_24)
                    } else {
                        isLiked = true
                        imvLiked.setImageResource(R.drawable.ic_heart_fill)
                    }
                    onLiked?.invoke(article.id, email, isLiked)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            ItemArticleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}
class ArticleDataDiff : DiffUtil.ItemCallback<Article>() {
    override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem == newItem
    }
}