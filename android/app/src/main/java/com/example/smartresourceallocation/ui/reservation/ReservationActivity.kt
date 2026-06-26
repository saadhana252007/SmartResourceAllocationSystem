package com.example.smartresourceallocation.ui.reservation

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.ArrayAdapter
import com.example.smartresourceallocation.R
import com.example.smartresourceallocation.databinding.ActivityReservationBinding
import java.util.Calendar
import androidx.lifecycle.ViewModelProvider
import com.example.smartresourceallocation.viewmodel.ReservationViewModel
import com.example.smartresourceallocation.model.CreateReservationRequest
import com.example.smartresourceallocation.utils.SharedPrefManager
import android.widget.Toast
import com.example.smartresourceallocation.ui.dashboard.UserDashboardActivity
import com.example.smartresourceallocation.viewmodel.ResourceViewModel

class ReservationActivity : AppCompatActivity() {

    private lateinit var binding:
            ActivityReservationBinding

    private var resourceType = ""

    private var resourceId = ""

    private var isEditMode = false

    private var reservationId = ""

    private lateinit var viewModel:
            ReservationViewModel

    private lateinit var sharedPrefManager:
            SharedPrefManager

    private lateinit var resourceViewModel:
            ResourceViewModel

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityReservationBinding.inflate(
                layoutInflater
            )

        setContentView(
            binding.root
        )
        viewModel =
            ViewModelProvider(this)[
                ReservationViewModel::class.java
            ]

        sharedPrefManager =
            SharedPrefManager(this)

        observeReservationResponse()

        setupCreateReservation()

        loadResourceData()

        setupSpinners()

        checkEditMode()

        setupDatePicker()

        setupTimePicker()

        resourceViewModel =
            ViewModelProvider(this)[
                ResourceViewModel::class.java
            ]

