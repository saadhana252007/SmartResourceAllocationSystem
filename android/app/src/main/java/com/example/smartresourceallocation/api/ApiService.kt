package com.example.smartresourceallocation.api

import com.example.smartresourceallocation.model.LoginRequest
import com.example.smartresourceallocation.model.LoginResponse
import com.example.smartresourceallocation.model.RegisterRequest
import com.example.smartresourceallocation.model.RegisterResponse
import com.example.smartresourceallocation.model.Resource
import com.example.smartresourceallocation.model.Reservation
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Path
import com.example.smartresourceallocation.model.CreateReservationRequest
import com.example.smartresourceallocation.model.CreateReservationResponse
import com.example.smartresourceallocation.model.BookingStatusResponse
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.PUT


interface ApiService {

    @POST("api/auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    ): Response<RegisterResponse>

    @GET("api/resources")
    suspend fun getAllResources():
            Response<List<Resource>>

    @GET("api/resources/category/{category}")
    suspend fun getResourcesByCategory(

        @Path("category")
        category: String

    ): Response<List<Resource>>

    @POST("api/reservations")
    suspend fun createReservation(

        @Header("Authorization")
        token: String,

        @Body request: CreateReservationRequest

    ): Response<CreateReservationResponse>

    @GET("api/resources/booking-status")
    suspend fun getBookingStatus(

        @Query("resourceId")
        resourceId: String,

        @Query("date")
        date: String

    ): Response<BookingStatusResponse>

    @GET("api/reservations/my-reservations")
    suspend fun getMyReservations(

        @Header("Authorization")
        token: String

    ): Response<List<Reservation>>

    @PUT("api/reservations/cancel/{id}")
    suspend fun cancelReservation(

        @Header("Authorization")
        token: String,

        @Path("id")
        reservationId: String

    ): Response<Map<String,String>>

    @PUT("api/reservations/{id}")
    suspend fun updateReservation(

        @Header("Authorization")
        token: String,

        @Path("id")
        reservationId: String,

        @Body
        request: CreateReservationRequest

    ): Response<CreateReservationResponse>

    @GET("api/reservations/{id}")
    suspend fun getReservationById(

        @Header("Authorization")
        token: String,

        @Path("id")
        reservationId: String

    ): Response<Reservation>

}
