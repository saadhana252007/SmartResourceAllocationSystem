package com.example.smartresourceallocation.ui.admin.fragments.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.smartresourceallocation.databinding.AdminFragmentProfileBinding
import com.example.smartresourceallocation.ui.auth.LoginActivity
import com.example.smartresourceallocation.utils.SharedPrefManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.smartresourceallocation.ui.admin.bottomsheet.EditProfileBottomSheet
import com.example.smartresourceallocation.viewmodel.ProfileViewModel
import com.example.smartresourceallocation.ui.admin.bottomsheet.ChangePasswordBottomSheet

class ProfileFragment : Fragment() {

    private var _binding: AdminFragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPrefManager: SharedPrefManager

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = AdminFragmentProfileBinding.inflate(inflater, container, false)

        sharedPrefManager = SharedPrefManager(requireContext())

        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        setupObservers()

        loadData()

        setupClickListeners()

        return binding.root
    }
    private fun loadData() {

        val token = sharedPrefManager.getToken()

        if (token == null) {

            Toast.makeText(
                requireContext(),
                "Please login again",
                Toast.LENGTH_SHORT
            ).show()

            logout()

            return
        }

        profileViewModel.loadProfile("Bearer $token")
        profileViewModel.loadSummary("Bearer $token")

    }
    private fun setupObservers() {

        profileViewModel.profile.observe(viewLifecycleOwner){ profile ->

            binding.tvUserName.text = profile.name

            binding.tvEmail.text = profile.email

            binding.tvRole.text = profile.role

            binding.tvFullName.text =
                " Full Name : ${profile.name}"

            binding.tvProfileEmail.text =
                " Email : ${profile.email}"

            binding.tvProfileRole.text =
                " Role : ${profile.role}"

            binding.tvCreatedDate.text =
                " Account Created : ${
                    com.example.smartresourceallocation.utils.DateUtils.format(
                        profile.createdAt
                    )
                }"

        }



        profileViewModel.summary.observe(viewLifecycleOwner) { summary ->

            binding.tvResourcesManaged.text =
                summary.resourcesManaged.toString()

            binding.tvReservationsProcessed.text =
                summary.reservationsProcessed.toString()

            binding.tvPendingRequests.text =
                summary.pendingRequests.toString()

            binding.tvSystemUtilization.text =
                "${summary.systemUtilization}%"

        }


        profileViewModel.error.observe(viewLifecycleOwner) {

            Toast.makeText(

                requireContext(),

                it,

                Toast.LENGTH_SHORT

            ).show()

        }

    }

    private fun setupClickListeners() {

        binding.tvEditProfile.setOnClickListener {

            EditProfileBottomSheet().show(

                parentFragmentManager,

                "EditProfile"

            )

        }

        binding.tvChangePassword.setOnClickListener {

            ChangePasswordBottomSheet().show(

                parentFragmentManager,

                "ChangePassword"

            )

        }

        binding.tvPrivacyPolicy.setOnClickListener {

            com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())

                .setTitle("Privacy Policy")

                .setMessage(

                    """
Your information is securely stored.

• Passwords are encrypted.

• Authentication is protected using JWT.

• Personal information is used only for authentication and reservation management.

• No personal information is shared with third parties.

By using this application you agree to this privacy policy.
            """.trimIndent()

                )

                .setPositiveButton("OK", null)

                .show()

        }

        binding.tvAbout.setOnClickListener {

            com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())

                .setTitle("About")

                .setMessage(

                    """
OPTI SOURCE                        

Smart Resource Allocation System

Version 1.0

Developed using

• Kotlin
• XML
• Node.js
• MongoDB

This application helps manage shared resources efficiently by reducing booking conflicts and improving resource utilization.

© 2026 Smart Resource Allocation System
            """.trimIndent()

                )

                .setPositiveButton("OK", null)

                .show()

        }

        binding.tvVersion.setOnClickListener {

            com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())

                .setTitle("Application Version")

                .setMessage(

                    """
Version : 1.0.0

Build : 1

Release Date

July 2026
            """.trimIndent()

                )

                .setPositiveButton("OK", null)

                .show()

        }

        binding.btnLogout.setOnClickListener {

            logout()

        }
    }

    private fun logout() {

        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())

            .setTitle("Logout")

            .setMessage("Are you sure you want to logout?")

            .setPositiveButton("Logout") { _, _ ->

                sharedPrefManager.clearSession()

                val intent = Intent(

                    requireContext(),

                    LoginActivity::class.java

                )

                intent.flags =

                    Intent.FLAG_ACTIVITY_NEW_TASK or

                            Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)

                requireActivity().finish()

            }

            .setNegativeButton("Cancel", null)

            .show()

    }

    override fun onResume() {
        super.onResume()

        loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}