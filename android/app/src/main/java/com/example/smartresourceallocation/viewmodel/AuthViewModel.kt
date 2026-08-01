package com.example.smartresourceallocation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartresourceallocation.model.LoginResponse
import com.example.smartresourceallocation.model.RegisterResponse
import com.example.smartresourceallocation.repository.AuthRepository
import kotlinx.coroutines.launch
import com.example.smartresourceallocation.model.ForgotPasswordRequest
import com.example.smartresourceallocation.model.VerifyOtpRequest
import com.example.smartresourceallocation.model.ResetPasswordRequest
import android.util.Log
class AuthViewModel : ViewModel() {

    private val repository =
        AuthRepository()

    val loginResponse =
        MutableLiveData<LoginResponse>()

    val registerResponse =
        MutableLiveData<RegisterResponse>()

    val errorMessage =
        MutableLiveData<String>()

    val forgotPassword =
        MutableLiveData<Boolean>()

    val otpVerified =
        MutableLiveData<Boolean>()

    val passwordReset =
        MutableLiveData<Boolean>()

    var verifiedOtp = ""

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
                    e.localizedMessage ?: "Something went wrong"

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
                    e.localizedMessage ?: "Something went wrong"

            }

        }

    }
    fun forgotPassword(
        request: ForgotPasswordRequest
    ) {

        viewModelScope.launch {

            try {

                val response =
                    repository.forgotPassword(request)

                if (response.isSuccessful) {

                    forgotPassword.value = true

                } else {

                    errorMessage.value =
                        response.errorBody()?.string()
                            ?: "Failed to send OTP"

                }

            } catch (e: Exception) {

                errorMessage.value =
                    e.localizedMessage ?: "Something went wrong"

            }
        }

    }
    fun verifyOtp(
        request: VerifyOtpRequest
    ) {

        viewModelScope.launch {

            try {

                val response =
                    repository.verifyOtp(request)

                if (response.isSuccessful) {

                    verifiedOtp = request.otp

                    otpVerified.value = true

                } else {

                    errorMessage.value =
                        response.errorBody()?.string()
                            ?: "OTP Verification Failed"

                }

            } catch (e: Exception) {

                errorMessage.value =
                    e.localizedMessage ?: "Something went wrong"

            }

        }

    }
    fun resetPassword(
        request: ResetPasswordRequest
    ) {

        viewModelScope.launch {

            try {

                val response =
                    repository.resetPassword(request)

                if (response.isSuccessful) {

                    verifiedOtp = ""

                    passwordReset.value = true

                }else {

                    errorMessage.value =
                        response.errorBody()?.string()
                            ?: "Password Reset Failed"

                }

            } catch (e: Exception) {

                errorMessage.value =
                    e.localizedMessage ?: "Something went wrong"

            }

        }

    }


}