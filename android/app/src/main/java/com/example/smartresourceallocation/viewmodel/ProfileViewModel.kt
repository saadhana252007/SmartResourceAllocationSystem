package com.example.smartresourceallocation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartresourceallocation.model.AdminProfileData
import com.example.smartresourceallocation.model.AdminSummaryResponse
import com.example.smartresourceallocation.model.ChangePasswordRequest
import com.example.smartresourceallocation.model.UpdateProfileRequest
import com.example.smartresourceallocation.model.UpdateProfileResponse
import com.example.smartresourceallocation.repository.AdminProfileRepository
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val repository = AdminProfileRepository()

    private val _profile = MutableLiveData<AdminProfileData>()
    val profile: LiveData<AdminProfileData> = _profile

    private val _summary = MutableLiveData<AdminSummaryResponse>()
    val summary: LiveData<AdminSummaryResponse> = _summary

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _updateProfile = MutableLiveData<UpdateProfileResponse>()
    val updateProfile : LiveData<UpdateProfileResponse> = _updateProfile

    private val _passwordChanged = MutableLiveData<Boolean>()

    val passwordChanged: LiveData<Boolean> = _passwordChanged


    fun loadProfile(
        token: String
    ) {

        viewModelScope.launch {

            _loading.value = true

            try {

                val response =
                    repository.getAdminProfile(token)

                if (response.isSuccessful &&
                    response.body() != null
                ) {

                    _profile.value =
                        response.body()!!.user

                } else {

                    _error.value = "Unable to load profile."

                }

            } catch (e: Exception) {

                _error.value =
                    e.localizedMessage ?: "Unknown Error"

            }

            _loading.value = false

        }

    }


    fun loadSummary(
        token: String
    ) {

        viewModelScope.launch {

            try {

                val response =
                    repository.getAdminProfileSummary(token)

                if (response.isSuccessful &&
                    response.body() != null
                ) {

                    _summary.value =
                        response.body()

                }

            } catch (e: Exception) {

                _error.value =
                    e.localizedMessage ?: "Unable to load summary"

            }

        }

    }
    fun updateProfile(

        token: String,

        request: UpdateProfileRequest

    ) {

        viewModelScope.launch {

            _loading.value = true

            try {

                val response = repository.updateProfile(

                    token,

                    request

                )

                if (

                    response.isSuccessful &&

                    response.body() != null

                ) {

                    _updateProfile.value =

                        response.body()

                } else {

                    _error.value =

                        "Update Failed"

                }

            } catch (e: Exception) {

                _error.value =

                    e.localizedMessage ?: "Something went wrong"

            }

            _loading.value = false

        }

    }
    fun changePassword(

        token: String,

        request: ChangePasswordRequest

    ) {

        viewModelScope.launch {

            _loading.value = true

            try {

                val response = repository.changePassword(

                    token,

                    request

                )

                if (response.isSuccessful) {

                    _passwordChanged.value = true

                } else {

                    _error.value =

                        "Current password is incorrect"

                }

            } catch (e: Exception) {

                _error.value =

                    e.localizedMessage ?: "Something went wrong"

            }

            _loading.value = false

        }

    }

}