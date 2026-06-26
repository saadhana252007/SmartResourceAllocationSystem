package com.example.smartresourceallocation.ui.resource

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smartresourceallocation.databinding.ActivityResourceDetailsBinding
import com.example.smartresourceallocation.R
import android.content.Intent
import android.view.View
import com.example.smartresourceallocation.ui.reservation.ReservationActivity

class ResourceDetailsActivity : AppCompatActivity() {

    private lateinit var binding:
            ActivityResourceDetailsBinding

    private var resourceId = ""

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityResourceDetailsBinding.inflate(
                layoutInflater
            )

        setContentView(
            binding.root
        )

        loadResourceData()

        val fromReservation =
            intent.getBooleanExtra(
                "fromReservation",
                false
            )

        if (fromReservation) {

            binding.btnReserve.visibility =
                View.GONE

        }
        binding.btnReserve.setOnClickListener {

            openReservationScreen()

        }

        resourceId =
            intent.getStringExtra(
                "resourceId"
            ) ?: ""


    }

    private fun openReservationScreen() {

        val intent = Intent(
            this,
            ReservationActivity::class.java
        )

        intent.putExtra(
            "name",
            binding.tvName.text.toString()
        )

        intent.putExtra(
            "category",
            binding.tvCategory.text.toString()
        )

        intent.putExtra(
            "location",
            binding.tvLocation.text.toString()
        )

        intent.putExtra(
            "resourceType",
            binding.detailResourceType.text.toString()
        )

        intent.putExtra(
            "resourceId",
            resourceId
        )

        startActivity(intent)

    }

    private fun loadResourceData() {

        val name =
            intent.getStringExtra("name")

        val category =
            intent.getStringExtra("category")

        val description =
            intent.getStringExtra("description")

        val location =
            intent.getStringExtra("location")

        val resourceType =
            intent.getStringExtra("resourceType")

        val capacity =
            intent.getIntExtra(
                "capacity",
                0
            )

        val availableUnits =
            intent.getIntExtra(
                "availableUnits",
                0
            )

        val bookingOpenBeforeHours =
            intent.getIntExtra(
                "bookingOpenBeforeHours",
                0
            )

        val bookingWindowDurationHours =
            intent.getIntExtra(
                "bookingWindowDurationHours",
                0
            )

        binding.tvName.text =
            name

        binding.tvCategory.text =
            category

        binding.tvLocation.text =
            location

        binding.tvDescription.text =
            description

        binding.tvCategory.text =
            category

        binding.tvLocation.text =
            location

        binding.detailResourceType.text =
            resourceType

        binding.detailBookingOpen.text =
            "$bookingOpenBeforeHours Hours"



        binding.detailBookingWindow.text =
            "$bookingWindowDurationHours Hours"

        when (category) {

            "Meeting Room" -> {
                binding.imgResource.setImageResource(
                    R.drawable.meeting
                )
            }

            "Laboratory Equipment" -> {
                binding.imgResource.setImageResource(
                    R.drawable.lab
                )
            }

            "Projector" -> {
                binding.imgResource.setImageResource(
                    R.drawable.projector
                )
            }

            "Sports Facility" -> {
                binding.imgResource.setImageResource(
                    R.drawable.sports
                )
            }

            "Study Area" -> {
                binding.imgResource.setImageResource(
                    R.drawable.study
                )
            }



        }

        if (
            resourceType ==
            "CAPACITY_BASED"
        ) {

            binding.tvCapacityLabel.text =
                "Capacity"

            binding.detailCapacity.text =
                capacity.toString()

        } else {

            binding.tvCapacityLabel.text =
                "Available Units"

            binding.detailCapacity.text =
                availableUnits.toString()

        }

    }


}