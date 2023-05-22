package com.example.plantapp.ui.main.specie

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.plantapp.R
import com.example.plantapp.data.model.Specie
import com.example.plantapp.databinding.FragmentSpeciesDetailListBinding
import com.example.plantapp.network.ApiClient
import com.example.plantapp.network.repository.plant.PlantRepository
import com.example.plantapp.ui.ViewModelFactory

class SpeciesDetailListFragment : Fragment() {

    private var _binding: FragmentSpeciesDetailListBinding? = null
    private val binding get() = _binding!!

    private lateinit var type: String
    private val args: SpeciesDetailListFragmentArgs by navArgs()

    private lateinit var viewModel: SpeciesViewModel
    private val adapter = SpeciesListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = args.type
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSpeciesDetailListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val service = ApiClient.plantService
        val mainRepository = PlantRepository(service)
        viewModel = ViewModelProvider(requireActivity(), ViewModelFactory(mainRepository))[SpeciesViewModel::class.java]

        initView()
        initEvent()
        initData()
    }

    private fun initData() {
        viewModel.species.observe(viewLifecycleOwner) { list ->
            Log.d("TAG", "specieDetailListspecies: ${list.size}")
            val inputStream = resources.openRawResource(R.raw.config)
            val text = inputStream.bufferedReader().use { it.readText() }
            val key = text.substringAfter("=")
            val filterList = list.filter { it?.type == type }
            viewModel.getDetailSpecies(list = filterList as MutableList<Specie?>, key)
        }

        viewModel.specieDetailList.observe(viewLifecycleOwner) {
            if (it.size == 0) return@observe
            Log.d("TAG", "specieDetailList: ${it.size}")
            val newList = it.toMutableList()
            adapter.submitList(newList)
//            adapter.notifyDataSetChanged()
        }
    }

    private fun initEvent() {
        adapter.onItemClick = {
            findNavController().navigate(SpeciesDetailListFragmentDirections.actionSpeciesDetailListFragmentToSpecieDetailFragment(it.id))
        }
    }

    private fun initView() {
        binding.imvBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.rcSpeciesListType.adapter = adapter

        binding.tvArticleTitle.text = type
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}