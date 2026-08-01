package com.example.smartresourceallocation.model

data class Insights(

    val mostReservedResource: String,

    val highestUtilization: Utilization?,

    val leastUtilization: Utilization?,

    val peakReservationDay: String?,

    val averageReservationDuration: Double

)