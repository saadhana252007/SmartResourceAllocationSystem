package com.example.smartresourceallocation.repository

import com.example.smartresourceallocation.api.RetrofitInstance
import com.example.smartresourceallocation.model.ForgotPasswordRequest
import com.example.smartresourceallocation.model.LoginRequest
import com.example.smartresourceallocation.model.RegisterRequest
import com.example.smartresourceallocation.model.ResetPasswordRequest
import com.example.smartresourceallocation.model.VerifyOtpRequest

class AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ) = RetrofitInstance.api.login(
        LoginRequest(
            email,
            password
        )
    )

    suspend fun register(
        name: String,
        email: String,
        password: String,
        role: String
    ) = RetrofitInstance.api.register(
        RegisterRequest(
            name,
            email,
            password,
            role
        )
    )

    suspend fun forgotPassword(
        request: ForgotPasswordRequest
    ) = RetrofitInstance.api.forgotPassword(request)

    suspend fun verifyOtp(
        request: VerifyOtpRequest
    ) = RetrofitInstance.api.verifyOtp(request)

    suspend fun resetPassword(
        request: ResetPasswordRequest
    ) = RetrofitInstance.api.resetPassword(request)

}