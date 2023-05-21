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
import com.example.plantapp.databinding.FragmentLoginBinding
import com.example.plantapp.databinding.FragmentWalkThroughBinding
import com.example.plantapp.utils.isValidEmail
import com.example.plantapp.utils.isValidPassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    var currentUser: FirebaseUser? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = auth.currentUser
        if (currentUser != null) {
            reloadUI()
        }
    }

    private fun reloadUI() {
        binding.apply {
            edtEmail.setText(currentUser?.email.toString())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    private fun initEvent() {
        binding.apply {
            btnLogin.setOnClickListener {
                val isValidEmail = edtEmail.text.toString().isValidEmail()
                val isValidPassword = edtPassword.text.toString().isValidPassword()
                if (isValidEmail && isValidPassword) {
                    try {
                        auth.signInWithEmailAndPassword(edtEmail.text.toString(), edtPassword.text.toString())
                            .addOnCompleteListener(requireActivity()) { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    findNavController().navigate(R.id.main)
                                } else {
                                    Log.e("TAG", "signInWithEmail:failure", task.exception)
                                    Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }catch (ex:Exception){
                        Log.e("TAG", "signInWithEmail:failure: ${ex.message}")
                    }
                }
            }
            tvSignUp.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
            }
            tvForget.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_forgetPasswordFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}