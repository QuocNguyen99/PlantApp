package com.example.plantapp.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.plantapp.R
import com.example.plantapp.databinding.FragmentMainBinding
import com.example.plantapp.ui.main.home.HomeFragment
import com.example.plantapp.ui.main.profile.ProfileFragment
import com.google.common.util.concurrent.ListenableFuture

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

            vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    bottomNavigationView.menu.getItem(position).isChecked = true
                }
            })

            fab.setOnClickListener {
                binding.close.isVisible = true
                binding.fab.isVisible = false
                startCamera()
            }

            close.setOnClickListener { stopCamera() }
        }
    }

    private fun initView() {
        binding.apply {
            bottomNavigationView.background = null

            val params = binding.vp.layoutParams as CoordinatorLayout.LayoutParams
            params.setMargins(0, 0, 0, bottomNavigationView.height)
            vp.layoutParams = params

            val adapter = BottomNavigationViewPager(requireActivity().supportFragmentManager, lifecycle)
            adapter.addFragment(HomeFragment())
            adapter.addFragment(ProfileFragment())
            vp.adapter = adapter

            vp.isUserInputEnabled = false
        }
    }

    private var cameraProvider: ProcessCameraProvider? = null
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview
                )

            } catch (exc: Exception) {
                Log.e("TAG", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun stopCamera() {
        cameraProvider?.unbindAll()
        binding.close.isVisible = false
        binding.fab.isVisible = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}