package com.example.smartresourceallocation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartresourceallocation.model.Resource
import com.example.smartresourceallocation.repository.ResourceRepository
import kotlinx.coroutines.launch
import com.example.smartresourceallocation.model.BookingStatusResponse
import com.example.smartresourceallocation.model.CreateResourceRequest

class ResourceViewModel : ViewModel() {

    private val repository =
        ResourceRepository()

    val resources =
        MutableLiveData<List<Resource>>()

    val errorMessage =
        MutableLiveData<String>()

    val bookingStatus =
        MutableLiveData<BookingStatusResponse>()

    val createSuccess =
        MutableLiveData<Boolean>()

    val selectedResource =
        MutableLiveData<Resource>()

    val updateSuccess = MutableLiveData<Boolean>()

    val deleteSuccess = MutableLiveData<Boolean>()

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
                        response.body()!!.resources

                } else {

                    errorMessage.value =
                        response.errorBody()?.string()
                            ?: response.message()

                }

            } catch (e: Exception) {

                errorMessage.value =
                    e.localizedMessage ?: "Something went wrong"

            }

        }

    }

    fun getMyResources(

        token: String

    ) {

        viewModelScope.launch {

            try {

                val response =

                    repository.getMyResources(
                        token
                    )

                if (

                    response.isSuccessful &&
                    response.body() != null

                ) {

                    resources.value =
                        response.body()!!.resources

                }

                else {

                    errorMessage.value =
                        response.errorBody()?.string()
                            ?: response.message()

                }

            }

            catch (e: Exception) {

                Log.e(
                    "RESOURCE_ERROR",
                    "getMyResources failed",
                    e
                )

                errorMessage.value =
                    e.localizedMessage ?: "Unknown error"

            }

        }

    }
    fun getMyResourcesByCategory(

        token: String,

        category: String

    ) {

        viewModelScope.launch {

            try {

                val response = repository.getMyResourcesByCategory(

                    token,

                    category

                )

                if (

                    response.isSuccessful &&
                    response.body() != null

                ) {

                    resources.value =
                        response.body()!!.resources

                } else {

                    errorMessage.value =
                        response.errorBody()?.string()
                            ?: response.message()

                }

            } catch (e: Exception) {

                errorMessage.value =
                    e.localizedMessage ?: "Unknown error"

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
                        response.body()!!.resources

                } else {

                    errorMessage.value =
                        "Category API\nCode: ${response.code()}\n${response.errorBody()?.string()}"
                }

            }
            catch (e: Exception) {

                Log.e(
                    "RESOURCE_ERROR",
                    "getResourcesByCategory failed",
                    e
                )

                errorMessage.value =
                    e.localizedMessage ?: "Unknown error"

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
                    response.isSuccessful &&
                    response.body() != null
                ) {

                    bookingStatus.value =
                        response.body()

                } else {

                    errorMessage.value =
                        response.errorBody()?.string()
                            ?: response.message()

                }

            } catch (e: Exception) {

                errorMessage.value =
                    e.localizedMessage ?: "Something went wrong"

            }

        }

    }
    fun createResource(
        token: String,
        request: CreateResourceRequest
    ) {

        viewModelScope.launch {

            try {

                val response = repository.createResource(
                    token,
                    request
                )

                if (response.isSuccessful) {

                    createSuccess.value = true

                } else {

                    errorMessage.value =
                        response.errorBody()?.string()
                            ?: response.message()

                }

            } catch (e: Exception) {

                errorMessage.value =
                    e.localizedMessage ?: e.toString()

            }

        }


    }
    fun getResourceById(
        token: String,
        id: String
    ) {

        viewModelScope.launch {

            try {

                val response =
                    repository.getResourceById(
                        token,
                        id
                    )

                if (
                    response.isSuccessful &&
                    response.body() != null
                ) {

                    selectedResource.value =
                        response.body()!!.resource

                } else {

                    errorMessage.value =
                        response.errorBody()?.string()
                            ?: response.message()

                }

            }
            catch (e: Exception) {

                Log.e(
                    "RESOURCE_ERROR",
                    "getResourceById failed",
                    e
                )

                errorMessage.value =
                    e.localizedMessage ?: "Unknown error"

            }

        }

    }
    fun updateResource(
        token: String,
        id: String,
        request: CreateResourceRequest
    ) {

        viewModelScope.launch {

            try {

                val response = repository.updateResource(
                    token,
                    id,
                    request
                )

                if (response.isSuccessful) {

                    updateSuccess.value = true

                } else {

                    errorMessage.value =
                        "Code: ${response.code()}\n${response.errorBody()?.string()}"

                }

            } catch (e: Exception) {

                errorMessage.value =
                    e.localizedMessage ?: e.toString()

            }

        }

    }
    fun deleteResource(

        token: String,

        id: String

    ) {

        viewModelScope.launch {

            try {

                val response =
                    repository.deleteResource(
                        token,
                        id
                    )

                if (response.isSuccessful) {

                    deleteSuccess.value = true

                } else {

                    errorMessage.value =
                        response.errorBody()?.string()
                            ?: response.message()

                }

            } catch (e: Exception) {

                errorMessage.value =
                    e.localizedMessage ?: "Something went wrong"

            }

        }

    }

}