        observeBookingStatus()

    }

    private fun loadResourceData() {

        val name =
            intent.getStringExtra("name")

        val category =
            intent.getStringExtra("category")

        val location =
            intent.getStringExtra("location")

        resourceId =
            intent.getStringExtra(
                "resourceId"
            ) ?: ""



        resourceType =
            intent.getStringExtra(
                "resourceType"
            ) ?: ""

        binding.tvResourceName.text =
            name

        binding.tvResourceCategory.text =
            category

        binding.tvResourceLocation.text =
            location

        when (category) {

            "Meeting Room" ->
                binding.imgResource.setImageResource(
                    R.drawable.meeting
                )

            "Laboratory Equipment" ->
                binding.imgResource.setImageResource(
                    R.drawable.lab
                )

            "Projector" ->
                binding.imgResource.setImageResource(
                    R.drawable.projector
                )

            "Sports Facility" ->
                binding.imgResource.setImageResource(
                    R.drawable.sports
                )

            "Study Area" ->
                binding.imgResource.setImageResource(
                    R.drawable.study
                )

        }

        if (
            resourceType ==
            "CAPACITY_BASED"
        ) {

            binding.layoutParticipantCount.visibility =
                View.VISIBLE

            binding.layoutQuantityRequired.visibility =
                View.GONE

        } else {

            binding.layoutParticipantCount.visibility =
                View.GONE

            binding.layoutQuantityRequired.visibility =
                View.VISIBLE

        }

    }

    private fun setupSpinners() {

        val durationList = listOf(
            "Select Duration",
            "1 Hour",
            "2 Hours",
            "3 Hours",
            "4 Hours",
            "5 Hours",
            "6 Hours",
            "8 Hours",
            "10 Hours",
            "12 Hours",
            "24 Hours"
        )

        val purposeList =
            listOf(
                "Select Purpose",
                "Academic",
                "Research",
                "Project Work",
                "Club Activity",
                "Personal"
            )

        val allocationList =
            listOf(
                "Select Allocation Preference",
                "SPECIFIC_RESOURCE",
                "ALTERNATE_RESOURCE",
                "ALTERNATE_TIME",
                "ALTERNATE_RESOURCE_AND_TIME"
            )

        val durationAdapter =
            ArrayAdapter(
                this,
                R.layout.spinner_item,
                durationList
            )

        durationAdapter.setDropDownViewResource(
            R.layout.spinner_dropdown_item
        )

        binding.spinnerDuration.adapter =
            durationAdapter


        val purposeAdapter =
            ArrayAdapter(
                this,
                R.layout.spinner_item,
                purposeList
            )

        purposeAdapter.setDropDownViewResource(
            R.layout.spinner_dropdown_item
        )

        binding.spinnerPurpose.adapter =
            purposeAdapter


        val allocationAdapter =
            ArrayAdapter(
                this,
                R.layout.spinner_item,
                allocationList
            )

        allocationAdapter.setDropDownViewResource(
            R.layout.spinner_dropdown_item
        )

        binding.spinnerAllocationPreference.adapter =
            allocationAdapter

    }

    private fun setupDatePicker() {

        binding.tvSelectedDate
            .setOnClickListener {

                val calendar =
                    Calendar.getInstance()

                DatePickerDialog(
                    this,
                    { _, year, month, day ->

                        binding.tvSelectedDate.text =
                            String.format(
                                "%04d-%02d-%02d",
                                year,
                                month + 1,
                                day
                            )
                        resourceViewModel.getBookingStatus(
                            resourceId,
                            binding.tvSelectedDate.text.toString()
                        )

                    },
                    calendar.get(
                        Calendar.YEAR
                    ),
                    calendar.get(
                        Calendar.MONTH
                    ),
                    calendar.get(
                        Calendar.DAY_OF_MONTH
                    )
                ).show()

            }

    }

    private fun setupTimePicker() {

        binding.tvSelectedTime
            .setOnClickListener {

                val calendar =
                    Calendar.getInstance()

                TimePickerDialog(
                    this,
                    { _, hour, minute ->

                        binding.tvSelectedTime.text =
                            String.format(
                                "%02d:%02d",
                                hour,
                                minute
                            )

                    },
                    calendar.get(
                        Calendar.HOUR_OF_DAY
                    ),
                    calendar.get(
                        Calendar.MINUTE
                    ),
                    true
                ).show()

            }

    }
    private fun observeReservationResponse() {

        viewModel.reservationResponse
            .observe(this) {

                Toast.makeText(
                    this,
                    "Reservation Created Successfully",
                    Toast.LENGTH_LONG
                ).show()

                finish()

            }

        viewModel.errorMessage
            .observe(this) {

                Toast.makeText(
                    this,
                    it,
                    Toast.LENGTH_LONG
                ).show()

            }

        viewModel.updateSuccess.observe(
            this
        ){

            Toast.makeText(
                this,
                it,
                Toast.LENGTH_LONG
            ).show()

            val dashboardIntent =
                Intent(
                    this,
                    UserDashboardActivity::class.java
                )

            dashboardIntent.putExtra(
                "openReservationTab",
                true
            )

            dashboardIntent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_NEW_TASK

            startActivity(
                dashboardIntent
            )

            finish()

        }

    }
    private fun setupCreateReservation() {

        binding.btnCreateReservation
            .setOnClickListener {

                val token =
                    sharedPrefManager
                        .getToken()

                if (token == null) {

                    Toast.makeText(
                        this,
                        "Token Missing",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                val duration =
                    binding.spinnerDuration
                        .selectedItem
                        .toString()

                val purpose =
                    binding.spinnerPurpose
                        .selectedItem
                        .toString()

                val allocation =
                    binding.spinnerAllocationPreference
                        .selectedItem
                        .toString()

                if (duration == "Select Duration") {

                    Toast.makeText(
                        this,
                        "Please select duration",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                if (purpose == "Select Purpose") {

                    Toast.makeText(
                        this,
                        "Please select purpose",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                if (allocation == "Select Allocation Preference") {

                    Toast.makeText(
                        this,
                        "Please select allocation preference",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                val participantCount =
                    if (
                        resourceType ==
                        "CAPACITY_BASED"
                    ) {
                        binding
                            .etParticipantCount
                            .text
                            .toString()
                            .ifEmpty { "1" }
                            .toInt()
                    } else {
                        1
                    }

                val quantityRequired =
                    if (
                        resourceType ==
                        "QUANTITY_BASED"
                    ) {
                        binding
                            .etQuantityRequired
                            .text
                            .toString()
                            .ifEmpty { "1" }
                            .toInt()
                    } else {
                        0
                    }
                Toast.makeText(
                    this,
                    "Resource ID = $resourceId",
                    Toast.LENGTH_LONG
                ).show()

                val request =
                    CreateReservationRequest(

                        requestedResource =
                            resourceId,

                        date =
                            binding.tvSelectedDate
                                .text
                                .toString(),

                        startTime =
                            binding.tvSelectedTime
                                .text
                                .toString(),

                        durationHours =
                            duration
                                .substringBefore(" ")
                                .toInt(),

                        allocationPreference =
                            allocation,

                        participantCount =
                            participantCount,

                        quantityRequired =
                            quantityRequired,

                        purpose =
                            purpose

                    )

                if(isEditMode){

                    viewModel.updateReservation(

                        token =
                            "Bearer $token",

                        reservationId =
                            reservationId,

                        request =
                            request

                    )

                }else{

                    viewModel.createReservation(

                        token =
                            "Bearer $token",

                        request =
                            request

                    )

                }

            }

    }

    private fun observeBookingStatus() {

        resourceViewModel.bookingStatus
            .observe(this) { response ->

                binding.cardBookingStatus.visibility =
                    View.VISIBLE

                when (
                    response.status
                ) {

                    "OPEN" -> {

                        binding.tvBookingStatus.text =
                            "🟢 OPEN"

                        binding.tvBookingMessage.text =
                            response.message

                        binding.tvRequestsReceived.text =
                            "Requests Received : ${response.requestsReceived}"

                        binding.tvAvailableUnits.text =
                            "Available Units : ${response.availableUnits}"

                        binding.btnCreateReservation.isEnabled =
                            true

                    }

                    "OPENS_SOON" -> {

                        binding.tvBookingStatus.text =
                            "🟡 OPENS SOON"

                        binding.tvBookingMessage.text =
                            "Opens in ${response.hoursRemaining} hours"

                        binding.tvRequestsReceived.text =
                            ""

                        binding.tvAvailableUnits.text =
                            ""

                        binding.btnCreateReservation.isEnabled =
                            false

                    }

                    "CLOSED" -> {

                        binding.tvBookingStatus.text =
                            "🔴 CLOSED"

                        binding.tvBookingMessage.text =
                            response.message

                        binding.tvRequestsReceived.text =
                            ""

                        binding.tvAvailableUnits.text =
                            ""

                        binding.btnCreateReservation.isEnabled =
                            false

                    }

                }

            }

    }
    private fun checkEditMode() {

        isEditMode =
            intent.getBooleanExtra(
                "isEditMode",
                false
            )

        if(isEditMode){

            reservationId =
                intent.getStringExtra(
                    "reservationId"
                ) ?: ""

            binding.btnCreateReservation.text =
                "UPDATE RESERVATION"

            prefillReservationData()

        }

    }

    private fun prefillReservationData() {

        binding.tvSelectedDate.text =
            intent.getStringExtra("date")
                ?.substring(0, 10)

        binding.tvSelectedTime.text =
            intent.getStringExtra("time")

        val duration =
            intent.getIntExtra(
                "duration",
                1
            )

        binding.spinnerDuration.setSelection(
            duration
        )

        val purpose =
            intent.getStringExtra(
                "purpose"
            )

        val purposePosition =
            when(purpose){

                "Academic" -> 1

                "Research" -> 2

                "Project Work" -> 3

                "Club Activity" -> 4

                "Personal" -> 5

                else -> 0

            }

        binding.spinnerPurpose.setSelection(
            purposePosition
        )

        val allocation =
            intent.getStringExtra(
                "allocation"
            )

        val allocationPosition =
            when(allocation){

                "SPECIFIC_RESOURCE" -> 1

                "ALTERNATE_RESOURCE" -> 2

                "ALTERNATE_TIME" -> 3

                "ALTERNATE_RESOURCE_AND_TIME" -> 4

                else -> 0

            }

        binding.spinnerAllocationPreference
            .setSelection(
                allocationPosition
            )

        if(resourceType == "CAPACITY_BASED"){

            binding.etParticipantCount.setText(

                intent.getIntExtra(
                    "participantCount",
                    1
                ).toString()

            )

        }else{

            binding.etQuantityRequired.setText(

                intent.getIntExtra(
                    "quantityRequired",
                    1
                ).toString()

            )

        }

    }

}