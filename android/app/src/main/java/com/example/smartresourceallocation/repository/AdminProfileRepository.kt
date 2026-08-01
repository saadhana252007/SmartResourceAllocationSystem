package com.example.smartresourceallocation.repository

import com.example.smartresourceallocation.api.RetrofitInstance
import com.example.smartresourceallocation.model.ChangePasswordRequest
import com.example.smartresourceallocation.model.UpdateProfileRequest

class AdminProfileRepository {

    suspend fun getAdminProfile(

        token:String

    )=RetrofitInstance.api.getAdminProfile(token)


    suspend fun getAdminProfileSummary(

        token:String

    )=RetrofitInstance.api.getAdminProfileSummary(token)

    suspend fun updateProfile(

        token: String,

        request: UpdateProfileRequest

    ) = RetrofitInstance.api.updateProfile(
        token,
        request
    )

    suspend fun changePassword(

        token: String,

        request: ChangePasswordRequest

    )=RetrofitInstance.api.changePassword(

        token,

        request

    )

}