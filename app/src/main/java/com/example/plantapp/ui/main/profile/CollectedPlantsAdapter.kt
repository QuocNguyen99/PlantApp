package com.example.plantapp.ui.main.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.plantapp.data.model.Article
import com.example.plantapp.data.model.DetailSpecie
import com.example.plantapp.data.response.Plant
import com.example.plantapp.databinding.ItemCollectedPlantBinding

class CollectedPlantsAdapter :
    ListAdapter<DetailSpecie, CollectedPlantsAdapter.CollectedPlantsViewHolder>(PlantDataDiff()) {

    var onClick: ((DetailSpecie) -> Unit)? = null

    inner class CollectedPlantsViewHolder(private val binding: ItemCollectedPlantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(detail: DetailSpecie) {
            Glide.with(binding.root.context).load(detail.default_image?.thumbnail).into(binding.imageArticle)
            binding.title.text = detail.scientific_name[0]
            binding.root.setOnClickListener {
                onClick?.invoke(detail)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectedPlantsViewHolder {
        return CollectedPlantsViewHolder(
            ItemCollectedPlantBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CollectedPlantsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class PlantDataDiff : DiffUtil.ItemCallback<DetailSpecie>() {
    override fun areItemsTheSame(oldItem: DetailSpecie, newItem: DetailSpecie): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DetailSpecie, newItem: DetailSpecie): Boolean {
        return oldItem == newItem
    }
}