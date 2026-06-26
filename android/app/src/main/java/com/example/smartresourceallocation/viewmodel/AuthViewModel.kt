package com.example.smartresourceallocation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartresourceallocation.model.LoginResponse
import com.example.smartresourceallocation.model.RegisterResponse
import com.example.smartresourceallocation.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository =
        AuthRepository()

    val loginResponse =
        MutableLiveData<LoginResponse>()

    val registerResponse =
        MutableLiveData<RegisterResponse>()

    val errorMessage =
        MutableLiveData<String>()

    fun login(
        email: String,
        password: String
    ) {

        viewModelScope.launch {

            try {

                val response =
                    repository.login(
                        email,
                        password
                    )

                if (response.isSuccessful &&
                    response.body() != null
                ) {

                    loginResponse.value =
                        response.body()

                } else {

                    errorMessage.value =
                        response.errorBody()?.string()
                            ?: "Login Failed"


                }

            } catch (e: Exception) {

                errorMessage.value =
                    e.message

            }

        }

    }

    fun register(
        name: String,
        email: String,
        password: String,
        role: String
    ) {

        viewModelScope.launch {

            try {

                val response =
                    repository.register(
                        name,
                        email,
                        password,
                        role
                    )

                if (
                    response.isSuccessful &&
                    response.body() != null
                ) {

                    registerResponse.value =
                        response.body()

                }
                else {

                    errorMessage.value =
                        response.errorBody()?.string()
                            ?: "Registration Failed"

                }

            } catch (e: Exception) {

                errorMessage.value =
                    e.message

            }

        }

    }

}