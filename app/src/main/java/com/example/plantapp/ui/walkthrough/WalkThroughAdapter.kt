package com.example.plantapp.ui.walkthrough

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.plantapp.data.WalkThrough
import com.example.plantapp.databinding.ItemWalkthroughBinding

class WalkThroughAdapter(private val context: Context) : ListAdapter<WalkThrough, WalkThroughAdapter.ViewHolder>(OnBoardingDiffCallback()) {

    inner class ViewHolder(private val vb: ItemWalkthroughBinding) :
        RecyclerView.ViewHolder(vb.root) {
        fun bind(onBoarding: WalkThrough) {
            Glide.with(context).load(onBoarding.image).into(vb.imgOb)
            vb.titleOB.text = onBoarding.title
            vb.subTitleOB.text = onBoarding.subString
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemWalkthroughBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class OnBoardingDiffCallback : DiffUtil.ItemCallback<WalkThrough>() {

        override fun areItemsTheSame(oldItem: WalkThrough, newItem: WalkThrough): Boolean {
            return true
        }

        override fun areContentsTheSame(oldItem: WalkThrough, newItem: WalkThrough): Boolean {
            return true
        }
    }

}