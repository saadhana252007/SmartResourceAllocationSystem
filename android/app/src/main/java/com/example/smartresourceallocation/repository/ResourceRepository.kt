package com.example.smartresourceallocation.repository

import com.example.smartresourceallocation.api.RetrofitInstance
import com.example.smartresourceallocation.api.RetrofitInstance.api
import com.example.smartresourceallocation.model.CreateReservationRequest

class ResourceRepository {

    suspend fun getAllResources() =
        RetrofitInstance.api
            .getAllResources()

    suspend fun getResourcesByCategory(
        category: String
    ) =
        RetrofitInstance.api
            .getResourcesByCategory(
                category
            )

    suspend fun createReservation(
        token: String,
        request: CreateReservationRequest
    ) =
        api.createReservation(
            token,
            request
        )

    suspend fun getBookingStatus(
        resourceId: String,
        date: String
    ) =
        api.getBookingStatus(
            resourceId,
            date
        )

    suspend fun getMyReservations(
        token: String
    ) =
        api.getMyReservations(
            token
        )

    suspend fun cancelReservation(
        token: String,
        reservationId: String
    ) =
        api.cancelReservation(
            token,
            reservationId
        )

    suspend fun updateReservation(

        token: String,

        reservationId: String,

        request: CreateReservationRequest

    ) =
        api.updateReservation(
            token,
            reservationId,
            request
        )

    suspend fun getReservationById(

        token: String,

        reservationId: String

    ) =
        api.getReservationById(
            token,
            reservationId
        )

}