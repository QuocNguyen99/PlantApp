package com.example.plantapp.ui.add_plants

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.plantapp.R
import com.example.plantapp.databinding.FragmentAddPlantBinding
import com.example.plantapp.network.repository.article.FireStoreRepository
import com.example.plantapp.ui.ViewModelFactory
import com.example.plantapp.utils.createWaitingDialog
import com.example.plantapp.utils.hasPermissions


class AddPlantFragment : Fragment() {

    private var _binding: FragmentAddPlantBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AddPlantViewModel

    private val cycleList = mutableListOf("Herbaceous Perennial", "Perennial", "Annual")
    private val wateringList = mutableListOf("Average", "Minimum", "Frequent")
    private lateinit var cycleAdapter: ArrayAdapter<String>
    private lateinit var wateringAdapter: ArrayAdapter<String>
    private lateinit var typeAdapter: ArrayAdapter<String>

    private var waitingDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddPlantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(fireStoreRepository = FireStoreRepository())
        )[AddPlantViewModel::class.java]
        initData()
        initEvent()
        initObserver()
    }

    private fun initObserver() {
        viewModel.error.observe(viewLifecycleOwner) {
            if (it.isEmpty())
                return@observe
            waitingDialog?.dismiss()
            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
            binding.btnSave.isEnabled = true
        }

        viewModel.saveCompleted.observe(viewLifecycleOwner) {
            waitingDialog?.dismiss()
            if (it) {
                Toast.makeText(requireContext(), "Add plant success!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }

        viewModel.type.observe(viewLifecycleOwner) {
            typeAdapter.clear()
            typeAdapter.addAll(it)
        }
    }

    private fun initData() {
        cycleAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, cycleList)
        wateringAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, wateringList)
        typeAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, mutableListOf())

        binding.spinnerCycle.adapter = cycleAdapter
        binding.spinnerWatering.adapter = wateringAdapter
        binding.spinnerType.adapter = typeAdapter
    }

    private fun initEvent() {
        binding.imvBack.setOnClickListener { findNavController().popBackStack() }
        binding.imageCapture.setOnClickListener { binding.image.callOnClick() }

        binding.image.setOnClickListener {
            // Check permission
            val hasPermissionCamera = requireContext().hasPermissions(Manifest.permission.CAMERA)
            if (hasPermissionCamera) {
                resultLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        binding.btnSave.setOnClickListener {
            it.isEnabled = false
            if (waitingDialog == null) {
                waitingDialog = requireContext().createWaitingDialog()
            }
            waitingDialog?.show()
            viewModel.addPlant(
                name = binding.edtName.text.toString(),
                type = binding.spinnerType.selectedItem.toString(),
                cycle = binding.spinnerCycle.selectedItem.toString(),
                watering = binding.spinnerWatering.selectedItem.toString(),
                description = binding.edtDescription.text.toString(),
            )
        }

        binding.image.callOnClick()
    }


    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                data?.let {
                    val bitmap = it.extras?.get("data") as Bitmap
                    binding.image.setImageBitmap(bitmap)
                    viewModel.bitmap = bitmap
                    binding.imageCapture.visibility = View.GONE
                }
            }
        }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                resultLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
            } else {
                // PERMISSION NOT GRANTED
                Toast.makeText(
                    requireContext(),
                    "Camera permission not granted",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
}