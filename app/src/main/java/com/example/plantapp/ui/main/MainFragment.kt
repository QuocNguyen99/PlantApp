package com.example.plantapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.plantapp.R
import com.example.plantapp.databinding.FragmentMainBinding
import com.example.plantapp.ui.main.home.HomeFragment
import com.example.plantapp.ui.main.profile.ProfileFragment


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initEvent()
    }

    private fun initEvent() {
        binding.apply {
            bottomNavigationView.menu.getItem(1).isEnabled = false
            bottomNavigationView.setOnItemSelectedListener { item ->
                // Xử lý sự kiện chuyển tab của BottomNavigationView
                val params = binding.vp.layoutParams as CoordinatorLayout.LayoutParams
                // Thiết lập khoảng cách dưới của ViewPager bằng chiều cao của BottomNavigationView
                params.setMargins(0, 0, 0, bottomNavigationView.height)
                vp.layoutParams = params
                when (item.itemId) {
                    R.id.home -> {
                        vp.currentItem = 0
                        true
                    }

                    R.id.profile -> {
                        vp.currentItem = 1
                        true
                    }

                    else -> false
                }
            }

            // Lắng nghe sự kiện trượt trang của ViewPager2
            vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    // Cập nhật trạng thái chọn mục trong BottomNavigationView
                    bottomNavigationView.menu.getItem(position).isChecked = true
                }
            })
        }
    }

    private fun initView() {
        binding.apply {
            bottomNavigationView.background = null

            val params = binding.vp.layoutParams as CoordinatorLayout.LayoutParams
            // Thiết lập khoảng cách dưới của ViewPager bằng chiều cao của BottomNavigationView
            params.setMargins(0, 0, 0, bottomNavigationView.height)
            vp.layoutParams = params


            val adapter = BottomNavigationViewPager(requireActivity().supportFragmentManager, lifecycle)
            adapter.addFragment(HomeFragment())
            adapter.addFragment(ProfileFragment())
            vp.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}