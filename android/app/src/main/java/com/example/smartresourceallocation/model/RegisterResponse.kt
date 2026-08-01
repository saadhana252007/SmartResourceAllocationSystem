package com.example.smartresourceallocation.model

data class RegisterResponse(
    val message: String,
    val token: String,
    val role: String,
    val name: String,
    val email: String,
    val createdAt: String
)