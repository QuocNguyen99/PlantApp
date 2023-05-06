package com.example.plantapp.ui.walkthrough

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.plantapp.R
import com.example.plantapp.data.WalkThrough
import com.example.plantapp.databinding.FragmentWalkThroughBinding
import com.google.android.material.tabs.TabLayoutMediator

class WalkThroughFragment : Fragment() {

    private var _binding: FragmentWalkThroughBinding? = null
    private val binding get() = _binding!!

    private val data: MutableList<WalkThrough> = mutableListOf()
    private lateinit var pagerAdapter: WalkThroughAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalkThroughBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pref = requireContext().getSharedPreferences("PREF", MODE_PRIVATE)
        val isFirst = pref.getBoolean("isFirst", false)
        if (isFirst) {
            findNavController().navigate(R.id.action_walkThroughFragment_to_loginFragment)
            return
        } else {
            pref.edit().putBoolean("isFirst", true).apply()
        }
        initView()
        initData()
    }

    private fun initData() {
        addDataWalkThrough()
    }

    private fun initView() {
        pagerAdapter = WalkThroughAdapter(requireContext())
        binding.vp.adapter = pagerAdapter

        binding.vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == data.size - 1) {
                    binding.btnSubmit.text = "Sign In"
                } else {
                    binding.btnSubmit.text = "Next"
                }
            }
        })

        binding.btnSubmit.setOnClickListener {
            when (binding.vp.currentItem) {
                0 -> binding.vp.currentItem += 1
                1 -> binding.vp.currentItem += 1
                2 -> findNavController().navigate(R.id.action_walkThroughFragment_to_loginFragment)
            }
        }
    }

    private fun addDataWalkThrough() {
        data.add(
            WalkThrough(
                "Identify Plants",
                "You can identify the plants you don't know through your camera",
                R.mipmap.walkthrough_first
            )
        )
        data.add(
            WalkThrough(
                "Learn Many Plants Species",
                "Let's learn about the many plant species that exist in this world",
                R.mipmap.walkthrough_second
            )
        )
        data.add(
            WalkThrough(
                "Read Many Articles About Plant",
                "SLet's learn more about beautiful plants and read many articles about plants and gardening",
                R.mipmap.walkthrough_third
            )
        )
        pagerAdapter.submitList(data)
        binding.indicator.setViewPager(binding.vp)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}