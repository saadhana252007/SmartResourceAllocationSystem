package com.example.smartresourceallocation.model

data class AdminSummaryResponse(

    val success: Boolean,

    val resourcesManaged: Int,

    val reservationsProcessed: Int,

    val pendingRequests: Int,

    val systemUtilization: Int

)