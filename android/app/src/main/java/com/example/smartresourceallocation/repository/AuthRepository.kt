package com.example.smartresourceallocation.repository

import com.example.smartresourceallocation.api.RetrofitInstance
import com.example.smartresourceallocation.model.LoginRequest
import com.example.smartresourceallocation.model.RegisterRequest

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

}