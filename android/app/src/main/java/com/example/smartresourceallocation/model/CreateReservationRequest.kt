package com.example.smartresourceallocation.model

data class CreateReservationRequest(

    val requestedResource: String,

    val date: String,

    val startTime: String,

    val durationHours: Int,

    val allocationPreference: String,

    val participantCount: Int,

    val quantityRequired: Int,

    val purpose: String

)