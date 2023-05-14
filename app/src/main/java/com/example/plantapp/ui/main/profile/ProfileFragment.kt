package com.example.plantapp.ui.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.plantapp.R
import com.example.plantapp.databinding.FragmentHomeBinding
import com.example.plantapp.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    private fun initEvent() {
        binding.apply {
            btnArticle.setOnClickListener {
                btnArticle.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_item_profile)
                btnArticle.setTextColor(requireContext().getColor(R.color.white))
                btnSpecies.background = ContextCompat.getDrawable(requireContext(), R.color.white)
                btnSpecies.setTextColor(requireContext().getColor(R.color.colorText))
            }
            btnSpecies.setOnClickListener {
                btnSpecies.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_item_profile)
                btnSpecies.setTextColor(requireContext().getColor(R.color.white))
                btnArticle.background = ContextCompat.getDrawable(requireContext(), R.color.white)
                btnArticle.setTextColor(requireContext().getColor(R.color.colorText))

            }
        }
    }
}