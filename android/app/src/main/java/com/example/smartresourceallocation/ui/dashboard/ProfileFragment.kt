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

        binding.btnLogout.setOnClickListener {

            logout()

        }

    }

    private fun setupProfile() {

        val pref =
            SharedPrefManager(
                requireContext()
            )

        binding.tvUserName.text =
            pref.getUserName() ?: "User"

        binding.tvRole.text =
            pref.getRole() ?: "USER"

        if(pref.getRole() == "ADMIN"){

            binding.tvRoleBadge.text =
                "RESOURCE ADMIN"

        }else{

            binding.tvRoleBadge.text =
                "RESOURCE USER"

        }

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

        viewModel.reservations.observe(
            viewLifecycleOwner
        ) { reservations ->

            binding.tvTotalMade.text =
                reservations.size.toString()

            binding.tvApprovedCount.text =
                reservations.count {

                    it.status == "APPROVED"

                }.toString()

            val categoryCount =
                mutableMapOf<String, Int>()

            reservations.forEach {

                val category =
                    it.requestedResource.category

                categoryCount[category] =
                    categoryCount.getOrDefault(
                        category,
                        0
                    ) + 1

            }

            val mostReservedCategory =
                categoryCount.maxByOrNull {

                    it.value

                }?.key ?: "-"

            binding.tvMostReserved.text =
                mostReservedCategory

        }

    }

    private fun logout() {

        val pref =
            SharedPrefManager(
                requireContext()
            )

        pref.clearToken()

        pref.clearRole()

        startActivity(
            Intent(
                requireContext(),
                LoginActivity::class.java
            )
        )

        requireActivity().finish()

    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null

    }

}