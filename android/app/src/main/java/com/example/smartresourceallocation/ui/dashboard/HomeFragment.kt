package com.example.smartresourceallocation.ui.dashboard

import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.smartresourceallocation.databinding.FragmentHomeBinding
import androidx.lifecycle.ViewModelProvider
import com.example.smartresourceallocation.viewmodel.ReservationViewModel
import com.example.smartresourceallocation.utils.SharedPrefManager

class HomeFragment : Fragment() {

    private var _binding:
            FragmentHomeBinding? = null

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
            FragmentHomeBinding.inflate(
                inflater,
                container,
                false
            )

        viewModel =
            ViewModelProvider(this)[
                ReservationViewModel::class.java
            ]

        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        observeReservations()

        loadReservations()

        val userName =
            SharedPrefManager(
                requireContext()
            ).getUserName()

        binding.tvWelcome.text =
            "Hello, $userName 👋"

    }
    private fun loadReservations() {

        val token =
            SharedPrefManager(
                requireContext()
            ).getToken()

        if(token != null){

            viewModel.getMyReservations(
                "Bearer $token"
            )

        }

    }
    private fun observeReservations() {

        viewModel.reservations.observe(
            viewLifecycleOwner
        ) { reservations ->

            binding.tvTotalReservations.text =
                reservations.size.toString()

            binding.tvPendingReservations.text =
                reservations.count {

                    it.status == "PENDING"

                }.toString()

            binding.tvApprovedReservations.text =
                reservations.count {

                    it.status == "APPROVED"

                }.toString()

            binding.tvWaitlistedReservations.text =
                reservations.count {

                    it.status == "WAITLISTED"

                }.toString()

            if(reservations.isNotEmpty()){

                binding.tvRecentReservation1.text =
                    formatReservation(
                        reservations[0].requestedResource.name,
                        reservations[0].status
                    )

            }

            if(reservations.size > 1){

                binding.tvRecentReservation2.text =
                    formatReservation(
                        reservations[1].requestedResource.name,
                        reservations[1].status
                    )

            }

            if(reservations.size > 2){

                binding.tvRecentReservation3.text =
                    formatReservation(
                        reservations[2].requestedResource.name,
                        reservations[2].status
                    )

            }

        }

    }
    private fun formatReservation(
        name: String,
        status: String
    ): SpannableString {

        val statusText = when(status){

            "APPROVED" -> "✓ APPROVED"

            "PENDING" -> "⏳ PENDING"

            "WAITLISTED" -> "📋 WAITLISTED"

            "CANCELLED" -> "✖ CANCELLED"

            else -> status

        }

        val text =
            "$name\n$statusText"

        val span =
            SpannableString(text)

        span.setSpan(
            android.text.style.StyleSpan(
                android.graphics.Typeface.BOLD
            ),
            0,
            name.length,
            android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return span

    }

}