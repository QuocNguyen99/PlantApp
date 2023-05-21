package com.example.plantapp.ui.main.specie

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.plantapp.data.model.Specie
import com.example.plantapp.databinding.ItemSpecieBinding

class SpeciesAdapter : ListAdapter<Specie, SpeciesAdapter.SpeciesViewHolder>(SpeciesDiffCallback()) {

    var onItemClick: ((type: String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpeciesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSpecieBinding.inflate(inflater, parent, false)
        return SpeciesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SpeciesViewHolder, position: Int) {
        val species = getItem(position)
        holder.bind(species)
    }

    inner class SpeciesViewHolder(private val binding: ItemSpecieBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(species: Specie) {
            binding.content.text = species.type
            if (adapterPosition == 0) {
                binding.header.isVisible = true
                binding.header.text = species.alphabet
            } else {
                if (currentList[adapterPosition].alphabet != currentList[adapterPosition - 1].alphabet) {
                    binding.header.isVisible = true
                    binding.header.text = species.alphabet
                } else {
                    binding.header.isVisible = false
                }
            }
            binding.content.setOnClickListener { onItemClick?.invoke(species.type) }
        }
    }

    class SpeciesDiffCallback : DiffUtil.ItemCallback<Specie>() {
        override fun areItemsTheSame(oldItem: Specie, newItem: Specie): Boolean {
            return oldItem.specie.id == oldItem.specie.id
        }

        override fun areContentsTheSame(oldItem: Specie, newItem: Specie): Boolean {
            return oldItem == newItem
        }
    }
}

