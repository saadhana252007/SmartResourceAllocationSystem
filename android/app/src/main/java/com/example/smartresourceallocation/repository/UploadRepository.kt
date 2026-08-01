package com.example.smartresourceallocation.repository

import okhttp3.MultipartBody
import com.example.smartresourceallocation.api.RetrofitInstance

class UploadRepository {

    suspend fun uploadImage(

        image: MultipartBody.Part

    ) = RetrofitInstance.api.uploadImage(image)

}