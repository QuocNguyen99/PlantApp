package com.example.plantapp.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.plantapp.R
import com.example.plantapp.databinding.FragmentForgetPasswordBinding
import com.example.plantapp.databinding.FragmentLoginBinding
import com.example.plantapp.utils.isValidEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ForgetPasswordFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    private var _binding: FragmentForgetPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        initEvent()
    }

    private fun initEvent() {
        binding.apply {
            btnReset.setOnClickListener {
                val emailAddress = edtEmail.text.toString()
                val isValidEmail = emailAddress.isValidEmail()

                if (isValidEmail) {
                    Firebase.auth.sendPasswordResetEmail(emailAddress)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Vui lòng kiểm tra email để thay đổi mật khẩu", Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener { Log.d("TAG", "addOnFailureListener: ${it.message}") }
                } else {
                    Toast.makeText(requireContext(), "Sai email", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}