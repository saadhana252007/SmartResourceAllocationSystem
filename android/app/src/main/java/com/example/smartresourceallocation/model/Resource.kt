package com.example.smartresourceallocation.model

data class Resource(

    val _id: String,

    val name: String,

    val category: String,

    val imageUrl: String,

    val description: String,

    val location: String,

    val resourceType: String,

    val capacity: Int,

    val availableUnits: Int,

    val bookingOpenBeforeHours: Int,

    val bookingWindowDurationHours: Int

)