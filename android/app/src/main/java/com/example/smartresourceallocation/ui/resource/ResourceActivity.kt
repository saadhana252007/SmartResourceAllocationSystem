package com.example.smartresourceallocation.ui.resource

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartresourceallocation.adapter.ResourceAdapter
import com.example.smartresourceallocation.databinding.ActivityResourceBinding
import com.example.smartresourceallocation.viewmodel.ResourceViewModel
import android.content.Intent

class ResourceActivity : AppCompatActivity() {

    private lateinit var binding:
            ActivityResourceBinding

    private lateinit var viewModel:
            ResourceViewModel

    private lateinit var adapter:
            ResourceAdapter

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityResourceBinding.inflate(
                layoutInflater
            )

        setContentView(
            binding.root
        )

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
                        this,
                        ResourceDetailsActivity::class.java
                    )

                intent.putExtra(
                    "name",
                    resource.name
                )

                intent.putExtra(
                    "resourceId",
                    resource._id
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
                    "imageUrl",
                    resource.imageUrl
                )

                startActivity(intent)

            }

        binding.rvResources.layoutManager =
            LinearLayoutManager(this)

        binding.rvResources.adapter =
            adapter

        binding.tabMeeting.isSelected = true

        viewModel.getResourcesByCategory(
            "Meeting Room"
        )

        observeData()

        setupCategoryTabs()

    }

    private fun observeData() {

        viewModel.resources.observe(this) {

            adapter.updateList(it)

        }

        viewModel.errorMessage.observe(this) {

            Toast.makeText(
                this,
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