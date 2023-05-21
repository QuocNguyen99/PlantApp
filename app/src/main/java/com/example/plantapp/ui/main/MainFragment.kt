package com.example.plantapp.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.plantapp.R
import com.example.plantapp.databinding.FragmentMainBinding
import com.example.plantapp.ui.main.home.HomeFragment
import com.example.plantapp.ui.main.profile.ProfileFragment
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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

            fab.setOnClickListener {
//                startCamera()
                val db = Firebase.firestore
            }
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

            vp.isUserInputEnabled = false
        }
    }

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraPreview: Preview
    private lateinit var cameraSelector: CameraSelector

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            cameraPreview = Preview.Builder().build()
            cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            try {
                cameraProvider.unbindAll()

                val camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, cameraPreview
                )

//                cameraPreview.setSurfaceProvider(viewFinder.createSurfaceProvider(camera.cameraInfo))
            } catch (exception: Exception) {
                Log.e("TAG", "Failed to start camera", exception)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}