package com.example.smartresourceallocation.ui.reservation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smartresourceallocation.databinding.ActivityReservationDetailsBinding
import com.example.smartresourceallocation.ui.resource.ResourceDetailsActivity
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.smartresourceallocation.viewmodel.ReservationViewModel
import androidx.appcompat.app.AlertDialog
import com.example.smartresourceallocation.utils.SharedPrefManager
import com.example.smartresourceallocation.model.Reservation
import com.example.smartresourceallocation.utils.DateUtils

class ReservationDetailsActivity :
    AppCompatActivity() {

    private lateinit var binding:
            ActivityReservationDetailsBinding

    private lateinit var viewModel:
            ReservationViewModel

    private var reservationId = ""

    private var currentReservation: Reservation? = null



    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityReservationDetailsBinding.inflate(
                layoutInflater
            )

        setContentView(
            binding.root
        )

        viewModel =
            ViewModelProvider(this)[
                ReservationViewModel::class.java
            ]

        observeData()

        binding.btnCancelReservation
            .setOnClickListener {

                showCancelDialog()

            }
        loadReservationData()
        fetchReservation()

        binding.btnViewResource.setOnClickListener {

            val reservation = currentReservation ?: return@setOnClickListener

            val resource =
                if (
                    reservation.status == "ALTERNATIVE_APPROVED"
                ) {
                    reservation.allocatedResource
                        ?: reservation.requestedResource
                } else {
                    reservation.requestedResource
                } ?: return@setOnClickListener

            val resourceIntent =
                Intent(
                    this,
                    ResourceDetailsActivity::class.java
                )

            resourceIntent.putExtra(
                "name",
                resource.name
            )

            resourceIntent.putExtra(
                "category",
                resource.category
            )

            resourceIntent.putExtra(
                "description",
                resource.description
            )

            resourceIntent.putExtra(
                "location",
                resource.location
            )

            resourceIntent.putExtra(
                "resourceType",
                resource.resourceType
            )

            resourceIntent.putExtra(
                "capacity",
                resource.capacity
            )

            resourceIntent.putExtra(
                "availableUnits",
                resource.availableUnits
            )

            resourceIntent.putExtra(
                "bookingOpenBeforeHours",
                resource.bookingOpenBeforeHours
            )

            resourceIntent.putExtra(
                "bookingWindowDurationHours",
                resource.bookingWindowDurationHours
            )

            resourceIntent.putExtra(
                "imageUrl",
                resource.imageUrl
            )

            resourceIntent.putExtra(
                "fromReservation",
                true
            )

            startActivity(resourceIntent)

        }
        binding.btnEditReservation
            .setOnClickListener {

                openEditReservation()

            }

    }
    override fun onResume() {

        super.onResume()

        fetchReservation()

    }

    private fun loadReservationData() {

        val participantCount =
            intent.getIntExtra(
                "participantCount",
                0
            )

        val quantityRequired =
            intent.getIntExtra(
                "quantityRequired",
                0
            )


        val resourceName =
            intent.getStringExtra(
                "resourceName"
            )
        val allocatedResourceName =
            intent.getStringExtra(
                "allocatedResourceName"
            )

        val date =
            intent.getStringExtra(
                "date"
            )

        val time =
            intent.getStringExtra(
                "time"
            )

        val duration =
            intent.getIntExtra(
                "duration",
                0
            )

        val purpose =
            intent.getStringExtra(
                "purpose"
            )

        reservationId =
            intent.getStringExtra(
                "reservationId"
            ) ?: ""

        if(quantityRequired > 0){

            binding.tvCountLabel.text =
                "Quantity Required"

            binding.tvCount.text =
                quantityRequired.toString()

        }else{

            binding.tvCountLabel.text =
                "Participant Count"

            binding.tvCount.text =
                participantCount.toString()

        }

        val allocation =
            intent.getStringExtra(
                "allocation"
            )

        val status =
            intent.getStringExtra(
                "status"
            )


        binding.btnEditReservation.isEnabled =
            status == "PENDING"

        binding.btnEditReservation.alpha =
            if (status == "PENDING") 1f else 0.5f


        binding.btnCancelReservation.isEnabled =
            status == "PENDING" || status == "APPROVED"

        binding.btnCancelReservation.alpha =
            if (
                status == "PENDING" ||
                status == "APPROVED"
            ) 1f else 0.5f

        binding.tvRequestedResource.text =
            resourceName

        if (
            status == "ALTERNATIVE_APPROVED" &&
            !allocatedResourceName.isNullOrEmpty()
        ) {

            binding.tvAllocatedLabel.visibility =
                View.VISIBLE

            binding.tvAllocatedResource.visibility =
                View.VISIBLE

            binding.tvAllocatedResource.text =
                allocatedResourceName

        } else {

            binding.tvAllocatedLabel.visibility =
                View.GONE

            binding.tvAllocatedResource.visibility =
                View.GONE

        }

        binding.tvDate.text =
            "Date: ${
                date?.let { DateUtils.formatReservationDate(it) }
            }"

        binding.tvTime.text =
            "Time: $time"

        binding.tvDuration.text =
            "Duration: $duration Hours"

        binding.tvPurpose.text =
            "Purpose: $purpose"

        binding.tvAllocation.text =
            "Allocation: $allocation"

        binding.tvStatus.text =
            "Status: $status"



    }
    private fun observeData() {

        viewModel.cancelSuccess.observe(
            this
        ) {

            Toast.makeText(
                this,
                it,
                Toast.LENGTH_SHORT
            ).show()

            finish()

        }

        viewModel.errorMessage.observe(
            this
        ) {

            Toast.makeText(
                this,
                it,
                Toast.LENGTH_SHORT
            ).show()

        }
        viewModel.selectedReservation.observe(
            this
        ){

            currentReservation = it

            binding.tvRequestedResource.text =
                it.requestedResource?.name
                    ?: "Resource Deleted"

            if (
                it.status == "ALTERNATIVE_APPROVED" &&
                it.allocatedResource != null
            ) {

                binding.tvAllocatedLabel.visibility =
                    View.VISIBLE

                binding.tvAllocatedResource.visibility =
                    View.VISIBLE

                binding.tvAllocatedResource.text =
                    it.allocatedResource.name

            } else {

                binding.tvAllocatedLabel.visibility =
                    View.GONE

                binding.tvAllocatedResource.visibility =
                    View.GONE

            }

            binding.tvDate.text =
                "Date: ${DateUtils.formatReservationDate(it.date)}"

            binding.tvTime.text =
                "Time: ${it.startTime}"

            binding.tvDuration.text =
                "Duration: ${it.durationHours} Hours"

            binding.tvPurpose.text =
                "Purpose: ${it.purpose}"

            binding.tvAllocation.text =
                "Allocation: ${it.allocationPreference}"

            binding.tvStatus.text =
                "Status: ${it.status}"

        }

    }
    private fun showCancelDialog() {

        AlertDialog.Builder(this)

            .setTitle(
                "Cancel Reservation"
            )

            .setMessage(
                "Are you sure you want to cancel this reservation?"
            )

            .setPositiveButton(
                "Yes"
            ) { _, _ ->

                cancelReservation()

            }

            .setNegativeButton(
                "No",
                null
            )

            .show()

    }
    private fun cancelReservation() {


        val token =
            SharedPrefManager(this)
                .getToken()


        if (token != null) {

            viewModel.cancelReservation(
                "Bearer $token",
                reservationId
            )

        }

    }
    private fun openEditReservation() {

        val editIntent =
            Intent(
                this,
                ReservationActivity::class.java
            )

        editIntent.putExtra(
            "isEditMode",
            true
        )

        editIntent.putExtra(
            "reservationId",
            reservationId
        )

        editIntent.putExtra(
            "resourceId",
            intent.getStringExtra(
                "resourceId"
            )
        )

        editIntent.putExtra(
            "name",
            intent.getStringExtra(
                "resourceName"
            )
        )

        editIntent.putExtra(
            "category",
            intent.getStringExtra(
                "category"
            )
        )

        editIntent.putExtra(
            "location",
            intent.getStringExtra(
                "location"
            )
        )

        editIntent.putExtra(
            "resourceType",
            intent.getStringExtra(
                "resourceType"
            )
        )

        editIntent.putExtra(
            "date",
            intent.getStringExtra(
                "date"
            )
        )

        editIntent.putExtra(
            "time",
            intent.getStringExtra(
                "time"
            )
        )

        editIntent.putExtra(
            "duration",
            intent.getIntExtra(
                "duration",
                0
            )
        )

        editIntent.putExtra(
            "purpose",
            intent.getStringExtra(
                "purpose"
            )
        )

        editIntent.putExtra(
            "allocation",
            intent.getStringExtra(
                "allocation"
            )
        )

        editIntent.putExtra(
            "participantCount",
            intent.getIntExtra(
                "participantCount",
                0
            )
        )

        editIntent.putExtra(
            "quantityRequired",
            intent.getIntExtra(
                "quantityRequired",
                0
            )
        )
        editIntent.putExtra(
            "imageUrl",
            intent.getStringExtra("imageUrl")
        )

        startActivityForResult(
            editIntent,
            100
        )

    }

    private fun fetchReservation() {

        val token =
            SharedPrefManager(this)
                .getToken()

        if(token != null){

            viewModel.getReservationById(

                "Bearer $token",

                reservationId

            )

        }

    }
    override fun onActivityResult(

        requestCode: Int,

        resultCode: Int,

        data: Intent?

    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if(
            requestCode == 100 &&
            resultCode == RESULT_OK
        ){

            finish()

        }

    }

}