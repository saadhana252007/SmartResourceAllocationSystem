package com.example.smartresourceallocation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartresourceallocation.model.CreateReservationRequest
import com.example.smartresourceallocation.model.CreateReservationResponse
import com.example.smartresourceallocation.repository.ResourceRepository
import kotlinx.coroutines.launch
import com.example.smartresourceallocation.model.Reservation

class ReservationViewModel : ViewModel() {

    private val repository =
        ResourceRepository()

    val reservationResponse =
        MutableLiveData<CreateReservationResponse>()

    val reservations =
        MutableLiveData<List<Reservation>>()

    val errorMessage =
        MutableLiveData<String>()

    val cancelSuccess =
        MutableLiveData<String>()

    val updateSuccess =
        MutableLiveData<String>()

    val selectedReservation =
        MutableLiveData<Reservation>()



    fun createReservation(
        token: String,
        request: CreateReservationRequest
    ) {

        viewModelScope.launch {

            try {

                val response =
                    repository.createReservation(
                        token,
                        request
                    )

                if (
                    response.isSuccessful &&
                    response.body() != null
                ) {

                    reservationResponse.value =
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

    fun getMyReservations(
        token: String
    ) {

        viewModelScope.launch {

            try {

                val response =
                    repository.getMyReservations(
                        token
                    )

                if (
                    response.isSuccessful &&
                    response.body() != null
                ) {

                    reservations.value =
                        response.body()!!.reservations

                } else {

                    errorMessage.value =
                        response.message()

                }

            } catch (e: Exception) {

                errorMessage.value =
                    e.localizedMessage ?: "Something went wrong"

            }

        }

    }

    fun getAllReservations(

        token: String

    ) {

        viewModelScope.launch {

            try {

                val response =

                    repository.getAllReservations(token)

                if (

                    response.isSuccessful &&

                    response.body() != null

                ) {

                    reservations.value =
                        response.body()!!.reservations

                }

                else {

                    errorMessage.value =

                        response.errorBody()?.string()

                            ?: response.message()

                }

            }

            catch (e: Exception) {

                errorMessage.value =
                    e.localizedMessage ?: "Something went wrong"

            }

        }

    }
    fun getReservationsForMyResources(

        token: String

    ) {

        viewModelScope.launch {

            try {

                val response =

                    repository.getReservationsForMyResources(
                        token
                    )

                if (

                    response.isSuccessful &&
                    response.body() != null

                ) {

                    reservations.value =
                        response.body()!!.reservations

                }

                else {

                    errorMessage.value =
                        response.errorBody()?.string()
                            ?: response.message()

                }

            }

            catch (e: Exception) {

                errorMessage.value =
                    e.localizedMessage ?: "Something went wrong"

            }

        }

    }
    fun cancelReservation(
        token: String,
        reservationId: String
    ) {

        viewModelScope.launch {

            try {

                val response =
                    repository.cancelReservation(
                        token,
                        reservationId
                    )

                if(response.isSuccessful){

                    cancelSuccess.value =
                        "Reservation cancelled"

                }else{

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
    fun updateReservation(

        token: String,

        reservationId: String,

        request: CreateReservationRequest

    ) {

        viewModelScope.launch {

            try {

                val response =
                    repository.updateReservation(
                        token,
                        reservationId,
                        request
                    )

                if(response.isSuccessful){

                    updateSuccess.value =
                        "Reservation updated successfully"

                }else{

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
    fun getReservationById(

        token: String,

        reservationId: String

    ) {

        viewModelScope.launch {

            try {

                val response =
                    repository.getReservationById(
                        token,
                        reservationId
                    )

                if (
                    response.isSuccessful &&
                    response.body() != null
                ) {

                    selectedReservation.value =
                        response.body()!!.reservation

                } else {

                    errorMessage.value =
                        response.message()

                }

            } catch (e: Exception) {

                errorMessage.value =
                    e.localizedMessage ?: "Something went wrong"

            }

        }

    }

}