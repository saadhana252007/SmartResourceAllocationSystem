package com.example.smartresourceallocation.model

data class ReservationListResponse(

    val success: Boolean,

    val reservations: List<Reservation>

)