package com.example.smartresourceallocation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartresourceallocation.repository.UploadRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class UploadViewModel : ViewModel() {

    private val repository =
        UploadRepository()

    val imageUrl =
        MutableLiveData<String?>()

    val error =
        MutableLiveData<String>()

    fun uploadImage(

        image: MultipartBody.Part

    ) {

        viewModelScope.launch {

            try {

                val response =
                    repository.uploadImage(image)

                if (

                    response.isSuccessful &&
                    response.body() != null

                ) {

                    imageUrl.value =
                        response.body()!!.imageUrl

                } else {

                    error.value =
                        response.errorBody()?.string()
                            ?: response.message()

                }

            } catch (e: Exception) {

                error.value =
                    e.localizedMessage
                        ?: "Something went wrong"

            }

        }

    }

}