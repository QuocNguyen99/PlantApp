package com.example.plantapp.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
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

    private val REQUEST_CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS =
        mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()

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
                if (allPermissionsGranted()) {
                    binding.viewFinder.isVisible = true
                    it.isVisible = false
                    startCamera()
                } else {
                    ActivityCompat.requestPermissions(
                        requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
                    )
                }
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
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
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                    }
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview
                )
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