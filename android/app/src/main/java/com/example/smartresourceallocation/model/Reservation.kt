package com.example.smartresourceallocation.model

data class Reservation(

    val _id: String,

    val date: String,

    val startTime: String,

    val durationHours: Int,

    val purpose: String,

    val status: String,

    val allocationPreference: String,

    val participantCount: Int,

    val quantityRequired: Int,

    val user: User,

    val requestedResource: Resource?,

    val resourceCategory: String,

    val allocatedResource: Resource?,

    val allocationType: String?,

    val alternativeStartTime: String?

)