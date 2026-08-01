package com.example.smartresourceallocation.ui.admin.fragments.resources

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.smartresourceallocation.R
import com.example.smartresourceallocation.databinding.ActivityAdminResourceDetailsBinding
import com.example.smartresourceallocation.utils.DateUtils
import com.example.smartresourceallocation.viewmodel.ResourceViewModel
import kotlin.jvm.java
import com.example.smartresourceallocation.ui.admin.resources.AddEditResourceActivity
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.example.smartresourceallocation.utils.SharedPrefManager

class AdminResourceDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminResourceDetailsBinding

    private lateinit var viewModel: ResourceViewModel

    private var resourceId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityAdminResourceDetailsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        viewModel =
            ViewModelProvider(this)[ResourceViewModel::class.java]

        resourceId =
            intent.getStringExtra("RESOURCE_ID") ?: ""

        val token =
            SharedPrefManager(this)
                .getToken()

        if (
            token != null &&
            resourceId.isNotEmpty()
        ) {

            viewModel.getResourceById(
                "Bearer $token",
                resourceId
            )

        }

        observeData()

        binding.btnEdit.setOnClickListener {

            val intent = Intent(
                this,
                AddEditResourceActivity::class.java
            )

            intent.putExtra(
                "MODE",
                "EDIT"
            )

            intent.putExtra(
                "RESOURCE_ID",
                resourceId
            )

            startActivity(intent)

        }

        binding.btnDelete.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Delete Resource")
                .setMessage("Are you sure you want to delete this resource?")
                .setPositiveButton("Delete") { _, _ ->

                    val token =
                        SharedPrefManager(this).getToken()

                    if (token == null) {

                        Toast.makeText(
                            this,
                            "Please login again",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@setPositiveButton
                    }

                    viewModel.deleteResource(
                        "Bearer $token",
                        resourceId
                    )

                }
                .setNegativeButton("Cancel", null)
                .show()

        }

    }
    override fun onResume() {
        super.onResume()

        val token =
            SharedPrefManager(this)
                .getToken()

        if (
            token != null &&
            resourceId.isNotEmpty()
        ) {

            viewModel.getResourceById(
                "Bearer $token",
                resourceId
            )

        }
    }

    private fun observeData() {

        viewModel.selectedResource.observe(this) { resource ->

            binding.tvName.text =
                resource.name

            binding.tvCategory.text =
                resource.category

            binding.tvLocation.text =
                resource.location

            binding.tvDescription.text =
                resource.description

            binding.detailCategory.text =
                resource.category

            val defaultImage = when (resource.category) {

                "Meeting Room" -> R.drawable.meeting

                "Laboratory Equipment" -> R.drawable.lab

                "Projector" -> R.drawable.projector

                "Sports Facility" -> R.drawable.sports

                "Study Area" -> R.drawable.study

                else -> R.drawable.meeting
            }

            Glide.with(this)
                .load(resource.imageUrl)
                .placeholder(defaultImage)
                .error(defaultImage)
                .fitCenter()
                .into(binding.imgResource)

            if (resource.resourceType == "CAPACITY_BASED") {

                binding.tvCapacityLabel.text =
                    "Capacity"

                binding.detailCapacity.text =
                    "${resource.capacity} Persons"

            } else {

                binding.tvCapacityLabel.text =
                    "Available Units"

                binding.detailCapacity.text =
                    "${resource.availableUnits}"

            }

            binding.detailBookingOpen.text =
                "${resource.bookingOpenBeforeHours} Hours Before"

            binding.detailBookingWindow.text =
                "${resource.bookingWindowDurationHours} Hours"

            binding.detailCreatedAt.text =
                DateUtils.format(resource.createdAt)

            binding.detailUpdatedAt.text =
                DateUtils.format(resource.updatedAt)

        }

        viewModel.errorMessage.observe(this) {

            Toast.makeText(
                this,
                it,
                Toast.LENGTH_SHORT
            ).show()

        }
        viewModel.deleteSuccess.observe(this) {

            if (it) {

                Toast.makeText(
                    this,
                    "Resource Deleted Successfully",
                    Toast.LENGTH_SHORT
                ).show()

                finish()

            }

        }

    }

}