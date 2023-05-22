package com.example.plantapp.ui.main.specie

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.plantapp.data.model.DetailSpecie
import com.example.plantapp.databinding.ItemSpecieListBinding

class SpeciesListAdapter : ListAdapter<DetailSpecie, SpeciesListAdapter.SpeciesViewHolder>(SpeciesDiffCallback()) {

    var onItemClick: ((detail: DetailSpecie) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpeciesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSpecieListBinding.inflate(inflater, parent, false)
        return SpeciesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SpeciesViewHolder, position: Int) {
        val species = getItem(position)
        holder.bind(species)
    }

    inner class SpeciesViewHolder(private val binding: ItemSpecieListBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(detailSpecie: DetailSpecie) {
            binding.apply {
                Glide.with(binding.root.context).load(detailSpecie.default_image.thumbnail).into(img)
                cycle.text = detailSpecie.cycle
                watering.text = detailSpecie.watering
                title.text = detailSpecie.scientific_name[0]
                description.text = "The word \"cactus\" derives, through Latin, from " +
                        "the Ancient Greek κάκτος, kaktos, a name orig" +
                        "inally used by Theophrastus for a spiny plant " +
                        "whose identity is not certain. Cacti occur in a " +
                        "wide range of shapes and sizes. Most cacti live " +
                        "in habitats subject to at least some drought. "

                root.setOnClickListener { onItemClick?.invoke(detailSpecie) }
            }
        }
    }

    class SpeciesDiffCallback : DiffUtil.ItemCallback<DetailSpecie>() {
        override fun areItemsTheSame(oldItem: DetailSpecie, newItem: DetailSpecie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DetailSpecie, newItem: DetailSpecie): Boolean {
            return oldItem == newItem
        }
    }
}

