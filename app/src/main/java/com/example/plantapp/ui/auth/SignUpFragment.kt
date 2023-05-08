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
import com.example.plantapp.data.User
import com.example.plantapp.databinding.FragmentSignUpBinding
import com.example.plantapp.databinding.FragmentWalkThroughBinding
import com.example.plantapp.utils.isValidEmail
import com.example.plantapp.utils.isValidPassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize Firebase Auth
        auth = Firebase.auth
        initEvent()
    }

    private fun initEvent() {
        binding.apply {
            btnSignUp.setOnClickListener {
                Log.d("TAG", "createUserWithEmail:success")
                val isValidEmail = edtEmail.text.toString().isValidEmail()
                val isValidPassword = edtPassword.text.toString().isValidPassword()
                if (isValidEmail && isValidPassword) {
                    auth.createUserWithEmailAndPassword(edtEmail.text.toString(), edtPassword.text.toString())
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "createUserWithEmail:success")
                                val user = auth.currentUser
                                db.collection("auth").document(System.currentTimeMillis().toString())
                                    .set(User(edtEmail.text.toString(), edtFullName.text.toString()))
                                    .addOnCompleteListener { findNavController().popBackStack() }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", "createUserWithEmail:failure", task.exception)
                                Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(requireContext(), "Invalid email or password.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}