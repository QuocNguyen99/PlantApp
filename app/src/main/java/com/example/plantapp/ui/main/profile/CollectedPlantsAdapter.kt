package com.example.plantapp.ui.main.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.plantapp.data.response.Plant
import com.example.plantapp.databinding.ItemCollectedPlantBinding

class CollectedPlantsAdapter:
    ListAdapter<Plant, CollectedPlantsAdapter.CollectedPlantsViewHolder>(PlantDataDiff()) {

    inner class CollectedPlantsViewHolder(private val binding: ItemCollectedPlantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(plant: Plant) {
            Glide.with(binding.root.context).load(plant.default_image.thumbnail).into(binding.imagePlant)
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
class PlantDataDiff : DiffUtil.ItemCallback<Plant>() {
    override fun areItemsTheSame(oldItem: Plant, newItem: Plant): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Plant, newItem: Plant): Boolean {
        return oldItem == newItem
    }
}