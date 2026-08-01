package com.example.smartresourceallocation.model

data class ReservationStatus(

    val PENDING: Int,

    val APPROVED: Int,

    val ALTERNATIVE_APPROVED: Int,

    val WAITLISTED: Int,

    val REJECTED: Int,

    val CANCELLED: Int

)