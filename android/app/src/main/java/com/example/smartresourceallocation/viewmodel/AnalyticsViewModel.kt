package com.example.smartresourceallocation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartresourceallocation.model.AnalyticsResponse
import com.example.smartresourceallocation.repository.ResourceRepository
import kotlinx.coroutines.launch

class AnalyticsViewModel : ViewModel() {

    private val repository =
        ResourceRepository()

    val analytics =
        MutableLiveData<AnalyticsResponse>()

    val errorMessage =
        MutableLiveData<String>()

    fun getAnalytics(
        token: String
    ) {

        viewModelScope.launch {

            try {

                val response =
                    repository.getAnalytics(
                        token
                    )

                if (
                    response.isSuccessful &&
                    response.body() != null
                ) {

                    analytics.value =
                        response.body()

                }

                else {

                    errorMessage.value =
                        response.message()

                }

            }

            catch (e: Exception) {

                errorMessage.value =
                    e.localizedMessage

            }

        }

    }

}