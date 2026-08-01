package com.example.smartresourceallocation.ui.admin.fragments.reservations

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.smartresourceallocation.R
import com.example.smartresourceallocation.databinding.ActivityAdminReservationDetailsBinding
import com.example.smartresourceallocation.utils.DateUtils
import com.example.smartresourceallocation.utils.SharedPrefManager
import com.example.smartresourceallocation.viewmodel.ReservationViewModel

class AdminReservationDetailsActivity :
    AppCompatActivity() {

    private lateinit var binding:
            ActivityAdminReservationDetailsBinding

    private lateinit var viewModel:
            ReservationViewModel

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        binding =
            ActivityAdminReservationDetailsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        viewModel =
            ViewModelProvider(this)[ReservationViewModel::class.java]

        val reservationId =
            intent.getStringExtra("RESERVATION_ID")

        if (reservationId.isNullOrEmpty()) {

            Toast.makeText(
                this,
                "Reservation not found",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        val token = SharedPrefManager(this).getToken()

        if (token == null) {

            Toast.makeText(
                this,
                "Please login again",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        viewModel.getReservationById(
            "Bearer $token",
            reservationId
        )

        viewModel.getReservationById(
            token,
            reservationId
        )

        observeData()

    }

    private fun observeData() {

        viewModel.selectedReservation.observe(this) { reservation ->

            val requestedResource =
                reservation.requestedResource

            val displayResource =
                if (
                    reservation.status == "ALTERNATIVE_APPROVED"
                ) {
                    reservation.allocatedResource
                        ?: requestedResource
                } else {
                    requestedResource
                }

            binding.RequestedResource.text =
                requestedResource?.name
                    ?: "Resource Deleted"

            if (
                reservation.status == "ALTERNATIVE_APPROVED" &&
                reservation.allocatedResource != null
            ) {

                binding.AllocatedLabel.visibility =
                    View.VISIBLE

                binding.AllocatedResource.visibility =
                    View.VISIBLE

                binding.AllocatedResource.text =
                    reservation.allocatedResource.name

            } else {

                binding.AllocatedLabel.visibility =
                    View.GONE

                binding.AllocatedResource.visibility =
                    View.GONE

            }

            binding.Category.text =
                displayResource?.category ?: reservation.resourceCategory

            binding.Location.text =
                displayResource?.location ?: "N/A"

            binding.Purpose.text =
                reservation.purpose

            binding.detailStudentName.text =
                reservation.user.name

            binding.detailEmail.text =
                reservation.user.email

            binding.detailDate.text =
                DateUtils.formatReservationDate(
                    reservation.date
                )

            binding.detailStartTime.text =
                reservation.startTime

            binding.detailDuration.text =
                "${reservation.durationHours} Hour${
                    if (reservation.durationHours > 1) "s" else ""
                }"

            binding.detailPreference.text =
                reservation.allocationPreference

            binding.detailStatus.text =
                reservation.status

            if (displayResource != null) {

                if (displayResource.resourceType == "CAPACITY_BASED") {

                    binding.detailParticipants.visibility =
                        View.VISIBLE

                    binding.detailQuantity.visibility =
                        View.GONE

                    binding.detailParticipants.text =
                        reservation.participantCount.toString()

                } else {

                    binding.detailParticipants.visibility =
                        View.GONE

                    binding.detailQuantity.visibility =
                        View.VISIBLE

                    binding.detailQuantity.text =
                        reservation.quantityRequired.toString()

                }

                val placeholder =
                    when (displayResource.category) {

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
                    .load(displayResource.imageUrl)
                    .placeholder(placeholder)
                    .error(placeholder)
                    .into(binding.imgResource)

            } else {

                binding.detailParticipants.visibility =
                    View.GONE

                binding.detailQuantity.visibility =
                    View.GONE

                binding.imgResource.setImageResource(
                    R.drawable.meeting
                )
            }

        }

        viewModel.errorMessage.observe(this){

            Toast.makeText(
                this,
                it,
                Toast.LENGTH_SHORT
            ).show()

        }

    }

}