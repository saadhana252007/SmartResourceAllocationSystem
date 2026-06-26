package com.example.smartresourceallocation.model

data class BookingStatusResponse(

    val status: String,

    val hoursRemaining: Int? = null,

    val requestsReceived: Int? = null,

    val availableUnits: Int? = null,

    val message: String? = null

)