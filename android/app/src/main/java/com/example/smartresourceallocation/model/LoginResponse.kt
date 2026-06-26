package com.example.smartresourceallocation.model

data class LoginResponse(
    val message: String,
    val token: String,
    val role: String,
    val name: String
)