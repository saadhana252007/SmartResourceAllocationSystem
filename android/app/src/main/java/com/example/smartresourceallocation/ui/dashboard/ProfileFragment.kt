package com.example.smartresourceallocation.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.smartresourceallocation.databinding.FragmentProfileBinding
import com.example.smartresourceallocation.ui.auth.LoginActivity
import com.example.smartresourceallocation.utils.SharedPrefManager
import com.example.smartresourceallocation.viewmodel.ReservationViewModel
import android.widget.Toast
import com.example.smartresourceallocation.ui.admin.bottomsheet.ChangePasswordBottomSheet
import com.example.smartresourceallocation.ui.admin.bottomsheet.EditProfileBottomSheet
import com.example.smartresourceallocation.utils.DateUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProfileFragment : Fragment() {

    private var _binding:
            FragmentProfileBinding? = null

    private val binding
        get() = _binding!!

    private lateinit var viewModel:
            ReservationViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentProfileBinding.inflate(
                inflater,
                container,
                false
            )

        return binding.root

    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        viewModel =
            ViewModelProvider(this)[
                ReservationViewModel::class.java
            ]

        setupProfile()

        observeReservations()

        loadReservations()

        setupClickListeners()

        binding.btnLogout.setOnClickListener {

            logout()

        }

    }

    private fun setupProfile() {

        val pref = SharedPrefManager(requireContext())

        binding.tvUserName.text =
            pref.getUserName() ?: "User"

        binding.tvEmail.text =
            pref.getEmail() ?: "Email"

        binding.tvRole.text =
            if (pref.getRole() == "USER")
                "RESOURCE USER"
            else
                pref.getRole()

        binding.tvFullName.text =
            "👤 Full Name : ${pref.getUserName()}"

        binding.tvProfileEmail.text =
            "📧 Email : ${pref.getEmail()}"

        binding.tvProfileRole.text =
            "🛡 Role : ${pref.getRole()}"

        binding.tvCreatedDate.text =
            "📅 Account Created : ${
                pref.getCreatedAt()?.let {
                    DateUtils.formatCreatedDate(it)
                } ?: "--"
            }"

        binding.tvRoleBadge.text =
            "RESOURCE USER"

    }

    private fun loadReservations() {

        val token =
            SharedPrefManager(
                requireContext()
            ).getToken()

        if (token != null) {

            viewModel.getMyReservations(
                "Bearer $token"
            )

        }

    }

    private fun observeReservations() {

        viewModel.reservations.observe(viewLifecycleOwner) { reservations ->

            binding.tvResourcesManaged.text =
                reservations.size.toString()

            val approved =
                reservations.count {

                    it.status == "APPROVED" ||
                            it.status == "ALTERNATIVE_APPROVED"

                }

            binding.tvReservationsProcessed.text =
                approved.toString()

            val pending =
                reservations.count {

                    it.status == "PENDING"

                }

            binding.tvPendingRequests.text =
                pending.toString()

            val categoryMap =
                mutableMapOf<String, Int>()

            reservations.forEach {

                val category =
                    it.requestedResource?.category
                        ?: it.resourceCategory

                categoryMap[category] =
                    categoryMap.getOrDefault(
                        category,
                        0
                    ) + 1

            }

            binding.tvSystemUtilization.text =
                categoryMap.maxByOrNull {

                    it.value

                }?.key ?: "-"

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

            MaterialAlertDialogBuilder(requireContext())

                .setTitle("Privacy Policy")

                .setMessage(
                    "Your information is securely stored. Passwords are encrypted and protected using JWT authentication."
                )

                .setPositiveButton("OK", null)

                .show()

        }

        binding.tvAbout.setOnClickListener {

            MaterialAlertDialogBuilder(requireContext())

                .setTitle("About")

                .setMessage(
                    "OPTI SOURCE\n\nSmart Resource Allocation System\n\nVersion 1.0\n\nDeveloped using Kotlin, XML, Node.js and MongoDB."
                )

                .setPositiveButton("OK", null)

                .show()

        }

        binding.tvVersion.setOnClickListener {

            MaterialAlertDialogBuilder(requireContext())

                .setTitle("Application Version")

                .setMessage("Version 1.0.0")

                .setPositiveButton("OK", null)

                .show()

        }

    }

    private fun logout() {

        MaterialAlertDialogBuilder(requireContext())

            .setTitle("Logout")

            .setMessage("Are you sure you want to logout?")

            .setPositiveButton("Logout") { _, _ ->

                val pref =
                    SharedPrefManager(requireContext())

                pref.clearSession()

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
    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null

    }
    override fun onResume() {

        super.onResume()

        setupProfile()

        loadReservations()

    }

}