package com.example.smartresourceallocation.utils

import android.content.Context

class SharedPrefManager(
    context: Context
) {

    private val prefs =
        context.getSharedPreferences(
            "SmartResourcePrefs",
            Context.MODE_PRIVATE
        )

    fun saveToken(token: String) {

        prefs.edit()
            .putString(
                "TOKEN",
                token
            )
            .apply()



    }

    fun getToken(): String? {

        return prefs.getString(
            "TOKEN",
            null
        )

    }

    fun clearToken() {

        prefs.edit()
            .remove("TOKEN")
            .apply()

    }
    fun saveRole(
        role: String
    ) {

        prefs.edit()
            .putString(
                "ROLE",
                role
            )
            .apply()

    }

    fun saveUserName(
        name: String
    ) {

        prefs.edit()
            .putString(
                "USER_NAME",
                name
            )
            .apply()

    }

    fun getUserName(): String? {

        return prefs.getString(
            "USER_NAME",
            null
        )

    }

    fun getRole(): String? {

        return prefs.getString(
            "ROLE",
            null
        )

    }
    fun clearRole() {

        prefs.edit()
            .remove("ROLE")
            .apply()

    }

}