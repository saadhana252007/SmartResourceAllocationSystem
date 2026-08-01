package com.example.smartresourceallocation.model

data class CreateResourceRequest(

    val name:String,

    val category:String,

    val imageUrl:String,

    val description:String,

    val location:String,

    val resourceType:String,

    val capacity:Int,

    val availableUnits:Int,

    val bookingOpenBeforeHours:Int,

    val bookingWindowDurationHours:Int,

    val workingStartTime: String,

    val workingEndTime: String

)