package com.example.smartresourceallocation.ui.admin.fragments.reservations

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartresourceallocation.R
import com.example.smartresourceallocation.adapter.AdminReservationAdapter
import com.example.smartresourceallocation.databinding.AdminFragmentReservationBinding
import com.example.smartresourceallocation.model.Reservation
import com.example.smartresourceallocation.utils.SharedPrefManager
import com.example.smartresourceallocation.ui.admin.fragments.reservations.AdminReservationDetailsActivity
import com.example.smartresourceallocation.viewmodel.ReservationViewModel

class ReservationsFragment :
    Fragment(R.layout.admin_fragment_reservation) {

    private var _binding: AdminFragmentReservationBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ReservationViewModel

    private lateinit var adapter: AdminReservationAdapter

    private var allReservations =
        listOf<Reservation>()

    private var displayedReservations =
        listOf<Reservation>()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(view, savedInstanceState)

        _binding =
            AdminFragmentReservationBinding.bind(view)

        viewModel =
            ViewModelProvider(this)[ReservationViewModel::class.java]

        setupRecycler()

        observeData()

        setupTabs()

        setupSearch()

        val token = SharedPrefManager(requireContext()).getToken()

        if (token == null) {

            Toast.makeText(
                requireContext(),
                "Please login again",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        viewModel.getReservationsForMyResources("Bearer $token")

    }

    private fun setupRecycler() {

        adapter =
            AdminReservationAdapter(
                emptyList()
            ) { reservation ->

                val intent =
                    Intent(
                        requireContext(),
                        AdminReservationDetailsActivity::class.java
                    )

                intent.putExtra(
                    "RESERVATION_ID",
                    reservation._id
                )

                startActivity(intent)

            }

        binding.rvReservations.layoutManager =
            LinearLayoutManager(requireContext())

        binding.rvReservations.adapter =
            adapter

    }

    private fun observeData() {

        viewModel.reservations.observe(viewLifecycleOwner) {

            allReservations = it

            displayedReservations = it

            adapter.updateList(displayedReservations)

            binding.tvTotalReservations.text =
                "Total Reservations : ${displayedReservations.size}"

        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {

            Toast.makeText(
                requireContext(),
                it,
                Toast.LENGTH_SHORT
            ).show()

        }

    }

    private fun setupSearch() {

        binding.etSearch.addTextChangedListener {

            val query =
                it.toString().trim().lowercase()

            val filtered =
                displayedReservations.filter { reservation ->

                    reservation.user.name.lowercase().contains(query) ||

                            reservation.user.email.lowercase().contains(query) ||

                            (reservation.requestedResource?.name ?: "Resource Deleted")
                                .lowercase()
                                .contains(query)

                }

            adapter.updateList(filtered)

            binding.tvTotalReservations.text =
                "Total Reservations : ${filtered.size}"

        }

    }

    private fun setupTabs() {

        selectTab(binding.tabAll)

        binding.tabAll.setOnClickListener {

            selectTab(binding.tabAll)

            displayedReservations = allReservations

            adapter.updateList(displayedReservations)

            binding.tvTotalReservations.text =
                "Total Reservations : ${displayedReservations.size}"

        }

        binding.tabPending.setOnClickListener {

            selectTab(binding.tabPending)

            filterStatus("PENDING")

        }

        binding.tabAllocated.setOnClickListener {

            selectTab(binding.tabAllocated)

            filterAllocated()

        }

        binding.tabWaitlisted.setOnClickListener {

            selectTab(binding.tabWaitlisted)

            filterStatus("WAITLISTED")

        }

        binding.tabCancelled.setOnClickListener {

            selectTab(binding.tabCancelled)

            filterCancelled()

        }

    }

    private fun filterStatus(
        status: String
    ) {

        displayedReservations =
            allReservations.filter {

                it.status == status

            }

        adapter.updateList(displayedReservations)

        binding.tvTotalReservations.text =
            "Total Reservations : ${displayedReservations.size}"

    }

    private fun selectTab(
        selectedTab: TextView
    ) {

        val tabs = listOf(

            binding.tabAll,

            binding.tabPending,

            binding.tabAllocated,

            binding.tabWaitlisted,

            binding.tabCancelled

        )

        tabs.forEach {

            it.isSelected = false

        }

        selectedTab.isSelected = true

    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null

    }
    private fun filterAllocated() {

        displayedReservations =
            allReservations.filter {

                it.status == "APPROVED" ||

                        it.status == "ALTERNATIVE_APPROVED"

            }

        adapter.updateList(displayedReservations)

        binding.tvTotalReservations.text =
            "Total Reservations : ${displayedReservations.size}"

    }
    private fun filterCancelled() {

        displayedReservations =
            allReservations.filter {

                it.status == "CANCELLED" ||

                        it.status == "REJECTED"

            }

        adapter.updateList(displayedReservations)

        binding.tvTotalReservations.text =
            "Total Reservations : ${displayedReservations.size}"

    }
    override fun onResume() {

        super.onResume()

        val token =
            SharedPrefManager(requireContext()).getToken()

        if (token != null) {

            viewModel.getReservationsForMyResources(
                "Bearer $token"
            )

        }

    }


}