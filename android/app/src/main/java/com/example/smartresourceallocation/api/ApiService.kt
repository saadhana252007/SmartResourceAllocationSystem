package com.example.smartresourceallocation.api

import com.example.smartresourceallocation.model.AdminProfileResponse
import com.example.smartresourceallocation.model.AdminSummaryResponse
import com.example.smartresourceallocation.model.AnalyticsResponse
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
import com.example.smartresourceallocation.model.ChangePasswordRequest
import com.example.smartresourceallocation.model.CreateResourceRequest
import com.example.smartresourceallocation.model.ForgotPasswordRequest
import com.example.smartresourceallocation.model.ReservationListResponse
import com.example.smartresourceallocation.model.ReservationResponse
import com.example.smartresourceallocation.model.ResetPasswordRequest
import com.example.smartresourceallocation.model.ResourceListResponse
import com.example.smartresourceallocation.model.ResourceResponse
import com.example.smartresourceallocation.model.UpdateProfileRequest
import com.example.smartresourceallocation.model.UpdateProfileResponse
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.PUT
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.Part
import com.example.smartresourceallocation.model.UploadResponse
import com.example.smartresourceallocation.model.VerifyOtpRequest
import retrofit2.http.DELETE


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
            Response<ResourceListResponse>

    @GET("api/resources/my-resources")
    suspend fun getMyResources(

        @Header("Authorization")
        token: String

    ): Response<ResourceListResponse>

    @GET("api/resources/category/{category}")
    suspend fun getResourcesByCategory(

        @Path("category")
        category: String

    ): Response<ResourceListResponse>

    @POST("api/reservations")
    suspend fun createReservation(

        @Header("Authorization")
        token: String,

        @Body request: CreateReservationRequest

    ): Response<CreateReservationResponse>

    @GET("api/reservations/resource-owner")
    suspend fun getReservationsForMyResources(

        @Header("Authorization")
        token: String

    ): Response<ReservationListResponse>

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

    ): Response<ReservationListResponse>

    @GET("api/reservations")
    suspend fun getAllReservations(

        @Header("Authorization")
        token: String

    ): Response<ReservationListResponse>

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

    ): Response<ReservationResponse>



    @POST("api/resources")
    suspend fun createResource(

        @Header("Authorization")
        token:String,

        @Body
        request: CreateResourceRequest

    ):Response<ResourceResponse>

    @Multipart
    @POST("api/upload")
    suspend fun uploadImage(

        @Part
        image: MultipartBody.Part

    ): Response<UploadResponse>

    @GET("api/resources/{id}")
    suspend fun getResourceById(
        @Header("Authorization")
        token: String,

        @Path("id")
        resourceId: String
    ): Response<ResourceResponse>



    @PUT("api/resources/{id}")
    suspend fun updateResource(

        @Header("Authorization")
        token: String,

        @Path("id")
        resourceId: String,

        @Body
        request: CreateResourceRequest

    ): Response<ResourceResponse>

    @DELETE("api/resources/{id}")
    suspend fun deleteResource(

        @Header("Authorization")
        token: String,

        @Path("id")
        resourceId: String

    ): Response<Map<String,String>>

    @GET("api/analytics")
    suspend fun getAnalytics(

        @Header("Authorization")
        token: String

    ): Response<AnalyticsResponse>

    @GET("api/auth/profile")
    suspend fun getAdminProfile(

        @Header("Authorization")
        token: String

    ): Response<AdminProfileResponse>


    @GET("api/admin/profile-summary")
    suspend fun getAdminProfileSummary(

        @Header("Authorization")
        token: String

    ): Response<AdminSummaryResponse>

    @PUT("api/auth/profile")
    suspend fun updateProfile(

        @Header("Authorization")
        token: String,

        @Body
        request: UpdateProfileRequest

    ): Response<UpdateProfileResponse>

    @PUT("api/auth/change-password")
    suspend fun changePassword(

        @Header("Authorization")
        token: String,

        @Body
        request: ChangePasswordRequest

    ): Response<Map<String, String>>
    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(

        @Body request: ForgotPasswordRequest

    ):Response<Map<String,String>>

    @POST("api/auth/verify-otp")
    suspend fun verifyOtp(

        @Body request: VerifyOtpRequest

    ):Response<Map<String,String>>

    @POST("api/auth/reset-password")
    suspend fun resetPassword(

        @Body request: ResetPasswordRequest

    ):Response<Map<String,String>>

    @GET("api/resources/my-resources/category/{category}")
    suspend fun getMyResourcesByCategory(

        @Header("Authorization")
        token: String,

        @Path("category")
        category: String

    ): Response<ResourceListResponse>



}
