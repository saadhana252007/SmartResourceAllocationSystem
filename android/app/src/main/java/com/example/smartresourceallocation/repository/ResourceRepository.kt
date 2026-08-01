package com.example.smartresourceallocation.repository

import com.example.smartresourceallocation.api.RetrofitInstance
import com.example.smartresourceallocation.api.RetrofitInstance.api
import com.example.smartresourceallocation.model.CreateReservationRequest
import com.example.smartresourceallocation.model.CreateResourceRequest

class ResourceRepository {

    suspend fun getAllResources() =
        RetrofitInstance.api
            .getAllResources()

    suspend fun getMyResources(

        token: String

    ) = api.getMyResources(token)

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

    suspend fun getAllReservations(

        token: String

    ) =

        api.getAllReservations(token)

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

    suspend fun createResource(

        token: String,

        request: CreateResourceRequest

    ) =

        api.createResource(
            token,
            request
        )

    suspend fun getResourceById(

        token: String,

        resourceId: String

    ) =
        api.getResourceById(
            token,
            resourceId
        )



    suspend fun updateResource(

        token:String,

        id:String,

        request: CreateResourceRequest

    )=api.updateResource(

        token,

        id,

        request

    )

    suspend fun deleteResource(

        token:String,

        id:String

    )=api.deleteResource(

        token,

        id

    )
    suspend fun getAnalytics(

        token: String

    ) =

        api.getAnalytics(token)

    suspend fun getReservationsForMyResources(

        token: String

    ) =

        api.getReservationsForMyResources(
            token
        )

    suspend fun getMyResourcesByCategory(

        token: String,

        category: String

    ) = api.getMyResourcesByCategory(

        token,

        category

    )

}