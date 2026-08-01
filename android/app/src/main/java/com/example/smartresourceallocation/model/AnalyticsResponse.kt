package com.example.smartresourceallocation.model

data class AnalyticsResponse(
    val totalResources: Int,
    val totalReservations: Int,
    val pending: Int,
    val approved: Int,
    val alternativeApproved: Int,
    val waitlisted: Int,
    val rejected: Int,
    val cancelled: Int,
    val reservationStatus: ReservationStatus,
    val categoryUsage: List<CategoryUsage>,
    val topResources: List<TopResource>,
    val reservationTrend: List<ReservationTrend>,
    val utilization: List<Utilization>,
    val insights: Insights
)