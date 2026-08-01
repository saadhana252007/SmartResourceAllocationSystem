package com.example.smartresourceallocation.ui.admin.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.smartresourceallocation.databinding.BottomSheetChangePasswordBinding
import com.example.smartresourceallocation.model.ChangePasswordRequest
import com.example.smartresourceallocation.utils.SharedPrefManager
import com.example.smartresourceallocation.viewmodel.ProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChangePasswordBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetChangePasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel
    private lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            BottomSheetChangePasswordBinding.inflate(
                inflater,
                container,
                false
            )

        sharedPrefManager =
            SharedPrefManager(requireContext())

        viewModel =
            ViewModelProvider(this)[ProfileViewModel::class.java]

        observeViewModel()

        binding.btnChangePassword.setOnClickListener {

            changePassword()

        }

        return binding.root
    }

    private fun changePassword() {
        binding.etNewPassword.error = null
        binding.etConfirmPassword.error = null
        binding.etCurrentPassword.error = null
        val currentPassword =
            binding.etCurrentPassword.text.toString().trim()

        val newPassword =
            binding.etNewPassword.text.toString().trim()

        val confirmPassword =
            binding.etConfirmPassword.text.toString().trim()

        if (currentPassword.isEmpty()) {

            binding.etCurrentPassword.error =
                "Enter current password"

            return

        }

        if (newPassword.isEmpty()) {

            binding.etNewPassword.error =
                "Enter new password"

            return

        }

        if (newPassword.length < 6) {

            binding.etNewPassword.error =
                "Minimum 6 characters"

            return

        }

        if (confirmPassword.isEmpty()) {

            binding.etConfirmPassword.error =
                "Confirm password"

            return

        }

        if (newPassword != confirmPassword) {

            binding.etConfirmPassword.error =
                "Passwords do not match"

            return

        }
        if (currentPassword == newPassword) {

            binding.etNewPassword.error =
                "New password must be different"

            return

        }

        val token =
            sharedPrefManager.getToken()

        if (token == null) {

            Toast.makeText(

                requireContext(),

                "Session expired. Login again.",

                Toast.LENGTH_SHORT

            ).show()

            dismiss()

            return

        }

        viewModel.changePassword(

            "Bearer $token",

            ChangePasswordRequest(

                currentPassword,

                newPassword

            )

        )

    }

    private fun observeViewModel() {

        viewModel.passwordChanged.observe(viewLifecycleOwner) {

            if (it) {

                Toast.makeText(

                    requireContext(),

                    "Password Updated Successfully",

                    Toast.LENGTH_SHORT

                ).show()

                dismiss()

            }

        }

        viewModel.error.observe(viewLifecycleOwner) {

            Toast.makeText(

                requireContext(),

                it,

                Toast.LENGTH_SHORT

            ).show()

        }

        viewModel.loading.observe(viewLifecycleOwner) {

            binding.btnChangePassword.isEnabled = !it

            binding.btnChangePassword.text =

                if (it)

                    "Updating..."

                else

                    "UPDATE PASSWORD"

        }

    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null

    }

}