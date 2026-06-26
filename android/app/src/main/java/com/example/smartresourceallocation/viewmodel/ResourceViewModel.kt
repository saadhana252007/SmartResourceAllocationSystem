package com.example.smartresourceallocation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartresourceallocation.model.Resource
import com.example.smartresourceallocation.repository.ResourceRepository
import kotlinx.coroutines.launch
import com.example.smartresourceallocation.model.BookingStatusResponse

class ResourceViewModel : ViewModel() {

    private val repository =
        ResourceRepository()

    val resources =
        MutableLiveData<List<Resource>>()

    val errorMessage =
        MutableLiveData<String>()

    val bookingStatus =
        MutableLiveData<BookingStatusResponse>()

    fun getAllResources() {

        viewModelScope.launch {

            try {

                val response =
                    repository.getAllResources()

                if (
                    response.isSuccessful &&
                    response.body() != null
                ) {

                    resources.value =
                        response.body()

                } else {

                    errorMessage.value =
                        "Failed to fetch resources"

                }

            } catch (e: Exception) {

                errorMessage.value =
                    e.message

            }

        }

    }

    fun getResourcesByCategory(
        category: String
    ) {

        viewModelScope.launch {

            try {

                val response =
                    repository
                        .getResourcesByCategory(
                            category
                        )

                if (
                    response.isSuccessful &&
                    response.body() != null
                ) {

                    resources.value =
                        response.body()

                } else {

                    errorMessage.value =
                        "Failed to fetch resources"

                }

            } catch (e: Exception) {

                errorMessage.value =
                    e.message

            }

        }

    }
    fun getBookingStatus(
        resourceId: String,
        date: String
    ) {

        viewModelScope.launch {

            try {

                val response =
                    repository.getBookingStatus(
                        resourceId,
                        date
                    )

                if (
                    response.isSuccessful
                ) {

                    bookingStatus.value =
                        response.body()

                }

            } catch (e: Exception) {

                errorMessage.value =
                    e.message

            }

        }

    }

}