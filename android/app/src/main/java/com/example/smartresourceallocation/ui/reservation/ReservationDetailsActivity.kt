package com.example.smartresourceallocation.ui.reservation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smartresourceallocation.databinding.ActivityReservationDetailsBinding
import com.example.smartresourceallocation.ui.resource.ResourceDetailsActivity
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.smartresourceallocation.viewmodel.ReservationViewModel
import androidx.appcompat.app.AlertDialog
import com.example.smartresourceallocation.utils.SharedPrefManager

class ReservationDetailsActivity :
    AppCompatActivity() {

    private lateinit var binding:
            ActivityReservationDetailsBinding

    private lateinit var viewModel:
            ReservationViewModel

    private var reservationId = ""



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

        binding.btnViewResource.setOnClickListener {

            val resourceIntent =
                Intent(
                    this,
                    ResourceDetailsActivity::class.java
                )

            resourceIntent.putExtra(
                "name",
                intent.getStringExtra(
                    "resourceName"
                )
            )

            resourceIntent.putExtra(
                "category",
                intent.getStringExtra(
                    "category"
                )
            )

            resourceIntent.putExtra(
                "description",
                intent.getStringExtra(
                    "description"
                )
            )

            resourceIntent.putExtra(
                "location",
                intent.getStringExtra(
                    "location"
                )
            )

            resourceIntent.putExtra(
                "resourceType",
                intent.getStringExtra(
                    "resourceType"
                )
            )

            resourceIntent.putExtra(
                "capacity",
                intent.getIntExtra(
                    "capacity",
                    0
                )
            )

            resourceIntent.putExtra(
                "availableUnits",
                intent.getIntExtra(
                    "availableUnits",
                    0
                )
            )

            resourceIntent.putExtra(
                "bookingOpenBeforeHours",
                intent.getIntExtra(
                    "bookingOpenBeforeHours",
                    0
                )
            )

            resourceIntent.putExtra(
                "bookingWindowDurationHours",
                intent.getIntExtra(
                    "bookingWindowDurationHours",
                    0
                )
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

        loadReservationData()

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

        if (status != "PENDING") {

            binding.btnEditReservation.isEnabled =
                false

            binding.btnCancelReservation.isEnabled =
                false

            binding.btnEditReservation.alpha =
                0.5f

            binding.btnCancelReservation.alpha =
                0.5f

        }

        binding.tvResourceName.text =
            resourceName

        binding.tvDate.text =
            "Date: ${
                date?.substring(0,10)
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

            binding.tvResourceName.text =
                it.requestedResource.name

            binding.tvDate.text =
                "Date: ${
                    it.date.substring(0,10)
                }"

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