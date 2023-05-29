package com.example.plantapp.ui.main.specie

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.plantapp.R
import com.example.plantapp.databinding.FragmentSpecieDetailBinding
import com.example.plantapp.network.ApiClient
import com.example.plantapp.network.repository.article.FireStoreRepository
import com.example.plantapp.network.repository.plant.PlantRepository
import com.example.plantapp.ui.ViewModelFactory
import com.example.plantapp.ui.main.profile.ProfileViewModel

class SpecieDetailFragment : Fragment() {

    private var _binding: FragmentSpecieDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SpeciesViewModel
    private lateinit var profileViewModel: ProfileViewModel

    private val args: SpecieDetailFragmentArgs by navArgs()
    private var id: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = args.id
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSpecieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val service = ApiClient.plantService
        val mainRepository = PlantRepository(service)
        viewModel = ViewModelProvider(requireActivity(), ViewModelFactory(mainRepository))[SpeciesViewModel::class.java]

        profileViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(mainRepository, fireStoreRepository = FireStoreRepository())
        )[ProfileViewModel::class.java]

        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE) ?: return
        var liked = sharedPref.getString("liked", "")


        viewModel.specieDetailList.observe(viewLifecycleOwner) {
            if (id == -1) return@observe
            val detail = it.find { item -> item.id == id } ?: return@observe
            binding.apply {
                setUpLiked(liked)
                Glide.with(requireContext()).load(detail.default_image?.medium_url).into(background)
                title.text = detail.scientific_name?.get(0) ?:""
                cycle.text = detail.cycle
                watering.text = detail.watering
                rating.numStars = 5
                rating.rating = (0..5).random().toFloat()
                description.text = "The word \"cactus\" derives, through Latin, from \n" +
                        "the Ancient Greek κάκτος, kaktos, a name orig\n" +
                        "inally used by Theophrastus for a spiny plant \n" +
                        "whose identity is not certain. Cacti occur in a \n" +
                        "wide range of shapes and sizes. Most cacti live \n" +
                        "in habitats subject to at least some drought. "
            }
        }

        profileViewModel.itemDetail.observe(viewLifecycleOwner) { detail ->
            binding.apply {
                id = detail.id?: -1
                setUpLiked(liked)
                Glide.with(requireContext()).load(detail.default_image?.medium_url).into(background)
                title.text = detail.scientific_name?.get(0) ?:""
                cycle.text = detail.cycle
                watering.text = detail.watering
                rating.numStars = 5
                rating.rating = (0..5).random().toFloat()
                description.text = "The word \"cactus\" derives, through Latin, from \n" +
                        "the Ancient Greek κάκτος, kaktos, a name orig\n" +
                        "inally used by Theophrastus for a spiny plant \n" +
                        "whose identity is not certain. Cacti occur in a \n" +
                        "wide range of shapes and sizes. Most cacti live \n" +
                        "in habitats subject to at least some drought. "
            }
        }

        binding.imvBack.setOnClickListener { findNavController().popBackStack() }

        binding.imvLiked.setOnClickListener {
            if (liked != null) {
                if (!liked!!.contains(id.toString())) {
                    with(sharedPref.edit()) {
                        putString("liked", "$liked $id ")
                        apply()
                    }
                    liked = sharedPref.getString("liked", "")
                    binding.imvLiked.setImageResource(R.drawable.background_like)
                } else {
                    with(sharedPref.edit()) {
                        putString("liked", liked!!.replace("$id ", ""))
                        apply()
                    }
                    liked = sharedPref.getString("liked", "")
                    binding.imvLiked.setImageResource(R.drawable.background_unlike)
                }
            } else {
                with(sharedPref.edit()) {
                    putString("liked", "$id ")
                    apply()
                }
            }
        }
    }

    fun setUpLiked(liked: String?) {
        if (liked != null) {
            if (liked.contains(id.toString())) {
                binding.imvLiked.setImageResource(R.drawable.background_like)
            } else {
                binding.imvLiked.setImageResource(R.drawable.background_unlike)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}