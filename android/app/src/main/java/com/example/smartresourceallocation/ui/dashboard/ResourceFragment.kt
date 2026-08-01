package com.example.smartresourceallocation.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartresourceallocation.adapter.ResourceAdapter
import com.example.smartresourceallocation.databinding.FragmentResourceBinding
import com.example.smartresourceallocation.ui.resource.ResourceDetailsActivity
import com.example.smartresourceallocation.viewmodel.ResourceViewModel
class ResourceFragment : Fragment() {

    private var _binding:
            FragmentResourceBinding? = null

    private val binding
        get() = _binding!!

    private lateinit var viewModel:
            ResourceViewModel

    private lateinit var adapter:
            ResourceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentResourceBinding.inflate(
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

        setupUI()

    }

    private fun setupUI() {

        viewModel =
            ViewModelProvider(this)[
                ResourceViewModel::class.java
            ]

        adapter =
            ResourceAdapter(
                emptyList()
            ) { resource ->

                val intent =
                    Intent(
                        requireContext(),
                        ResourceDetailsActivity::class.java
                    )

                intent.putExtra(
                    "name",
                    resource.name
                )

                intent.putExtra(
                    "category",
                    resource.category
                )

                intent.putExtra(
                    "description",
                    resource.description
                )

                intent.putExtra(
                    "location",
                    resource.location
                )

                intent.putExtra(
                    "resourceType",
                    resource.resourceType
                )

                intent.putExtra(
                    "capacity",
                    resource.capacity
                )

                intent.putExtra(
                    "availableUnits",
                    resource.availableUnits
                )

                intent.putExtra(
                    "bookingOpenBeforeHours",
                    resource.bookingOpenBeforeHours
                )

                intent.putExtra(
                    "bookingWindowDurationHours",
                    resource.bookingWindowDurationHours
                )
                intent.putExtra(
                    "resourceId",
                    resource._id
                )
                intent.putExtra(
                    "imageUrl",
                    resource.imageUrl
                )

                startActivity(intent)

            }

        binding.rvResources.layoutManager =
            LinearLayoutManager(
                requireContext()
            )

        binding.rvResources.adapter =
            adapter

        observeData()

        setupCategoryTabs()

        binding.tabMeeting.isSelected =
            true

        viewModel.getResourcesByCategory(
            "Meeting Room"
        )

    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null

    }

    private fun observeData() {

        viewModel.resources.observe(
            viewLifecycleOwner
        ) {

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

    private fun resetTabs() {
        binding.tabMeeting.isSelected = false
        binding.tabLab.isSelected = false
        binding.tabProjector.isSelected = false
        binding.tabSports.isSelected = false
        binding.tabStudy.isSelected = false
    }

    private fun setupCategoryTabs() {

        binding.tabMeeting.setOnClickListener {

            resetTabs()

            binding.tabMeeting.isSelected = true


            viewModel.getResourcesByCategory(
                "Meeting Room"
            )

        }

        binding.tabLab.setOnClickListener {
            resetTabs()

            binding.tabLab.isSelected = true



            viewModel.getResourcesByCategory(
                "Laboratory Equipment"
            )

        }

        binding.tabProjector.setOnClickListener {

            resetTabs()

            binding.tabProjector.isSelected = true


            viewModel.getResourcesByCategory(
                "Projector"
            )

        }

        binding.tabSports.setOnClickListener {

            resetTabs()

            binding.tabSports.isSelected = true


            viewModel.getResourcesByCategory(
                "Sports Facility"
            )

        }

        binding.tabStudy.setOnClickListener {

            resetTabs()

            binding.tabStudy.isSelected = true


            viewModel.getResourcesByCategory(
                "Study Area"
            )

        }

    }


}