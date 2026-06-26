package com.example.smartresourceallocation.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartresourceallocation.adapter.ReservationAdapter
import com.example.smartresourceallocation.databinding.FragmentReservationBinding
import com.example.smartresourceallocation.utils.SharedPrefManager
import com.example.smartresourceallocation.viewmodel.ReservationViewModel
import com.example.smartresourceallocation.model.Reservation
import android.content.Intent
import com.example.smartresourceallocation.ui.reservation.ReservationDetailsActivity

class ReservationFragment : Fragment() {

    private var _binding:
            FragmentReservationBinding? = null

    private val binding
        get() = _binding!!

    private lateinit var viewModel:
            ReservationViewModel

    private lateinit var adapter:
            ReservationAdapter

    private var allReservations =
        listOf<Reservation>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentReservationBinding.inflate(
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

        setupRecyclerView()

        setupObservers()

        setupTabs()

        loadReservations()

    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null

    }
    override fun onResume() {

        super.onResume()

        loadReservations()

    }

    private fun setupRecyclerView() {

        adapter =
            ReservationAdapter(
                emptyList()
            ) { reservation ->

                openReservationDetails(
                    reservation
                )

            }

        binding.rvReservations.layoutManager =
            LinearLayoutManager(
                requireContext()
            )

        binding.rvReservations.adapter =
            adapter

    }

    private fun setupObservers() {

        viewModel =
            ViewModelProvider(this)[
                ReservationViewModel::class.java
            ]

        viewModel.reservations.observe(
            viewLifecycleOwner
        ) {

            allReservations = it

            adapter.updateList(it)

        }

        viewModel.errorMessage.observe(
            viewLifecycleOwner
        ) {

            Toast.makeText(
                requireContext(),
                it,
                Toast.LENGTH_SHORT
            ).show()

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

    private fun filterReservations(
        status: String
    ) {

        if(status == "ALL"){

            adapter.updateList(
                allReservations
            )

            return

        }

        val filteredList =
            allReservations.filter {

                it.status == status

            }

        adapter.updateList(
            filteredList
        )

    }

    private fun resetTabs() {

        binding.tabAll.isSelected = false
        binding.tabPending.isSelected = false
        binding.tabApproved.isSelected = false
        binding.tabWaitlisted.isSelected = false
        binding.tabCancelled.isSelected = false
        binding.tabRejected.isSelected = false

    }
    private fun setupTabs() {

        binding.tabAll.setOnClickListener {

            resetTabs()

            binding.tabAll.isSelected = true

            filterReservations("ALL")

        }

        binding.tabPending.setOnClickListener {

            resetTabs()

            binding.tabPending.isSelected = true

            filterReservations("PENDING")


        }

        binding.tabApproved.setOnClickListener {

            resetTabs()

            binding.tabApproved.isSelected = true

            filterReservations("APPROVED")

        }

        binding.tabWaitlisted.setOnClickListener {

            resetTabs()

            binding.tabWaitlisted.isSelected = true

            filterReservations("WAITLISTED")

        }

        binding.tabCancelled.setOnClickListener {

            resetTabs()

            binding.tabCancelled.isSelected = true

            filterReservations("CANCELLED")
        }
        binding.tabRejected.setOnClickListener {

            resetTabs()

            binding.tabRejected.isSelected = true

            filterReservations("REJECTED")

        }
        binding.tabAll.performClick()

    }
    private fun openReservationDetails(
        reservation: Reservation
    ) {

        val intent =
            Intent(
                requireContext(),
                ReservationDetailsActivity::class.java
            )


        intent.putExtra(
            "resourceName",
            reservation.requestedResource.name
        )

        intent.putExtra(
            "date",
            reservation.date
        )

        intent.putExtra(
            "time",
            reservation.startTime
        )

        intent.putExtra(
            "duration",
            reservation.durationHours
        )

        intent.putExtra(
            "purpose",
            reservation.purpose
        )

        intent.putExtra(
            "allocation",
            reservation.allocationPreference
        )

        intent.putExtra(
            "status",
            reservation.status
        )

        intent.putExtra(
            "resourceId",
            reservation.requestedResource._id
        )

        intent.putExtra(
            "reservationId",
            reservation._id
        )

        intent.putExtra(
            "category",
            reservation.requestedResource.category
        )

        intent.putExtra(
            "description",
            reservation.requestedResource.description
        )

        intent.putExtra(
            "location",
            reservation.requestedResource.location
        )

        intent.putExtra(
            "resourceType",
            reservation.requestedResource.resourceType
        )

        intent.putExtra(
            "capacity",
            reservation.requestedResource.capacity
        )

        intent.putExtra(
            "availableUnits",
            reservation.requestedResource.availableUnits
        )

        intent.putExtra(
            "bookingOpenBeforeHours",
            reservation.requestedResource.bookingOpenBeforeHours
        )

        intent.putExtra(
            "bookingWindowDurationHours",
            reservation.requestedResource.bookingWindowDurationHours
        )

        intent.putExtra(
            "participantCount",
            reservation.participantCount
        )

        intent.putExtra(
            "quantityRequired",
            reservation.quantityRequired
        )

        intent.putExtra(
            "category",
            reservation.requestedResource.category
        )

        intent.putExtra(
            "description",
            reservation.requestedResource.description
        )

        intent.putExtra(
            "location",
            reservation.requestedResource.location
        )

        intent.putExtra(
            "resourceType",
            reservation.requestedResource.resourceType
        )

        intent.putExtra(
            "capacity",
            reservation.requestedResource.capacity
        )

        intent.putExtra(
            "availableUnits",
            reservation.requestedResource.availableUnits
        )

        intent.putExtra(
            "bookingOpenBeforeHours",
            reservation.requestedResource.bookingOpenBeforeHours
        )

        intent.putExtra(
            "bookingWindowDurationHours",
            reservation.requestedResource.bookingWindowDurationHours
        )

        intent.putExtra(
            "reservationId",
            reservation._id
        )

        startActivity(intent)

    }
}