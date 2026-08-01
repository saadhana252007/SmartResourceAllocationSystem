package com.example.smartresourceallocation.ui.resource

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smartresourceallocation.databinding.ActivityResourceDetailsBinding
import com.example.smartresourceallocation.R
import android.content.Intent
import android.view.View
import com.bumptech.glide.Glide
import com.example.smartresourceallocation.ui.reservation.ReservationActivity

class ResourceDetailsActivity : AppCompatActivity() {

    private lateinit var binding:
            ActivityResourceDetailsBinding

    private var resourceId = ""

    private var imageUrl = ""

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

        resourceId =
            intent.getStringExtra(
                "resourceId"
            ) ?: ""

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

        intent.putExtra(
            "imageUrl",
            imageUrl
        )

        if (resourceId.isBlank()) {

            android.widget.Toast.makeText(
                this,
                "Resource not found",
                android.widget.Toast.LENGTH_SHORT
            ).show()

            return
        }

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

        imageUrl =
            intent.getStringExtra("imageUrl") ?: ""

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

        binding.detailResourceType.text =
            resourceType

        binding.detailBookingOpen.text =
            "$bookingOpenBeforeHours Hours"



        binding.detailBookingWindow.text =
            "$bookingWindowDurationHours Hours"

        val placeholder =
            when (category) {

                "Meeting Room" ->
                    R.drawable.meeting

                "Laboratory Equipment" ->
                    R.drawable.lab

                "Projector" ->
                    R.drawable.projector

                "Sports Facility" ->
                    R.drawable.sports

                "Study Area" ->
                    R.drawable.study

                else ->
                    R.drawable.meeting

            }

        Glide.with(this)
            .load(imageUrl)
            .placeholder(placeholder)
            .error(placeholder)
            .into(binding.imgResource)

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