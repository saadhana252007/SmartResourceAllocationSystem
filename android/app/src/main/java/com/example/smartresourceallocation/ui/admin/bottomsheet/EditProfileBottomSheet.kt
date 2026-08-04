package com.example.smartresourceallocation.ui.admin.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.smartresourceallocation.databinding.BottomSheetEditProfileBinding
import com.example.smartresourceallocation.model.UpdateProfileRequest
import com.example.smartresourceallocation.utils.SharedPrefManager
import com.example.smartresourceallocation.viewmodel.ProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditProfileBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetEditProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel
    private lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = BottomSheetEditProfileBinding.inflate(
            inflater,
            container,
            false
        )

        sharedPrefManager = SharedPrefManager(requireContext())

        viewModel =
            ViewModelProvider(this)[ProfileViewModel::class.java]

        loadProfile()

        observeViewModel()

        binding.btnUpdate.setOnClickListener {

            updateProfile()

        }

        return binding.root
    }

    private fun loadProfile() {

        val token = sharedPrefManager.getToken()

        if (token != null) {

            viewModel.loadProfile(

                "Bearer $token"

            )

        }

    }

    private fun observeViewModel() {

        viewModel.profile.observe(viewLifecycleOwner) {

            binding.etName.setText(it.name)

            binding.etEmail.setText(it.email)

        }

        viewModel.updateProfile.observe(viewLifecycleOwner) {

            sharedPrefManager.saveUserName(
                it.user.name
            )

            sharedPrefManager.saveEmail(
                it.user.email
            )

            Toast.makeText(
                requireContext(),
                "Profile Updated Successfully",
                Toast.LENGTH_SHORT
            ).show()

            dismiss()

        }

        viewModel.error.observe(viewLifecycleOwner) {

            Toast.makeText(

                requireContext(),

                it,

                Toast.LENGTH_SHORT

            ).show()

        }

        viewModel.loading.observe(viewLifecycleOwner) {

            binding.btnUpdate.isEnabled = !it

            binding.btnUpdate.text =

                if (it)

                    "Updating..."

                else

                    "SAVE CHANGES"

        }

    }

    private fun updateProfile() {

        binding.etName.error = null
        binding.etEmail.error = null

        val name =

            binding.etName.text.toString().trim()

        val email =

            binding.etEmail.text.toString().trim()

        if (name.isEmpty()) {

            binding.etName.error =

                "Enter Name"

            return

        }

        if (name.length < 3) {

            binding.etName.error =

                "Minimum 3 characters"

            return

        }

        if (email.isEmpty()) {

            binding.etEmail.error =

                "Enter Email"

            return

        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()
        ) {

            binding.etEmail.error =

                "Invalid Email"

            return

        }

        val token =

            sharedPrefManager.getToken()

        if (token == null) {

            Toast.makeText(

                requireContext(),

                "Please login again",

                Toast.LENGTH_SHORT

            ).show()

            dismiss()

            return

        }

        viewModel.updateProfile(

            "Bearer $token",

            UpdateProfileRequest(

                name,

                email

            )

        )

    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null

    }

}