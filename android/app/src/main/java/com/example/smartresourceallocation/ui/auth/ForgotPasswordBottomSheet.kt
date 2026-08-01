package com.example.smartresourceallocation.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.smartresourceallocation.databinding.BottomSheetForgotPasswordBinding
import com.example.smartresourceallocation.model.ForgotPasswordRequest
import com.example.smartresourceallocation.viewmodel.AuthViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.appcompat.app.AlertDialog

class ForgotPasswordBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            BottomSheetForgotPasswordBinding.inflate(
                inflater,
                container,
                false
            )

        viewModel =
            ViewModelProvider(this)[AuthViewModel::class.java]

        observeViewModel()

        binding.btnSendOtp.setOnClickListener {

            sendOtp()

        }

        return binding.root
    }

    private fun sendOtp() {

        val email =
            binding.etEmail.text.toString().trim()

        if (email.isEmpty()) {

            binding.etEmail.error = "Enter Email"

            return

        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            binding.etEmail.error = "Invalid Email"

            return

        }

        viewModel.forgotPassword(

            ForgotPasswordRequest(email)

        )

    }

    private fun observeViewModel() {

        viewModel.forgotPassword.observe(viewLifecycleOwner) {

            Toast.makeText(

                requireContext(),

                "OTP Sent Successfully",

                Toast.LENGTH_SHORT

            ).show()

            showOtpDialog()

        }

        viewModel.otpVerified.observe(viewLifecycleOwner) {

            Toast.makeText(

                requireContext(),

                "OTP Verified",

                Toast.LENGTH_SHORT

            ).show()

            showResetPasswordDialog()

        }

        viewModel.passwordReset.observe(viewLifecycleOwner) {

            Toast.makeText(

                requireContext(),

                "Password Reset Successfully",

                Toast.LENGTH_LONG

            ).show()

            dismiss()

        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {

            Toast.makeText(

                requireContext(),

                it,

                Toast.LENGTH_SHORT

            ).show()

        }

    }
    private fun showOtpDialog() {

        val otpInput = android.widget.EditText(requireContext())

        otpInput.hint = "Enter OTP"

        val dialog =

            com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())

                .setTitle("OTP Verification")

                .setView(otpInput)

                .setCancelable(false)

                .setPositiveButton("Verify", null)

                .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener {

                val otp = otpInput.text.toString().trim()

                if (otp.isEmpty()) {

                    Toast.makeText(

                        requireContext(),

                        "Enter OTP",

                        Toast.LENGTH_SHORT

                    ).show()

                    return@setOnClickListener

                }

                viewModel.verifyOtp(

                    com.example.smartresourceallocation.model.VerifyOtpRequest(

                        binding.etEmail.text.toString().trim(),

                        otp

                    )

                )

                dialog.dismiss()

            }

    }
    private fun showResetPasswordDialog() {

        val layout = android.widget.LinearLayout(requireContext())

        layout.orientation = android.widget.LinearLayout.VERTICAL

        layout.setPadding(40, 20, 40, 20)

        val newPassword = android.widget.EditText(requireContext())

        newPassword.hint = "New Password"

        newPassword.inputType =
            android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

        val confirmPassword = android.widget.EditText(requireContext())

        confirmPassword.hint = "Confirm Password"

        confirmPassword.inputType =
            android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

        layout.addView(newPassword)

        layout.addView(confirmPassword)

        val dialog =

            com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())

                .setTitle("Reset Password")

                .setView(layout)

                .setCancelable(false)

                .setPositiveButton("Reset", null)

                .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener {

                val pass = newPassword.text.toString().trim()

                val confirm = confirmPassword.text.toString().trim()

                if (pass.isEmpty()) {

                    Toast.makeText(
                        requireContext(),
                        "Enter Password",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener

                }

                if (confirm.isEmpty()) {

                    Toast.makeText(
                        requireContext(),
                        "Confirm Password",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener

                }

                if (pass.length < 6) {

                    Toast.makeText(
                        requireContext(),
                        "Password must be at least 6 characters",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener

                }

                if (pass != confirm) {

                    Toast.makeText(
                        requireContext(),
                        "Passwords do not match",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener

                }

                viewModel.resetPassword(

                    com.example.smartresourceallocation.model.ResetPasswordRequest(

                        binding.etEmail.text.toString().trim(),

                        viewModel.verifiedOtp,

                        pass

                    )

                )

                dialog.dismiss()

            }

    }
    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null

    }
}

